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

public class Votlvcdr01 extends AbstractParser {
	
	private VotlvcdrModels model;
	
	public Votlvcdr01(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.model = (VotlvcdrModels)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, StandardMeasurementModel> modelMap = model.getModelMap();
		Map<String, RateModel> rateMap = model.getRateMap();
		ctx.setTableName("VOTLVCDR_TMP");
		
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
				//get negara & rating
				if(map.get("CALLEDPARTYID")!=null){
					String called = map.get("CALLEDPARTYID").toString();
					if(
							(called.length()>8 && !called.startsWith("7")) || //excude local
							(called.length()>7 && !called.startsWith("3")) ||
							(called.length()<=7 && called.startsWith("62")) 
					)
					for (int j=called.length();j>0;j--){
						String potong = called.substring(0,j);
						if(rateMap.get(potong)!=null){
							RateModel rm = rateMap.get(potong);
							map.put("RATE", rm.getRate());
							map.put("COUNTRYNAME", rm.getCountryName());
//							map.put("OPERATOR_NAME", rm.getOperatorName());
							map.put("OPERATOR_NAME", rm.getIdparametercountry());
							if(map.get("CALLDURATION")!=null){
								Double calldur = getDouble(map.get("CALLDURATION").toString());
								if(calldur!=null){
									Double rating = calldur/60*rm.getRate();
									map.put("RATING", rating);
								}
							}
							break;
						}
					}
				}
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
