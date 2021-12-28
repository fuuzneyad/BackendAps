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

import javax.xml.parsers.ParserConfigurationException;
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
import id.co.telkom.parser.entity.pm.ericsson.model.StructuredEricsson3GModel;

public class EricssonPM3GEriV10 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonPM3GEriV10.class);
	
	@SuppressWarnings("unchecked")
	public EricssonPM3GEriV10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
		try{
			loader.onBeginFile();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
				private String qName;
				private StandardMeasurementModel mapField =modelMap.get("default_mapping_field");
				private Map<String, String> meas = new LinkedHashMap<String, String>();
				private int measCounter=1;
				private String newsw, moParam, moValue,tabel="-----";
				private StructuredEricsson3GModel strEri = new StructuredEricsson3GModel();
				
				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException  {
					this.qName=qName.trim();
					
					super.startElement(uri, localName, qName, attributes);
				}
				@Override
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					super.endElement(uri, localName, qName);
				}
				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					String read =(new String(ch, start, length)).trim();
					
					if(qName.equals("cbt") && !read.equals("")){
						ctx.setDatetimeid(convertDate(read));
					}else if(qName.equals("nedn")&& !read.equals("")){
//						System.out.println("NeID="+read);
						ctx.setNe_id(read);
					}else if(qName.equals("nesw")&& !read.equals("")){
//						System.out.println(read);
						newsw=read;
					}else if(qName.equals("mts") && !read.equals("")){
						meas = new LinkedHashMap<String, String>();
						measCounter=1;
					}else if(qName.equals("gp") && !read.equals("")){
//						System.out.println("GP="+read);
						ctx.setGranularity(Integer.parseInt(read)>60?Integer.parseInt(read)/60:Integer.parseInt(read));
					}else if(qName.equals("mt") && !read.equals("")){
						meas.put("m"+measCounter, read);
						measCounter++;
					}else if(qName.equals("r") && !read.equals("")){
						//table tetentu aja yang diambil
							if(modelMap.get(tabel)!=null || cynapseProp.isGENERATE_SCHEMA_MODE())
							{
								//content
								String ctr=meas.get("m"+measCounter);
								if(ctr!=null){
		//							System.out.println(tabel+"=>"+meas.get("m"+measCounter)+"=="+read);
									String param = mapField.getFieldMap().get(tabel+"|"+ctr.toUpperCase());
									if( param==null){
										param=meas.get("m"+measCounter).toUpperCase();
									}
									param=param.trim().toUpperCase();
									
									String tidx=null;
									if(param.contains("|")){
										String[] splitted=param.split("\\|");
										param=splitted[0].trim();
										tidx=splitted[1].trim();
									}
									String tableIndexed=(tidx==null || tidx.equals("0")) ? tabel : tabel+"_"+tidx ;
									strEri.setMap(tableIndexed, param, read);
										
									if(cynapseProp.isGENERATE_SCHEMA_MODE())
										PutModel(tableIndexed, param, read);
									
								}
							}
							measCounter++;
					}
					else if(qName.equals("moid") && !read.equals("")){
						//ready model here
						if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !strEri.getStructuredModelEri().isEmpty()){
							for(Map.Entry<String,Map<String,Object>> mp : strEri.getStructuredModelEri().entrySet()){
								
								Map<String,Object> map = mp.getValue();
									map.put("NESW", newsw);
									map.put(moParam, moValue);
								ctx.setTableName(mp.getKey());
								loader.onReadyModel(map, ctx);
							}
							strEri = new StructuredEricsson3GModel();
						} 
						
						measCounter=1;
//						System.out.println("MoID="+read);
						ctx.setMo_id(read);
						if(read.contains(",") && read.contains("=")){
							String[] t_name=read.substring(read.lastIndexOf(",")+1).split("=");
							tabel=t_name[0].toUpperCase();
							moParam=t_name[0]; 
							moValue=t_name[1];
//							map.put(t_name[0].toUpperCase(), t_name[1]);
//							System.out.println(t_name[0]);
//							System.out.println("T_NAME="+t_name[0].toUpperCase()+"|"+t_name[1]);
						}
					}
					super.characters(ch, start, length);
				}
				
				
			};
			
//			saxParser.parse(fileloc, handler);
			InputStream inputStream= new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream,"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			reader.close();
			inputStream.close();
			
			
		} catch (SAXException e){
			System.out.println(e.getMessage());
		} catch (ParserConfigurationException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			loader.onEndFile();
		}
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"Ericsson3GSchema.sql";
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
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
//					System.out.println(entry.getKey()+"|||"+entry2.getValue()+"|||"+entry2.getValue().toString().length());
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n"; 
//					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "TEXT,\n"; 
//					String typeData = "VARCHAR (600),\n";
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
//					sb.append("\t`C"+counterId+"` "+typeData);
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
				gc.add(Calendar.HOUR, 7);
			return myFormat.format(gc.getTime());
		}catch(ParseException e){return null;}
	}
	
	
}
