package id.co.telkom.parser.entity.pm.siemens;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.pm.siemens.model.SiemensPMCommandModel;
import id.co.telkom.parser.entity.pm.siemens.model.StructuredSiemensModel;

public class SiemensPM2GParser extends AbstractParser{
	private static final Logger logger = Logger.getLogger(SiemensPM2GParser.class);
	private ParserPropReader cynapseProp;
	private Map<String, SiemensPMCommandModel> modelMap;
	private StructuredSiemensModel stm = new StructuredSiemensModel();
	private String tempObj; 
	private SiemensPMCommandModel mdl;
	private Map<String, String> objAdj = new LinkedHashMap<String, String>();
	
	@SuppressWarnings("unchecked")
	public SiemensPM2GParser(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, SiemensPMCommandModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
		  loader.onBeginFile();
		  FileInputStream fstream = new FileInputStream(file);
	      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	      String stringLine;
	      Integer sumAdj=1;
	      while ((stringLine = br.readLine()) != null)
	      {
	    	  String[] arrayRow = stringLine.replace("} ", "}\t").trim().split("\t");
	    	  int indexObj=-1;
	    	  int idxToGet=0;
	    	  String tempResult;
	    	  boolean isObj;
	    	  
		      for (int a=0; a<arrayRow.length; a++){
		    	  
		    	  if(arrayRow[a].contains("{")){
		    		  isObj=true;
    				  a=getObjPos(arrayRow,a);
    				  indexObj++;
    				  tempResult=tempObj;
    			  }else{
    				  isObj=false;
    				  tempResult=arrayRow[a];
    				  idxToGet++;
    			  }
		    	  
	    		  if(isObj){
	    			  tempResult=tempResult.replace(" ", "");
	    			  String mo=tempResult.contains("ptppkf")?tempResult.replace("ptppkf", "bts").replace(",0}", "}"):tempResult;
	    			  if(indexObj==0 && mdl!=null){
	    				  ctx.setMo_id(mo);
	    			  }else{
	    				  objAdj.put("OBJECT_ADJ_"+indexObj, mo);
	    			  }
	    		  }else{//non object
	    			  if(idxToGet==1){
	    				  //comment, nothing to do
	    			  }else
					  if(idxToGet==2){
						  mdl=modelMap.get(tempResult);
						  if(mdl!=null){
							  ctx.setTableName(mdl.getTable_name());
						  }
					  }else
					  if(idxToGet==3 && mdl!=null){
						  String DATETIME_ID=convertDate(tempResult.indexOf("+") > 0  ? tempResult.split("\\+")[0] : tempResult);
						  ctx.setDatetimeid(DATETIME_ID);
					  }else
					  if(idxToGet==4 && mdl!=null){
						  ctx.setVersion("BR"+tempResult);
					  }else
					  if(idxToGet==5 && mdl!=null){
						  tempResult=tempResult.indexOf("/") > 0  ? tempResult : tempResult+"/";
						  ctx.setNe_id(tempResult.split("/")[0]);
					  }else
					  if(idxToGet==6 && mdl!=null){
						  ctx.setGranularity(Integer.parseInt(tempResult));
					  }else	 
					  if(idxToGet==7 && mdl!=null){
						  sumAdj=countAdj(tempResult);
					  }else
	    			  if(mdl!=null &&(arrayRow[a].contains(" ") || idxToGet>10)){
	    				  String[] meases=arrayRow[a].split(" ");
	    				  double measCount=Math.floor(meases.length/sumAdj);
	    				  
	    				  if(sumAdj==1){
	    					  
	    					  int u=0;
	    					  Map<String, Object> mp=new LinkedHashMap<String, Object>();
	    					  String oAdj=objAdj==null || objAdj.get("OBJECT_ADJ_1")== null ? "-" : objAdj.get("OBJECT_ADJ_1").toString();
	    					  mp.put("OBJECT_ADJ", oAdj);
	    					  mp.put("SUM_ADJ",sumAdj);
	    					  
	    					  for (String meas:meases){
	    						  u++;
	    						  mp.put(mdl.getCounter_id()+"_"+(u), meas);
	    					  }
	    					  stm.setMap(ctx.t_name+"|"+ctx.granularity+"|"+ctx.dateTimeid, ctx.mo_id, oAdj,mp);
	    					  mp=new LinkedHashMap<String, Object>();
	    					  
	    				  } else {
	    				  
	    					  Map<String, Object> mp=new LinkedHashMap<String, Object>();
							  
	    					  int u=0;
	    					  int idxObj=0;
	    					  for (String meas:meases){
	    						  u++;
	    						  if(sumAdj==meases.length && sumAdj>1)
		    						  continue;
	    						  
	    						  mp.put(mdl.getCounter_id()+"_"+(u), meas); 
	    						  if(u%measCount==0){
	    							  idxObj++;
	    							  String obj = objAdj.get("OBJECT_ADJ_"+(idxObj)) == null ? "x" : objAdj.get("OBJECT_ADJ_"+(idxObj)).toString();
	    							  mp.put("OBJECT_ADJ", obj);
	    							  mp.put("SUM_ADJ",sumAdj);
	    						      stm.setMap(ctx.t_name+"|"+ctx.granularity+"|"+ctx.dateTimeid, ctx.mo_id, obj, mp);
	    						      mp=new LinkedHashMap<String, Object>();
	    						      u=0;
	    						  }
	    					  }
	    				  }
	    				  
	    			  }
	    		  }
		    }
	    	  objAdj = new LinkedHashMap<String, String>();
	      }
	      br.close();
	
			//here we go..
			for(Map.Entry<String,Map<String,Map<String,Map<String,Object>>>> tableMap : stm.getStructuredModelSiemens().entrySet()){
				String t =tableMap.getKey();
				if(t.indexOf("|")>0){
					String[] x = t.split("\\|");
					ctx.setTableName(x[0]);
					ctx.setGranularity(convertGran(x[1]));
					ctx.setDatetimeid(x[2]);
				}
					
				for(Map.Entry<String,Map<String,Map<String,Object>>> moIdMap : tableMap.getValue().entrySet()){
					String moId=moIdMap.getKey();
					for(Map.Entry<String,Map<String,Object>> objMap : moIdMap.getValue().entrySet()){
						Object adj=objMap.getValue().get("OBJECT_ADJ");
						ctx.setMo_id(adj!=null && !adj.toString().equals("-") ? moId+"/"+adj.toString():moId);
						loader.onReadyModel(objMap.getValue(), ctx);
					}
				}
			}
			
			loader.onEndFile();
		}
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		
		for (Map.Entry<String, SiemensPMCommandModel> entry : modelMap.entrySet()) {
		       SiemensPMCommandModel sss = (SiemensPMCommandModel)entry.getValue();
		       for(int i=1; i<=sss.getIdxCounter(); i++){
		       		PutModel(sss.getTable_name(), sss.getCounter_id()+"_"+i, "0");
		       }
		}
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"SiemensSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			logger.info("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
				
