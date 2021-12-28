package id.co.telkom.parser.entity.pm.prtg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class PrtgTransportV1 extends AbstractParser {
	private Map<String, StandardMeasurementModel> modelMap;
	private StandardMeasurementModel mapField;
	private Map<String, Object> maps = new LinkedHashMap<String, Object>();
	private static final Logger logger = Logger.getLogger(PrtgTransportV1.class);
	private String id;
	
	@SuppressWarnings("unchecked")
	public PrtgTransportV1(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.mapField =modelMap.get("default_mapping_field");
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			final Context ctx) throws Exception {
		
		loader.onBeginFile();
		StandardMeasurementModel mdl=null;
		String measType=null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		String[] arrs = file.getName().split("~");
		for (int i=0;i<arrs.length;i++){
			if(i==0){
				String ne_id = arrs[i].indexOf("-")>0 ? arrs[i].split("-")[1] : arrs[i];
				ctx.setNe_id(ne_id);
				measType = arrs[i].split("-")[0];
				mdl =modelMap.get(measType);
			}
			
		}
		
		id = arrs.length>0?(arrs[arrs.length-1]).split("\\.")[0]:"-";
		ctx.setMo_id(id);
		
		if(mdl==null){
			ctx.setTableName(measType.toUpperCase());
			logger.info("Measurement "+measType+" cannot be found in mapping config, using "+measType+" as table name applied.");
		}else{
			ctx.setTableName(mdl.getTableName());
		}
			factory.setValidating(false);
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
			private String qName;
			private Attributes attributes;
			private boolean isStartElement;
				@Override
				public void endElement(String uri, String localName,
						String qName)
						throws SAXException {
					super.endElement(uri, localName, qName);
					isStartElement=false;
				}

				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes) throws SAXException {
					super.startElement(uri, localName, qName, attributes);
					isStartElement=true;
					this.qName=qName;
					this.attributes=attributes;
				}

				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					super.characters(ch, start, length);
					String read =(new String(ch, start, length)).trim();
					//datetime
					if(this.qName.equals("datetime")&& isStartElement){
						if(read.contains("-")){
							String dateFrom=read.split("-")[0].trim();
							if(read.contains("PM"))
								ctx.setDatetimeid(convertDate(dateFrom,false/*true*/));//PM
							else
								ctx.setDatetimeid(convertDate(dateFrom,false));//AM
							ctx.setGranularity(15);
						}
					}else
					//value
					if((qName.equals("value") || qName.equals("value_raw")) && isStartElement){
						String p =getAttribute(attributes, "channel");
						if(ctx.t_name!=null && cynapseProp.isGENERATE_SCHEMA_MODE()){
							PutModel(ctx.t_name,p,read);
						}else if(ctx.t_name!=null && !cynapseProp.isGENERATE_SCHEMA_MODE() && mapField!=null){
							String ctr=mapField.getFieldMap().get(/*ctx.t_name+"|"+*/p);
							if(ctr!=null){
								if(qName.equals("value")){
									maps.put(ctr, read);
									parserInit.MergeRows(ctx.t_name+"|"+ctx.dateTimeid+"|"+ctx.ne_id+"|"+ctx.mo_id+"|"+ctx.granularity,maps);
								}else
								if(qName.equals("value_raw")){
									maps.put(ctr+"_RAW", read);
									parserInit.MergeRows(ctx.t_name+"|"+ctx.dateTimeid+"|"+ctx.ne_id+"|"+ctx.mo_id+"|"+ctx.granularity,maps);
									maps = new LinkedHashMap<String, Object>();
								}
							}
						}
					}
				}
				
				private String getAttribute(final Attributes attributes, final String attToGet){
					for (int i=0;i<attributes.getLength();i++){
						if(attToGet.equalsIgnoreCase(attributes.getQName(i)))
							return attributes.getValue(i);
					}
					return null;
				}
			};
		InputStream inputStream= new FileInputStream(file);
		Reader reader = new InputStreamReader(inputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		saxParser.parse(is, handler);
		reader.close();
		inputStream.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		loader.onBeginFile();
		for(Map.Entry<String, Object> m : parserInit.mapBuffer.entrySet() ){
			String[] ct = m.getKey().split("\\|");
			ctx.setTableName(ct[0]);
			ctx.setDatetimeid(ct[1]);
			ctx.setNe_id(ct[2]);
			ctx.setMo_id(ct[3]);
			ctx.setGranularity(getGranularity(ct[4]));
			
			@SuppressWarnings("unchecked")
			final Map<String, Object> map = (Map<String, Object>)m.getValue();
			loader.onReadyModel(map, ctx);
		}
		loader.onEndFile();
	}

	private static String convertDate(String val, boolean isConvert) {
		String format="MM/dd/yyyy hh:mm:ss a";
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			if(isConvert){
				Date d = fromUser.parse(val);
				Calendar gc = new GregorianCalendar();
					gc.setTime(d);
					//Timediff in HOUR..
					gc.add(Calendar.HOUR, 12);
					return myFormat.format(gc.getTime());
			}else
				return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return "0000-00-00 00:00:00";}
	}
	
	@Override
	public void CreateSchemaFromMap(){
		CreateMappingConfigFromMap();
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"PrtgSchemaTransport.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(400) DEFAULT NULL,\n");
				
				int counterId=1;
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n"; 
					sb.append("\t`C"+counterId+"` VARCHAR(30),\n");
					sb.append("\t`C"+counterId+"_RAW` "+typeData);
					counterId++;
				}
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=MyIsam;\n");
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
					
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	private void CreateMappingConfigFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"PrtgFieldMapping.cfg";
			System.out.println("Generating mapping config to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("\n#Table Group :"+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"\n");
				int counterId=1;
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					sb.append("["+/*entry.getKey()+"|"+*/entry2.getKey()+"][C"+counterId+"]\n");	
					counterId++;
				}
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
					
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	private int getGranularity(String s){
		try{
			int a=Integer.parseInt(s);
			return a;
		}catch(NumberFormatException e){
			return 0;
		}
	}
}
