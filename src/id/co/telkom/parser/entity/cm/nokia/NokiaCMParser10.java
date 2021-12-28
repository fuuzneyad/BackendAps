package id.co.telkom.parser.entity.cm.nokia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class NokiaCMParser10 extends AbstractParser{
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	public NokiaCMParser10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, final LoaderHandlerManager loader,
			final Context ctx) throws Exception {
				if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
					String bscOrRnc = file.getName().contains("BSC") ? "BSC" : file.getName().contains("RNC") ? "RNC" : "NOT_MATCH" ;
						
					String fName=file.getName();
					String ne_id=null;
					ne_id = fName.contains("_RC") ? fName.substring(fName.indexOf(bscOrRnc), fName.indexOf("_RC")):null;
					String dTime= fName.substring(fName.lastIndexOf("_")+1, fName.lastIndexOf("."));
					ctx.setNe_id(ne_id);
					ctx.setDatetimeid(convertDate(dTime));
				}
				
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
			private String qName, listToGet, meas;
			private String valueListToGet="";
			private Attributes attributes=null;
			
				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					this.qName=qName;
					this.attributes=attributes;
					
					if(qName.equals("log")){
					}else
					if(qName.equals("managedObject")){
						//ready insert
						if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
//							if(ctx.t_name!=null && ctx.t_name.equals("BTS") && ctx.mo_id.contains("127"))
//								System.out.println(ctx.t_name+","+ctx.mo_id+map);
							loader.onReadyModel(map, ctx);
							map = new LinkedHashMap<String, Object>();
						}
						//tableName
//						System.out.println(getAttribute("class")+"|"+getAttribute("version")+"|"+getAttribute("distName")+"|"+getAttribute("id"));
						ctx.setTableName(cynapseProp.getTABLE_PREFIX()+getAttribute("class"));
						ctx.setVersion(getAttribute("version"));
						ctx.setMo_id(getAttribute("distName"));
						if(ctx.t_name!=null && (ctx.t_name.equalsIgnoreCase(cynapseProp.getTABLE_PREFIX()+"BSC")||ctx.t_name.equalsIgnoreCase(cynapseProp.getTABLE_PREFIX()+"RNC"))){
							ctx.setNe_id(getAttribute("distName").replace("PLMN-PLMN/", "").replace("-", ""));
						}
						PutModel(ctx.t_name, null, null);
							
					}else
					if(qName.equals("list") && getAttribute("name")!=null){
						listToGet=getAttribute("name");
//						listToGet=attributes.getValue(0);
					}
					//here
					else
					if(qName.equals("p"))
						this.meas=getAttribute("name");
					
					super.startElement(uri, localName, qName, attributes);
				}

				@Override
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					
					if(qName.equals("list")){
						map.put("LIST", listToGet);
						map.put("LIST_VALUE",valueListToGet);
						listToGet=null;
						valueListToGet="";
					}
					
					super.endElement(uri, localName, qName);
				}

				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {

					String c =(new String(ch, start, length)).trim();
					
					if(this.qName.equals("p") && listToGet!=null && !c.equals("")){
						valueListToGet+=c+",";
					}else
					if(this.qName.equals("p") && this.meas!=null && !c.equals("")){
						String param=this.meas.toUpperCase().trim();
//						String param=attributes.getValue(0).trim();
						if(true){
							
							if(!cynapseProp.isGENERATE_SCHEMA_MODE() )
								map.put(param, c);else
									PutModel(ctx.t_name, param, c);
						}
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
			};
			
			InputStream inputStream= new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream,"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			reader.close();
			inputStream.close();
			//the last
			if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
				loader.onReadyModel(map, ctx);
				map = new LinkedHashMap<String, Object>();
			}
			loader.onEndFile();
			

	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		createSchemaMysql();
		createSchemaOracle();
	}
	
	private  void createSchemaMysql(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"NokiaMysqlCMSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("DROP TABLE "+entry.getKey()+";\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`LIST` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`LIST_VALUE` TEXT DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					if(entry2.getKey()!=null)
						sb.append("\t`"+entry2.getKey()+"` VARCHAR("+((entry2.getValue().length()+50))+"),\n");
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
	
	private  void createSchemaOracle(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"NokiaOracleCMSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\tENTRY_DATE DATE,\n");
				sb.append("\tSOURCE_ID VARCHAR2(100 BYTE),\n");
				sb.append("\tDATETIME_ID DATE,\n");
				sb.append("\tNE_ID VARCHAR2(100 BYTE),\n");
				sb.append("\tMO_ID VARCHAR2(200 BYTE),\n");
				sb.append("\tLIST VARCHAR2(200 BYTE),\n");
				sb.append("\tLIST_VALUE VARCHAR2(4000 BYTE),\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					if(entry2.getKey().length()>30)
						System.out.println("Table ["+entry.getKey()+"]["+entry2.getValue()+ "] length are more 30 character length, field mapping required!!");
					if(entry2.getKey()!=null)
						sb.append("\t"+entry2.getKey()+" VARCHAR2("+((entry2.getValue().length()+50))+" BYTE),\n");
				}
				sb.setLength(sb.length()-2);
				sb.append("\n);\n");
				
				out.write(sb.toString());	
				out.flush();
				sb = new StringBuilder();

			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	protected static String convertDate(String val) {
		String format;
		if(val.length()=="ddMMyy".length())
			format="ddMMyy";
		else
			format="yyyyMMdd";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		try{
			return myFormat.format(fromUser.parse(val)).trim();
		}catch(ParseException e){return val;}
	}

}
