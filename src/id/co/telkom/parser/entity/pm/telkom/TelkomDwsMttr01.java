package id.co.telkom.parser.entity.pm.telkom;

import java.io.File;
import java.io.FileReader;
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
import id.co.telkom.parser.entity.pm.tcel.TcelNetworkPerfExcel01;

public class TelkomDwsMttr01 extends AbstractParser {
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(TcelNetworkPerfExcel01.class);

	@SuppressWarnings("unchecked")
	public TelkomDwsMttr01(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader, Context ctx) throws Exception {
		logger.info(file.getAbsoluteFile());
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
		
		if(mdl!=null){
			ctx.setTableName(mdl.getTableName());
			loader.onBeginFile();
			FileReader fr = new FileReader(file.getAbsolutePath());
			CSVReader reader = new CSVReader(fr, ',');
			String [] splitted;
			while ((splitted = reader.readNext()) != null) {
				for(int i=0;i<splitted.length;i++){
					Object cl  =	mdl.getFieldMap().get(splitted[i]);
//					System.out.println(cl);
					if(cl!=null){
                			header.put("H"+i, cl.toString());
					}else {
						if(header.get("H"+i)!=null)
							map.put(header.get("H"+i), splitted[i]);
					}
				}
			
				if(!map.isEmpty()) {
					loader.onReadyModel(map, ctx);
					map = new LinkedHashMap<String, Object>();
				}
			
			}
			reader.close();
			fr.close();
			loader.onEndFile();
		}
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx) throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
	}

}
