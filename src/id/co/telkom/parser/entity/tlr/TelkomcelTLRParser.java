package id.co.telkom.parser.entity.tlr;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class TelkomcelTLRParser  extends AbstractParser{
	private Map<String, StandardMeasurementModel> modelMap;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private static final Logger logger = Logger.getLogger(TelkomcelTLRParser.class);
	
	@SuppressWarnings("unchecked")
	public TelkomcelTLRParser(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
		
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		StandardMeasurementModel mapField = this.modelMap.get("default_mapping_field");
		
		FileReader fr = new FileReader(file.getAbsolutePath());
		CSVReader reader = new CSVReader(fr, '|');
		String [] splitted;
		while ((splitted = reader.readNext()) != null) {
			String aspname=null;
			int pos=0;
			for(String s:splitted){
				pos++;
				map.put(mapField.getFieldMap().get(pos+""), s);
				if(mapField.getFieldMap().get(pos+"").equals("ASP_NAME")){
					aspname=s;
				}else
				if(mapField.getFieldMap().get(pos+"").equals("TO_")){
						if(s.startsWith("67073") || s.startsWith("67074"))
							map.put("FL", "ON NET");
						else
							map.put("FL", "OFF NET");
				}
				
			}
			//ready
			if(aspname!=null && this.modelMap.get(aspname)!=null){
				String tname=this.modelMap.get(aspname).getTableName();
				ctx.setTableName(tname);
				if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
					loader.onReadyModel(map, ctx);
				}else{
					for(Map.Entry<String, Object> m : map.entrySet()){
						PutModel(tname, m.getKey(), m.getValue().toString());
					}
				}
			}
			map = new LinkedHashMap<String, Object>();
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
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"TLRSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\tENTRY_DATE DATE,\n");
				sb.append("\tSOURCE_ID varchar2(100),\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					String typeTxt = entry2.getValue().toString().length() > 300 ? "TEXT,\n" : "VARCHAR2("+(entry2.getValue().toString().length()+20)+"),\n" ;
					String typeData = isDouble(entry2.getValue()) ? "NUMBER,\n" : typeTxt; 
					sb.append("\t"+entry2.getKey()+" "+typeData);
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
			logger.error(e);
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
}
