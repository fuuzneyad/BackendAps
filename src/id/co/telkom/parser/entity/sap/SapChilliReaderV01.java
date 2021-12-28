package id.co.telkom.parser.entity.sap;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class SapChilliReaderV01 extends AbstractParser{

	public SapChilliReaderV01(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		  FileInputStream fstream = new FileInputStream(file);
	      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	      String stringLine;
	      ctx.setTableName("payment_detail_chili_temp");
	      while ((stringLine = br.readLine()) != null)
	      {
	    	  
	    	  stringLine=stringLine.trim();
	    	  if(stringLine!=null && stringLine.startsWith("|") && !stringLine.contains("DocumentNo")){
	    		  map.put("ROWDATA", stringLine);
	    	  }
	    	  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
	    		  loader.onReadyModel(map, ctx);
	    	  
	    	  map = new LinkedHashMap<String, Object>();
	    		  
	      }
	      br.close();
	      fstream.close();
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
