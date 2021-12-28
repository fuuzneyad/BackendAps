package id.co.telkom.parser.entity.pm.ericsson;

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

public class EricssonPSCGgsnPgw extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonPSCGgsnPgw.class);
	
	@SuppressWarnings("unchecked")
	public EricssonPSCGgsnPgw(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		
		try{
			loader.onBeginFile();
			final StandardMeasurementModel mapField = this.modelMap.get("default_mapping_field");
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
				private String qName;
				private Attributes attributes;
				private String TABLE_NAME,MO_ID;
				private String buffMeas, buffHead;
				private Map<String, String> header =new LinkedHashMap<String, String>();
				private Map<String, Object> map =new LinkedHashMap<String, Object>();
				
				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException  {
					this.qName=qName.trim();
					this.attributes =attributes;
					
					if(this.qName.equals("fileSender")){
						//NE_ID & Type
						ctx.setNe_id(getAttribute("localDn"));
						ctx.setVersion(getAttribute("elementType"));
					}else//GRAN
					if(this.qName.equals("granPeriod")){
						ctx.setGranularity(getGran(getAttribute("duration").replace("PT", "").replace("S", "")));
						header =new LinkedHashMap<String, String>();
					}else//DATETIME_ID
					if(this.qName.equals("measCollec")){
						String beginTime=getAttribute("beginTime");
						if(beginTime!=null){
							beginTime=beginTime.replace("T", " ").substring(0, beginTime.indexOf("+"));
							ctx.setDatetimeid(beginTime);
						}
					}else//MO_ID
					if(this.qName.equals("managedElement")){
						MO_ID=getAttribute("localDn");
					}else
					if(this.qName.equals("measValue")){//MO_ID, TABLE_NAME
						String measObjLdn=getAttribute("measObjLdn");
						
						if(measObjLdn.indexOf(",")>0){
							TABLE_NAME=measObjLdn.substring(0,measObjLdn.indexOf(",")).toUpperCase();
							ctx.setMo_id(MO_ID+"/"+measObjLdn.substring(measObjLdn.indexOf(",")+1));
						}
						else{
							TABLE_NAME=measObjLdn.toUpperCase();
							ctx.setMo_id(MO_ID);
						}
						ctx.setTableName(TABLE_NAME.toUpperCase());
						
						for(Map.Entry<String, String> h : header.entrySet()){
							PutModel(TABLE_NAME, h.getValue(), "0");
						}
					}else//HEADER
					if(this.qName.equals("measType")){
						buffHead=getAttribute("p");
					}else//VALUE
					if(this.qName.equals("r")){
						buffMeas=getAttribute("p");
					}
					
					super.startElement(uri, localName, qName, attributes);
				}
				@Override
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					super.endElement(uri, localName, qName);
					if(qName.equals("measValue")&&!cynapseProp.isGENERATE_SCHEMA_MODE()){
						//ready model 
						loader.onReadyModel(map, ctx);
						map = new LinkedHashMap<String, Object>();
					}
				}
				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					String read =(new String(ch, start, length)).trim();
					
					if(this.qName.equals("measType") && !read.equals("")){
						header.put("H"+buffHead, read.toUpperCase());
					}else
					if(this.qName.equals("r") && !read.equals("")){
						String key = mapField.getFieldMap().get(TABLE_NAME+"|"+header.get("H"+buffMeas));
						if(!cynapseProp.isGENERATE_SCHEMA_MODE() && key!=null)
							map.put(key, read);
					}
					
					super.characters(ch, start, length);
				}
				
				private String getAttribute(String attToGet){
					for (int i=0;i<attributes.getLength();i++){
						if(attToGet.equalsIgnoreCase(attributes.getQName(i)))
							return attributes.getValue(i);
					}
					return null;
				}
				
				private int getGran(String gran){
					try{
						return Integer.parseInt(gran);
					}catch(NumberFormatException e){
						return 0;
					}
				}
			};
			
			InputStream inputStream= new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream,"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			reader.close();
			inputStream.close();
			
		} finally {
			loader.onEndFile();
		}
	}
	
	@Override
	public void CreateSchemaFromMap(){
		CreateMappingConfigFromMap();
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"EricssonPGwGgsnSchema.sql";
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
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR(80),\n"; 
					sb.append("\t`C"+counterId+"` "+typeData);
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
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"EricssonPGwGgsnMappings.cfg";
			System.out.println("Generating mapping config to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("\n#Table :"+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"\n");
				int counterId=1;
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					sb.append("["+entry.getKey()+"|"+entry2.getKey()+"][C"+counterId+"]\n");	
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
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}

	
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	protected static String convertDate(String val, int timeDiff) {
		val=val.replace("Z", "");
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date d = fromUser.parse(val);
			Calendar gc = new GregorianCalendar();
				gc.setTime(d);
				gc.add(Calendar.HOUR, timeDiff);
			return myFormat.format(gc.getTime());
		}catch(ParseException e){logger.error(e.getMessage());return null;}
	}
	
	
}
