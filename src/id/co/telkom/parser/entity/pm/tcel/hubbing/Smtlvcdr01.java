package id.co.telkom.parser.entity.pm.tcel.hubbing;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class Smtlvcdr01 extends AbstractParser {
	
	private VotlvcdrModels model;
	
	public Smtlvcdr01(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.model = (VotlvcdrModels)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, StandardMeasurementModel> modelMap = model.getModelMap();
		@SuppressWarnings("unused")
		Map<String, RateModel> rateMap = model.getRateMap();
		ctx.setTableName("SMTLVCDR_TMP");
		
		loader.onBeginFile();
		FileReader fr = new FileReader(file.getAbsolutePath());
		CSVReader reader = new CSVReader(fr, ',');
		String [] splitted;
		while ((splitted = reader.readNext()) != null) {
			for(int i=0;i<splitted.length;i++){
				if(modelMap.get(""+i)!=null){
					String col = modelMap.get(""+i).getTableName();
					map.put(col, splitted[i]);
				}
			}
			if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
				String df = file.getName().contains(".")?file.getName().split("\\.")[1]:null;
				map.put("DATEFILE", df);
				
				//get negara & rating untuk SMS tidak perlu
				loader.onReadyModel(map, ctx);
			}
			map= new LinkedHashMap<String, Object>();
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

	}
	
	@SuppressWarnings("unused")
	private Double getDouble(String s){
		if(s==null)
			return null;
		try{
			return Double.parseDouble(s);
		}catch(NumberFormatException e){
			return null;
		}
	}
	
}
