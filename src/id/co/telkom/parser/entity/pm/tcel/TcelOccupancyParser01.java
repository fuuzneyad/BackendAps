package id.co.telkom.parser.entity.pm.tcel;

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

public class TcelOccupancyParser01  extends AbstractParser{
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(TcelOccupancyParser01.class);
	
	@SuppressWarnings("unchecked")
	public TcelOccupancyParser01(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		logger.info("Processing "+file.getName());
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, String> header = new LinkedHashMap<String, String>();
		StandardMeasurementModel mdl=null;
		for (Map.Entry<String, StandardMeasurementModel>entry : this.modelMap.entrySet()){
			mdl = null ;
			if((StandardMeasurementModel)entry.getValue()!=null &&
				file.getName().matches(((StandardMeasurementModel)entry.getValue()).getMeasurementType())
			){
				 mdl=(StandardMeasurementModel)entry.getValue();
				 break;
			}
		}
		
		loader.onBeginFile();
		if(mdl!=null){
			ctx.setTableName(mdl.getTableName());
			FileReader fr = new FileReader(file.getAbsolutePath());
			CSVReader reader = new CSVReader(fr, '\t');
			String [] splitted;
			while ((splitted = reader.readNext()) != null) {
				for(int i=0;i<splitted.length;i++){
					String s=splitted[i];
					Object cl = mdl.getFieldMap().get(s);
					if(cl!=null){//get header
	                	header.put("u"+i, cl.toString());
	                }else
	                if(header.get("u"+i)!=null){
	                	String m = header.get("u"+i);
	                	map.put(m, s);
	                	if(cynapseProp.isGENERATE_SCHEMA_MODE())
	                		PutModel(mdl.getTableName(), m, s);
	                }
				}
				//ready model
				if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					loader.onReadyModel(map, ctx);
				map=new LinkedHashMap<String, Object>();
				
			}
			reader.close();
			fr.close();
			loader.onEndFile();
		}else
			System.err.println("No Mapping model for: "+file.getName());
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"OccupancySchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					
					String typeData = isDouble(entry2.getValue()) ? "NUMBER,\n" : "VARCHAR2("+(entry2.getValue().toString().length()+20)+"),\n"; 
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