			StringBuilder sb = new StringBuilder();
				
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
					
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`VERSION` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`SUM_ADJ` integer(20) DEFAULT NULL,\n");
				sb.append("\t`OBJECT_ADJ` varchar(300) DEFAULT NULL,\n");
					
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
							
					if(entry2.getKey().length()>30)
							System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
							
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+entry2.getValue().length()+20+"),\n"; 
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
	 
	
		private int getObjPos(String[] arr, int pos){
			String res="";
			while(!arr[pos].contains("}")){
				res+=arr[pos]+",";
				pos++;
			}
			tempObj=res+arr[pos]; 
			return pos;
		}
		
		private static boolean isDouble(String s){
			try{
				Double.parseDouble(s);
				return true;
			}catch (NumberFormatException e){
				return false;
			}
		}
		
		private static int convertGran(String s){
			try{
				return Integer.parseInt(s);
			}catch (NumberFormatException e){
				return 60;
			}
		}
		
		private static Integer countAdj(String s){
			try{
				return Integer.parseInt(s);
			}catch (NumberFormatException e){
				return 1;
			}
		}
		
		private static String convertDate(String val) {
			String format;
			if(val.length()=="dd/MM/yyyy HH:mm:ss".length())
				format="dd/MM/yyyy HH:mm:ss";else
			if(val.length()=="dd/MM/yyyy HH:mm".length())
				format="dd/MM/yyyy HH:mm";else
			format="dd/MM/yyyy HH:mm:ss";
			
			SimpleDateFormat fromUser = new SimpleDateFormat(format);
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try{
				return myFormat.format(fromUser.parse(val));
			}catch(ParseException e){return val;}
		}
}
