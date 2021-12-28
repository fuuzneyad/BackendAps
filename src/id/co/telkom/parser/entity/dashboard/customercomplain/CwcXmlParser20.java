package id.co.telkom.parser.entity.dashboard.customercomplain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.CharParserExtender;

public class CwcXmlParser20 extends AbstractParser{
	private static final String T_NAME="CWC_CUSTOMER_COMPLAINT";
	
	public CwcXmlParser20(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		ctx.setTableName(T_NAME);
		loader.onBeginFile();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		final CharParserExtender cst = new CharParserExtender(reader){};
		cst.read();
		cst.skipLines(1);
		StringBuilder sb = new StringBuilder();
		boolean isEndTag=false;
		String qName;
		while (!cst.isEOF()) {
			
			if(cst.isEOL())
				cst.skipEOL();else
			if(cst.isEqual('<')){//get the qName
				cst.read();
				cst.readUntil('>', sb);
				if(sb.toString().charAt(0)=='/'){
					qName = sb.toString().substring(1);
					isEndTag = true;
				}else{
					qName = sb.toString();
					isEndTag = false;
				}
				if(cst.isEqual('<')){
					cst.read();
				}
				if(!isEndTag ){
					sb.setLength(0);
					cst.readUntilIgnoreLine('<', sb);
					String character=sb.toString();
					qName=qName.toUpperCase();
					if(qName.equals("VALUE"))
						map.put("INCIDENT_NUMBER", character.replace("Incident_Number>", ""));
					if(qName.endsWith("_MEASUREMENT")||qName.endsWith("_DATE")||qName.endsWith("_TIME")){
						map.put(qName, character.replace("T", " "));
					}else
						map.put(qName, character);
					
				}else{
					cst.read();
					if(qName.equals("Value")){
						if(cynapseProp.isGENERATE_SCHEMA_MODE()){
							for(Map.Entry<String, Object> m :map.entrySet())
								PutModel(T_NAME, m.getKey(), m.getValue().toString());
							
							break;
						}else
							loader.onReadyModel(map, ctx);
						map = new LinkedHashMap<String, Object>();
					}
				}
			}else{
				cst.read();
			}
		}
		loader.onEndFile();
		reader.close();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"CWCSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					String typeTxt = entry2.getValue().toString().length() > 50 ? "TEXT,\n" : "VARCHAR("+(entry2.getValue().toString().length()+200)+"),\n" ;
					sb.append("\t`"+entry2.getKey()+"` "+typeTxt);
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

}
