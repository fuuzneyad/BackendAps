package id.co.telkom.parser.entity.pm.tcel;

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

public class SyniverseRoamingReport  extends AbstractParser{
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public SyniverseRoamingReport(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		ctx.setTableName("ROAMING_TEMP");
		Map<String, String> header = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		StandardMeasurementModel mapField = this.modelMap.get("default_mapping_field");
		
		
		loader.onBeginFile();
		FileReader fr = new FileReader(file.getAbsolutePath());
		CSVReader reader = new CSVReader(fr, ';');
		String [] splitted;
		
		int line =0;
		while ((splitted = reader.readNext()) != null) {
			line++;
			if(line==1){
				for(int i=0;i<splitted.length;i++){
					header.put("H"+i, splitted[i]);
				}
//				System.out.println(header);
			} else {
				for(int i=0;i<splitted.length;i++){
					String val =splitted[i].trim();
					boolean angka =false;
					String headReaded = mapField.getFieldMap().get(header.get("H"+i));
					if(headReaded!=null && headReaded.equals("NUMBER_OF_CALL"))
						angka=true;
					if(angka)
						val.replace("\\,", "");
					map.put(mapField.getFieldMap().get(header.get("H"+i)), val.equals("")?null:val);
				}
				loader.onReadyModel(map, ctx);
//				System.out.println(map);
				map = new LinkedHashMap<String, Object>();
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
		
	}

}
