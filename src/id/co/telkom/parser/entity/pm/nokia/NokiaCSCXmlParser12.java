package id.co.telkom.parser.entity.pm.nokia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class NokiaCSCXmlParser12 extends AbstractParser {
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(NokiaCSCXmlParser12.class);
	private FileInputStream fstream=null;
	private Reader reader=null;
	
	@SuppressWarnings("unchecked")
	public NokiaCSCXmlParser12(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
	    final StandardMeasurementModel mapField = this.modelMap.get("default_mapping_field");
	    try{
	    	fstream= new FileInputStream(file);
	    	reader = new InputStreamReader(fstream,"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			loader.onBeginFile();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
				private String qName;
				private Attributes attributes=null;
				private Map<String, Object> map = new LinkedHashMap<String, Object>();
				private StandardMeasurementModel mdl;
				boolean isMeas=false;
				
				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException  {
					this.qName=qName.trim();
					this.attributes=attributes;
					
					if(qName.equals("OMeS")){
						ctx.setVersion(getAttribute("xmlns"));
					}else
					if(qName.equals("PMTarget")){
						
						this.mdl=modelMap.get(getAttribute("measurementType"));
						isMeas=true;
						
						if(mdl!=null){
							ctx.setTableName(mdl.getTableName());
						}else{
							ctx.setTableName(null);
//							throw new SAXException("File "+fileName+" has been ignored because ["+getAttribute("measurementType")+"] cannot be found in measurement map");
						}
						
					}else
					if(qName.equals("PMSetup")){
						String dtime=getAttribute("startTime").replace("T", " ");
						ctx.setDatetimeid(dtime.substring(0, dtime.indexOf(".")));
						ctx.setGranularity(Integer.parseInt(getAttribute("interval")));
					}
					
					super.startElement(uri, localName, qName, attributes);
				}
				@Override
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					
					if(qName.equals("PMMOResult"))
					{
						//ready model
						if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
							String[] datas=ctx.mo_id!=null?ctx.mo_id.split("/") : null ;
							if(datas!=null)
							for (String x:datas){
								x=x.trim().toUpperCase();
								if(x.contains("MSC-")||x.contains("MGW-")||x.contains("GGSN-")){
									ctx.setNe_id(x);
									break;
								}

							}
							if( ctx.t_name!=null)
								loader.onReadyModel(map, ctx);
//							System.out.println(map);
							map = new LinkedHashMap<String, Object>();
						}
						
					}else if(qName.equals("PMTarget")){
						isMeas=false;
					}
					super.endElement(uri, localName, qName);
				}
				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					String read =(new String(ch, start, length)).trim();
					
					if(qName.equals("DN") && read!=null & !read.equals("")){
						Object mo = map.get("MO_ID");
						String moi = mo==null ? read.replace("PLMN-PLMN/", "") : mo.toString()+"/"+read.replace("PLMN-PLMN/", "");
						map.put("MO_ID", moi);
						ctx.setMo_id(moi);

					}else
					if(isMeas && read!=null & !read.equals("")){
//						System.out.println(qName+" "+read);
						String fieldName=null;
						if(mapField!=null){
							fieldName = mapField.getFieldMap().get(qName);
							if(fieldName==null){
//								System.err.println("Cannot find counter map for counter_id "+qName+"!!");
								fieldName=qName.toUpperCase();
							}
						}else
							fieldName=qName.toUpperCase();
						
						if(fieldName!=null && ctx.t_name!=null){
							if(cynapseProp.isGENERATE_SCHEMA_MODE()){
								PutModel(ctx.t_name, fieldName, read);
							}else
								map.put(fieldName.toUpperCase(), read);
						}
					}
					super.characters(ch, start, length);
				}
				
				protected String getAttribute(String attToGet){
					for (int i=0;i<attributes.getLength();i++){
						if(attToGet.equalsIgnoreCase(attributes.getQName(i)))
							return attributes.getValue(i);
					}
					return null;
				}
				
			};
			
			saxParser.parse(is, handler);
		}
	    catch (SAXException e)
	    {
	    	System.out.println(e.getMessage());
	    	logger.error(e.getMessage());
	    }
		finally {
			try {
				reader.close();
				fstream.close();
				if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					loader.onEndFile();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"NokiaCSCSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("DROP TABLE IF EXISTS "+entry.getKey()+";\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT '',\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT '',\n");
				sb.append("\t`MO_ID` varchar(300) DEFAULT '',\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+entry2.getValue().length()+20+"),\n"; 
					
					typeData="PERIOD_START_TIME|PERIOD_STOP_TIME|PERIOD_REAL_START_TIME|PERIOD_REAL_STOP_TIME".contains(entry2.getKey()) ? "DATETIME NULL DEFAULT NULL,\n" :typeData;
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
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

	
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	protected static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}
	
}
