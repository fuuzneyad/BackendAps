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

public class HourlyPerformanceCrs  extends AbstractParser{
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public HourlyPerformanceCrs(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		Map<String, String> header = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
//		StandardMeasurementModel mapField = this.modelMap.get("default_mapping_field");
		
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
		
		if (mdl!=null){
			ctx.setTableName(mdl.getTableName());
			int line =0;
			try {
				FileReader fr = new FileReader(file.getAbsolutePath());
				CSVReader reader = new CSVReader(fr, ',');
				String [] splitted;
				while ((splitted = reader.readNext()) != null) {
					line++;
					int ctrIdx=0;
					if(line==1){//the header
			    		  for (String x:splitted){
			    			  ctrIdx++;
			    			  x=x.trim();
			    			  header.put("X"+ctrIdx,x);
			    		  }
			    	  }else if(line!=2 && !cynapseProp.isGENERATE_SCHEMA_MODE()){//isi..
			    		  for (String x:splitted){
			    			  ctrIdx++;
			    			  x=x.trim();
		    				  String counter = mdl.getFieldMap().get(header.get("X"+ctrIdx));
			    			  if(counter!=null && !x.equals("")){
//			    				  System.out.println(counter+"="+x.trim());
				    				  map.put(counter, x);
				    			  }
			    		  }
			    		  map.put("FILENAME", file.getName());
			    		  loader.onReadyModel(map, ctx);
			    		  map = new LinkedHashMap<String, Object>();
			    	  }
			    }
				reader.close();
				fr.close();
			} 
			finally{
				loader.onEndFile();
			}
		}else
			System.out.println(file.getName()+" Not Matched!!");
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
	}

}
