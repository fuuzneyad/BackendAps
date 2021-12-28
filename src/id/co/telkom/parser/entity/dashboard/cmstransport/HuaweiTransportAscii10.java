package id.co.telkom.parser.entity.dashboard.cmstransport;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class HuaweiTransportAscii10 extends AbstractParser{

	public HuaweiTransportAscii10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		ctx.setTableName("HUAWEI_PFM");
		Map<String, String> header = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		FileReader fr = new FileReader(file.getAbsolutePath());
		CSVReader reader = new CSVReader(fr, '\t');
		String [] splitted;
		int line =0;
		while ((splitted = reader.readNext()) != null){
			line++;
			int ctrIdx=0;
			if(line==1){//the header
				for (String x:splitted){
					ctrIdx++;
					
					header.put("H"+ctrIdx, x.toUpperCase().trim());
				}
	    	}else{//data
	    		for (String x:splitted){
					ctrIdx++;
					map.put(header.get("H"+ctrIdx), x.trim());
					if(cynapseProp.isGENERATE_SCHEMA_MODE()){
						PutModel(ctx.t_name, header.get("H"+ctrIdx), x );
					}
				}
	    		Object neName = map.get("NENAME");
	    		if(neName!=null){
	    			//here
	    			String idNe = neName.toString().split("_")[0];
	    			Object BOARD_ID=map.get("BRDID");
	    			Object BOARD_NAME=map.get("BRDNAME");
	    			map.put("LOOKUP_NE",idNe+" "+BOARD_ID+"-"+BOARD_NAME);
	    			if(cynapseProp.isGENERATE_SCHEMA_MODE())
	    				PutModel(ctx.t_name,"LOOKUP_NE",idNe+" "+BOARD_ID+"-"+BOARD_NAME);
	    		}
	    		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
	    			loader.onReadyModel(map, ctx);
	    			map = new LinkedHashMap<String, Object>();
	    		}
	    	}
			
		}
		reader.close();
		fr.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiTransportPfnSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().length()+50)+"),\n"; 
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
	@SuppressWarnings("unused")
	private Integer toInt(String s){
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return 0;
		}
	}
}
