package id.co.telkom.parser.entity.cm.siemens;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class SiemensCMSymbolicName10 extends AbstractParser {

	public SiemensCMSymbolicName10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		String filename=file.getName().toUpperCase();
		
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
			ctx.setTableName(cynapseProp.getTABLE_PREFIX()+"SYMBNAME");
			ctx.setDatetimeid(convertDate(filename.substring(filename.lastIndexOf("_")+1)));
			Map<String, Object> map = new LinkedHashMap<String, Object>();  
			FileInputStream fstream = new FileInputStream(file);
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      while ((stringLine = br.readLine()) != null)
		      {
		    	  if(stringLine.indexOf(";")>0){
		    		  String[] splitted =stringLine.split(";");
		    		  map.put("OBJ_ID", splitted[0]);
		    		  map.put("OBJ_NAME", splitted[1]);
		    		  loader.onReadyModel(map, ctx);
		    		  map = new LinkedHashMap<String, Object>();
		    	  }
		      }
		      loader.onEndFile();
		      fstream.close();	
		}
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"Schema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
				
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+"SYMBNAME"+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+"SYMBNAME"+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`HASH_VAL` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`SOURCE` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`OBJ_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`OBJ_NAME` varchar(200) DEFAULT NULL,\n");
				
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=MyIsam;\n");
				
				out.write(sb.toString());	
				out.flush();
				sb = new StringBuilder();

			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	protected static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmm";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}

}
