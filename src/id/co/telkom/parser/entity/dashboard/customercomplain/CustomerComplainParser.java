package id.co.telkom.parser.entity.dashboard.customercomplain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;


import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.CharParserExtender;

public class CustomerComplainParser extends AbstractParser {
	private ParserPropReader cynapseProp;
	private static final Logger logger = Logger.getLogger(CustomerComplainParser.class);
	private static final String T_NAME="CUSTOMER_COMPLAIN";
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public CustomerComplainParser(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.cynapseProp=cynapseProp;
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
		loader.onBeginFile();
		
		ctx.setTableName(T_NAME);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		final CharParserExtender cst = new CharParserExtender(reader){};
		cst.read();
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String,String> header = new LinkedHashMap <String,String>();
		int idxHdr=1;
		while (!cst.isEOF()) {
			
			cst.readUntilWithoutLastChar('|', sb);
			String read =sb.toString();
			//header
			if(cst.getLine()<=2){
				header.put("H"+idxHdr, sb.toString().trim());
			}else{
				if(isDate(read.trim())){
					ctx.setDatetimeid(read);
					idxHdr=1;
					if(map!=null && !map.isEmpty()){
//						System.out.println(map);
						//filter, kalo ternyata ada foreign delimiter "|" ..
						String filterClose=map.get("close")!=null ? map.get("close").toString() : "SOMETHING WRONG!!";
						filterClose=map.get("CLOSE")!=null ? map.get("CLOSE").toString() : "SOMETHING WRONG!!";
						if(filterClose!=null && filterClose.length()==1)//y|n
							loader.onReadyModel(map, ctx);
						map = new LinkedHashMap<String, Object>();
					}
				}
				
				StandardMeasurementModel mdl = modelMap.get(header.get("H"+idxHdr));
				if(mdl!=null){
					 String fldName=mdl.getTableName();
					 String last = map.get(fldName)==null ?"":map.get(fldName).toString();
					 map.put(fldName, last+read);
				}
			}
			if(cst.isEOL()){
				cst.skipEOLs();
			}else
			if(cst.isEqual('|')){
				idxHdr++;
				cst.read();
			}else{
				System.out.println("why here??"+cst.getChar());
			}
			
		}
//		the last
		if(map!=null && !map.isEmpty()){
			loader.onReadyModel(map, ctx);
		}
		loader.onEndFile();
		reader.close();
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	private boolean isDate(String val) {
		String format="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		if(val.length()!=format.length())
			return false;
		try{
			fromUser.parse(val);
			return true;
		}catch(ParseException e){
			return false;
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
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"CustComplainSchema.sql";
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
				
				@SuppressWarnings("unused")
				int counterId=1;
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n"; 
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
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
	
}
