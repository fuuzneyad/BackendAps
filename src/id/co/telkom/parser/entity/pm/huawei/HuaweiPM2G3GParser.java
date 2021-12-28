package id.co.telkom.parser.entity.pm.huawei;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

public class HuaweiPM2G3GParser extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private static final Logger logger = Logger.getLogger(HuaweiPM2G3GParser.class);
	
	@SuppressWarnings("unchecked")
	public HuaweiPM2G3GParser(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {

			loader.onBeginFile();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
				private boolean isMeasTypes=false;
				private boolean isMeasValue=false;
				private boolean isMeasResults=false;
				@SuppressWarnings("unused")
				private boolean isMeasInfo=false;
				
				private boolean isSelectedMeas=false;
				private String qName=null;
				private String measTypes=null;
				private String measInfoId=null;
				private Attributes attributes=null;
				private String measTime=null;
				private StandardMeasurementModel mdl=null;
				
				public synchronized void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {
					
					this.attributes=attributes;
					this.qName=qName;
					
					if(this.qName.equalsIgnoreCase("managedElement")){
						ctx.setNe_id(getAttribute("userLabel"));
					}else					
					if(this.qName.equalsIgnoreCase("measInfo")){
						this.measInfoId=getAttribute("measInfoId");
						mdl = modelMap.get(this.measInfoId);
						if(this.measInfoId!=null && mdl!=null ){
							this.isSelectedMeas=true; 
							ctx.setTableName(mdl.getTableName());
						}	
							else this.isSelectedMeas=false;
						
						this.isMeasInfo=true;
					}else			
					if (this.qName.equalsIgnoreCase("measCollec")){
						this.measTime=getAttribute("beginTime");
						if(this.measTime!=null){
//							String delim = measTime.substring(measTime.length()-6).contains("+")?"+":"-";
//							ctx.setDatetimeid(measTime.replace("T", " ").substring(0,measTime.indexOf(delim)));
							ctx.setDatetimeid(measTime.substring(0,"2013-03-04T01:00:00".length()).replace("T", " "));
						}
					}else					
					if(this.qName.equalsIgnoreCase("measValue")){
						this.isMeasValue=true;
						ctx.setMo_id(getAttribute("measObjLdn"));
					}
					else			
					if(this.qName.equalsIgnoreCase("measResults"))
						this.isMeasResults=true;
					else					
					if(this.qName.equalsIgnoreCase("measTypes"))
						this.isMeasTypes=true;
					else
						if(this.qName.equalsIgnoreCase("repPeriod") || this.qName.equalsIgnoreCase("granPeriod")){
							int gran=getInteger(getAttribute("duration").replace("PT", "").replace("S", ""))/60;
							ctx.setGranularity(gran == 0 ? 60 : gran);
					}
				}
				
				public synchronized void endElement(String uri, String localName,
						String qName) throws SAXException {
					if(!cynapseProp.isGENERATE_SCHEMA_MODE() && qName.equalsIgnoreCase("measValue")  && isSelectedMeas){
						loader.onReadyModel(map, ctx);
						map = new LinkedHashMap<String, Object>();
					}else
					if(qName.equalsIgnoreCase("measInfo")  && isSelectedMeas){
						loader.onEndTable();
					}
				}
				
				public synchronized void characters(char ch[], int start, int length) throws SAXException {
					
					if(isSelectedMeas){
						
						if(this.isMeasTypes){
							this.measTypes=(new String(ch, start, length)).trim();
							this.isMeasTypes=false;
						}else
						if(this.isMeasValue){
							this.isMeasValue=false;
						}else
						if(isMeasResults){
							PutMeasurement((new String(ch, start, length)).trim());
							this.isMeasResults=false;
						}
					}
				}
				
				private String getAttribute(String attToGet){
					for (int i=0;i<attributes.getLength();i++){
						if(attToGet.equalsIgnoreCase(attributes.getQName(i)))
							return attributes.getValue(i);
					}
					return null;
				}
				private int getInteger(String s){
					try {
						return Integer.parseInt(s);
					}
					catch (Exception e){
						return 0;
					}
					
				}
				
				private void PutMeasurement(String theVal){
					if(measTypes!=null && theVal!=null && measTypes.length()>0){
						String[] arrayMeas = measTypes.split("\\s+");
						String[] arrayVal = theVal.split("\\s+");
						
						if (arrayMeas.length==arrayVal.length){
							for (int i=0; i<arrayMeas.length;i++){
								if(arrayVal[i].equalsIgnoreCase("NIL"))
									arrayVal[i]=null;
								
								if(isSelectedMeas){
									String param =mdl.getFieldMap()!=null ? mdl.getFieldMap().get(arrayMeas[i]):null;
									if(param==null)
										param ="C"+arrayMeas[i];
									
									if(!cynapseProp.isGENERATE_SCHEMA_MODE())
										map.put(param, arrayVal[i]);
									else 
										PutModel(cynapseProp.getTABLE_PREFIX()+mdl.getTableName(),param, arrayVal[i]);
								}
							}
						}
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

			loader.onEndFile();
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(300) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					String typeTxt = entry2.getValue().toString().length() > 300 ? "TEXT,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n" ;
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : typeTxt; 
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
			logger.error(e);
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
	
	public static void main(String[] args){
		String s = "2013-03-04T01:00:00+07:00";
		System.out.println(s.substring(0,"2013-03-04T01:00:00".length()));
		
	}
	
}
