package id.co.telkom.parser.entity.cm.huawei;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class HuaweiCMNodebParser extends AbstractParser {
	private Map<String, String> tableConverted = new LinkedHashMap<String, String>();
	public HuaweiCMNodebParser(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		tableConverted.put("SSL", "SSL_");
	}

	@Override
	protected void ProcessFile(final File file, final LoaderHandlerManager loader,
			final Context ctx) throws Exception {
		loader.onBeginFile();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser saxParser = factory.newSAXParser();
		DefaultHandler handler = new DefaultHandler() {
			private String qName,t_name;
			private String sft,nt;
			private boolean isStartElement;
			private boolean isClassStart, isAttributeStart;
			private Map<String, Object> map = new LinkedHashMap<String, Object>();

			@Override
			public void endElement(String uri, String localName,
					String qName)
					throws SAXException {
				super.endElement(uri, localName, qName);
				isStartElement=false;
				if(qName.equals("class")){
					isClassStart=false;
				}
				else
				if(qName.equals("attributes")){
					isAttributeStart=false;
					//ready
					if(!cynapseProp.isGENERATE_SCHEMA_MODE()){	
						map.put("SOFTWARE_VERSION", sft);
						map.put("NODEB_TYPE", nt.substring(0,7).trim());
						map.put("FILENAME", file.getName());
						loader.onReadyModel(map, ctx);						
						System.out.println(t_name+map);
						map=new LinkedHashMap<String, Object>();
					}
				}
			}

			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				super.startElement(uri, localName, qName, attributes);
				isStartElement=true;
				this.qName=qName;
				if(qName.equals("class")){
					isClassStart=true;
				}else
				if(qName.equals("attributes")){
					isAttributeStart=true;
					
					if(qName.equalsIgnoreCase("fileFooter")){
						isAttributeStart=true;	
						}
				}else
					if(isClassStart&&!isAttributeStart){
					t_name=tableConverted.get(qName.toUpperCase());
					t_name=t_name==null?qName.toUpperCase():t_name;
					ctx.setTableName(t_name);
					String mo = getAttribute(attributes, "id");
					ctx.setMo_id(mo);
//					mo=mo==null?"-":"id:/"+mo;
//					ctx.setMo_id(mo);
					ctx.setGranularity(60);

					}else if(qName.equals("spec:fileHeader")){
						 sft = getAttribute(attributes, "NRMversion");
						 nt = getAttribute(attributes, "NEversion");
//						ctx.setMo_id(nt);
						ctx.setNe_id(sft);
						ctx.setDatetimeid(convertDate(file.lastModified()));
				}
 				
			}
			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				super.characters(ch, start, length);
				String read =(new String(ch, start, length)).trim();
				if(isAttributeStart && !qName.equals("attributes") && isStartElement){
					if(qName.equalsIgnoreCase("Version"))
						ctx.setVersion(read);
					if(cynapseProp.isGENERATE_SCHEMA_MODE())
						PutModel(t_name, qName.toUpperCase(), read);
					else
						map.put(qName.toUpperCase(), read.trim());
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
		inputStream.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"NodeBCfg.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`FILENAME` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`SOFTWARE_VERSION` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`NODEB_TYPE` varchar(300) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+entry2.getValue().length()+20+"),\n"; 
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
	
	private static String convertDate(Long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		try{
			return format.format(date).toString();
		}catch(Exception e){
			return "0000-00-00 00:00:00";
		}
	}
}
