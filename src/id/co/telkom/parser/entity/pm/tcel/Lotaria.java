package id.co.telkom.parser.entity.pm.tcel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class Lotaria extends AbstractParser{

	public Lotaria(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
	}

	@SuppressWarnings("resource")
	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		 FileInputStream fstream=null;
	     BufferedReader br =null;
	  	  loader.onBeginFile();
		  fstream = new FileInputStream(file);
	      br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	      String stringLine;
	      String result = "";
	      ctx.setTableName("lotaria4D");
	      while (( stringLine = br.readLine()) != null)
	      {
//	    	  result = (stringLine);
	    	  if(stringLine.contains("SG_ddate")){
	    		  if(stringLine.contains("other\">")){
	    			  String s = stringLine.split("other\"\\>")[1].split("\\<")[0];
	    			  result += ("date "+s+"\n");
	    		  }
	    	  }else
	    	  if(stringLine.contains("1ST Prize")){
	    		  stringLine = br.readLine();
	    		  if(stringLine.contains("SG_t[0]\">")){
	    			  String s = stringLine.split("SG_t\\[0\\]\"\\>")[1].split("\\<")[0];
	    			  result += ("1st "+s+"\n");
	    		  }
	    	  }else
	    	  if(stringLine.contains("2ND Prize")){
		    	 stringLine = br.readLine();
		    	 if(stringLine.contains("SG_t[1]\">")){
		    	  String s = stringLine.split("SG_t\\[1\\]\"\\>")[1].split("\\<")[0];
		    	  result += ("2nd "+s+"\n");
		    	 }
		     }else
		      if(stringLine.contains("3RD Prize")){
				  stringLine = br.readLine();
				  if(stringLine.contains("SG_t[2]\">")){
					  String s = stringLine.split("SG_t\\[2\\]\"\\>")[1].split("\\<")[0];
					  result += ("3rd "+s+"\n");
				  }
			  }else
			  if(stringLine.contains("Special")){
				  result += ("Special"+"\n");
			 }else
			 if(stringLine.contains("SG_s[")){
				 String s = stringLine.split("\\]\"\\>")[1].split("\\<")[0];
				 if(!s.startsWith("--"))
					 result += (s+"\n");
			 }else
				  if(stringLine.contains("Consolation")){
					  result += ("Consolation"+"\n");
				 }else
			 if(stringLine.contains("SG_c[")){
				 String s = stringLine.split("\\]\"\\>")[1].split("\\<")[0];
				 if(!s.startsWith("--"))
					result += (s+"\n");
			}
	      }
	      System.out.println(result);
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
