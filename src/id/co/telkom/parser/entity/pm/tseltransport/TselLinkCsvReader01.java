package id.co.telkom.parser.entity.pm.tseltransport;

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
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class TselLinkCsvReader01 extends AbstractParser{
	private static final Logger logger = Logger.getLogger(TselLinkCsvReader01.class);

	public TselLinkCsvReader01(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader, Context ctx) throws Exception {
		
		FileReader fr = new FileReader(file.getAbsolutePath());
	  	CSVReader reader = new CSVReader(fr, ',');
	  	Map<String,String> header = new LinkedHashMap <String,String>();
	  	Map<String,Object> map = new LinkedHashMap <String,Object>();
	  	try{	
  	  	  loader.onBeginFile();
  	  	  int line =0;
		      String t_name = file.getName().split("\\.")[0];
		      t_name=t_name.toUpperCase().replace("-", "_").replace(" ", "_").replace("__", "_");
		      
		      ctx.setTableName(t_name);
		      String [] splitted;
			  while ((splitted = reader.readNext()) != null) {
			    	  line++;
			    	  
			    	  for(int i=0;i<splitted.length ; i++) {
			    		  if(line==1) {
			    			  String fld =splitted[i];
			    			  if(fld!=null ) {
			    					 fld=fld.replace("#", "").replace("-", "_").replace("__", "_").replace("'", "").replace(",", "").replace(" ", "_");
			    					 fld = fld.toUpperCase();
			    					 fld = fld.replaceAll("LONG", "LONG_");
			    					 fld = fld.replaceAll("AS", "AS_");
			    			  }
			    			  
			    			  header.put("H"+i, fld);
			    		  }else {
			    			  if(header.get("H"+i)!=null)
				    			 if(cynapseProp.isGENERATE_SCHEMA_MODE()) 
				    				 PutModel(t_name, header.get("H"+i), splitted[i]);
				    			 else 
				    				 map.put(header.get("H"+i), splitted[i]);
				    			 
			    			 
			    		  }
			    	  }
			    	  if(!map.isEmpty()&&!cynapseProp.isGENERATE_SCHEMA_MODE()) {
//			    		 System.out.println(map); 
	    				 loader.onReadyModel(map, ctx);
	    				 map = new LinkedHashMap <String,Object>();
			    	  }

			  }
	  	} finally {
  		  loader.onEndFile();
		      try{
			    	  reader.close();
			    	  fr.close();
		      } catch (Exception e){
		    	  logger.error(e.getMessage());
		    	  e.printStackTrace();
		    	  System.err.println(e.getMessage());
		      }
		}
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx) throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"/TselLinkSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey()!=null && entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = "VARCHAR("+entry2.getValue().length()+20+"),\n"; 
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
	

}
