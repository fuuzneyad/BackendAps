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

public class PrtgServoV1 extends AbstractParser {
	private Map<String, StandardMeasurementModel> modelMap;
	private String tableName;
	private StandardMeasurementModel mapField;
	Map<String, Object> maps = new LinkedHashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public PrtgServoV1(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.mapField =modelMap.get("default_mapping_field");
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			final Context ctx) throws Exception {
		
		loader.onBeginFile();
		SAXParserFactory factory = SAXParserFactory.newInstance();
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
				StandardMeasurementModel mdl = modelMap.get(qName);
				//table
				if(mdl!=null && isStartElement){
//					System.out.println(mdl.getTableName()+"-->"+read);
					tableName=(mdl.getTableName()+"_"+read.toUpperCase().replace(".", ""));
				}
				//datetime
				if(this.qName.equals("datetime")&& isStartElement){
					if(read.contains("-")){
						String dateFrom=read.split("-")[0];
						if(read.contains("PM"))
							ctx.setDatetimeid(convertDate(dateFrom,true));//PM
						else
							ctx.setDatetimeid(convertDate(dateFrom,false));//AM
						ctx.setGranularity(15);
						//System.out.println(dateFrom+" "+ctx.dateTimeid);
					}
				}else
				if(qName.equalsIgnoreCase("Source") && isStartElement){
					ctx.setNe_id(read);
				}
				//mo_id
				if(qName.equals("id") && isStartElement){
//					ctx.setMo_id((ctx.ne_id!=null?ctx.ne_id+"/":"")+"ID="+read);v2
					ctx.setMo_id(ctx.ne_id);
				}
				//value
				if((qName.equals("value") || qName.equals("value_raw")) && isStartElement){
					String p =getAttribute(attributes, "channel");
//					System.out.println(">>>"+p+" =["+read+"]");
					if(tableName!=null && cynapseProp.isGENERATE_SCHEMA_MODE()){
						PutModel(tableName,p,read);
					}else if(tableName!=null && !cynapseProp.isGENERATE_SCHEMA_MODE() && mapField!=null){
						ctx.setTableName(tableName);
						String ctr=mapField.getFieldMap().get(tableName+"|"+p);
						if(ctr!=null){
//							if(qName.equals("value"))//ga usah
//								maps.put(ctr, read);
							if(qName.equals("value_raw"))
								maps.put(ctr+"_RAW", read);
//							System.out.println(tableName+"|"+qName+"|"+ctr+">>"+read);
						}
					}
						
				}
			}
			
			private String getAttribute(Attributes attributes,String attToGet){
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
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
//			System.out.println(">>"+maps);
			loader.onReadyModel(maps, ctx);
		}
		inputStream.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
	}

	private String convertDate(String val, boolean isConvert) {
		String format="MM/dd/yyyy hh:mm:ss";
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
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"PrtgSchema.sql";
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
					
//					System.out.println(entry.getKey()+"|||"+entry2.getValue()+"|||"+entry2.getValue().toString().length());
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n"; 
//					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "TEXT,\n"; 
//					String typeData = "VARCHAR (600),\n";
//					sb.append("\t`"+entry2.getKey()+"` "+typeData);
					sb.append("\t`C"+counterId+"` "+typeData);
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
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"PrtgMappings.cfg";
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
}
