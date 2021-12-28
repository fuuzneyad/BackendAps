package id.co.telkom.parser.entity.pm.ericsson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class EricssonUnberCSCParser10 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonUnberCSCParser10.class);
	
	@SuppressWarnings("unchecked")
	public EricssonUnberCSCParser10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
		loader.onBeginFile();
		
		String fileName=file.getName();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
	    int indentNya = 0;
	    String isiHabisTag = "";
		@SuppressWarnings("unused")
		int barisNya = 0;
		FileInputStream fstream;
	    String collectionBeginTimeNya = "";
	    String granularityPeriodNya = "";
	    String measObjInstIdNya = ""; 
	    String suspectFlagNya = "";
	    String nEUserNameNya = "";
	    ArrayList<String> lsNamaKolomNya = new ArrayList<String>();
	    ArrayList<String> lsNilaiKolomNya = new ArrayList<String>();

	    String NELangsung = ""; String nmTabelLangsung = ""; String MOLangsung = ""; 
		try {
			fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
			String strLine;
		    while ((strLine = br.readLine()) != null) {      
			try
		          {
		            barisNya++;
		            indentNya = strLine.indexOf("<");
		            isiHabisTag = strLine.substring(indentNya);

		            if ((indentNya == 4) && (isiHabisTag.indexOf("T=\"[0]\"") > 0))
		            {
		              strLine = br.readLine(); barisNya++; strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		              strLine = strLine.replaceAll("&#x", "").replaceAll(";", "");

		              strLine = br.readLine(); barisNya++; strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));

		              strLine = br.readLine(); barisNya++; strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));

		              strLine = br.readLine(); barisNya++; strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));

		              strLine = br.readLine(); barisNya++; strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		              collectionBeginTimeNya = strLine.replaceAll("&#x", "").replaceAll(";", "");
		              collectionBeginTimeNya = collectionBeginTimeNya.replace("Z", "");

		              strLine = br.readLine(); barisNya++;
		              indentNya = strLine.indexOf("<");
		              isiHabisTag = strLine.substring(indentNya);
		            }
		            else if ((indentNya == 4) && (isiHabisTag.indexOf("T=\"[1]\"") > 0))
		            {
		              strLine = br.readLine(); barisNya++;
		              indentNya = strLine.indexOf("<");
		              isiHabisTag = strLine.substring(indentNya);

		              while (indentNya > 4)
		              {
		                if (indentNya == 8)
		                {
		                  nEUserNameNya = "";
		                  granularityPeriodNya = ""; measObjInstIdNya = ""; suspectFlagNya = "";
		                  lsNamaKolomNya.clear(); lsNilaiKolomNya.clear();

		                  strLine = br.readLine(); barisNya++;
		                  indentNya = strLine.indexOf("<");
		                  isiHabisTag = strLine.substring(indentNya);

		                  while (indentNya > 8) {
		                    if ((indentNya == 12) && (isiHabisTag.indexOf("T=\"[0]\"") > 0))
		                    {
		                      strLine = br.readLine(); barisNya++; strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                      nEUserNameNya = strLine.replaceAll("&#x", "").replaceAll(";", "");
		                      strLine = br.readLine(); barisNya++; strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                    }
		                    else if ((indentNya == 12) && (isiHabisTag.indexOf("T=\"[1]\"") > 0))
		                    {
		                      strLine = br.readLine(); barisNya++;
		                      indentNya = strLine.indexOf("<");
		                      isiHabisTag = strLine.substring(indentNya);

		                      while (indentNya > 12) {
		                        if (indentNya == 16)
		                        {
		                          granularityPeriodNya = ""; measObjInstIdNya = ""; suspectFlagNya = "";
		                          lsNamaKolomNya.clear(); lsNilaiKolomNya.clear();

		                          strLine = br.readLine(); barisNya++;
		                          indentNya = strLine.indexOf("<");
		                          isiHabisTag = strLine.substring(indentNya);

		                          while (indentNya > 16)
		                          {
		                            if ((indentNya == 20) && (isiHabisTag.indexOf("T=\"[0]\"") > 0))
		                            {
		                              strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                            }
		                            else if ((indentNya == 20) && (isiHabisTag.indexOf("T=\"[1]\"") > 0))
		                            {
		                              strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                              strLine = strLine.replaceAll("&#x", "").replaceAll(";", "");
		                              granularityPeriodNya = Integer.toString(Integer.parseInt(strLine, 16));
		                            }
		                            else if ((indentNya != 20) || (isiHabisTag.indexOf("T=\"[2]\"") <= 0))
		                            {
		                              if (indentNya == 24)
		                              {
		                                strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                                lsNamaKolomNya.add(strLine);
		                              }
		                              else if ((indentNya == 20) && (isiHabisTag.indexOf("T=\"[3]\"") > 0))
		                              {
		                                strLine = br.readLine(); barisNya++;
		                                indentNya = strLine.indexOf("<");
		                                isiHabisTag = strLine.substring(indentNya);

		                                while (indentNya > 20) {
		                                  if (indentNya == 24)
		                                  {
		                                    measObjInstIdNya = ""; suspectFlagNya = "";
		                                    lsNilaiKolomNya.clear();
		                                  }
		                                  else if ((indentNya == 28) && (isiHabisTag.indexOf("T=\"[0]\"") > 0))
		                                  {
		                                    strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                                    measObjInstIdNya = strLine;
		                                  }
		                                  else if ((indentNya == 28) && (isiHabisTag.indexOf("T=\"[1]\"") > 0))
		                                  {
		                                    strLine = br.readLine(); barisNya++;
		                                    indentNya = strLine.indexOf("<");
		                                    isiHabisTag = strLine.substring(indentNya);

		                                    while (indentNya > 28) {
		                                      if (indentNya == 32)
		                                      {
		                                        strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                                        strLine = nilaiNya(strLine);
		                                        lsNilaiKolomNya.add(strLine);
		                                      }

		                                      strLine = br.readLine(); barisNya++;
		                                      indentNya = strLine.indexOf("<");
		                                      isiHabisTag = strLine.substring(indentNya);
		                                    }

		                                    NELangsung = fileName.substring(fileName.indexOf("_") + 1);
		                                    if (NELangsung.indexOf(":") > 0){
		                                      NELangsung = NELangsung.substring(0, NELangsung.indexOf(":"));
		                                    }
		                                    else {
		                                      NELangsung = NELangsung.substring(0, NELangsung.indexOf("_"));
		                                    }

		                                    nmTabelLangsung = measObjInstIdNya.substring(0, measObjInstIdNya.indexOf("."));
		                                    MOLangsung = measObjInstIdNya.substring(measObjInstIdNya.indexOf(".") + 1).trim();
		                                    String NeNya=NELangsung.trim()+(nEUserNameNya.trim()!=null&&!nEUserNameNya.trim().equals("")?","+nEUserNameNya.trim():"");
		                                    
		                                    //READY MODEL IS BELLOW..
		                                    //<READY MODEL>..
		                                    //ambil table yang perlu aja
//		                                    System.out.println(nmTabelLangsung);
		                           
		                                    if((modelMap.get(nmTabelLangsung)!=null)){
				                                  //ready  
				  				    			  if(!cynapseProp.isGENERATE_SCHEMA_MODE() 
				  				    					  && (
				  				    							  (map.get("MO")!=null && !map.get("MO").equals(MOLangsung))||
				  				    							  (map.get("NE")!=null && !map.get("NE").equals(NeNya))||
				  				    							  (map.get("TABEL")!=null && !map.get("TABEL").equals(nmTabelLangsung))
				  				    						)){
				  				    				  map.remove("MO");
				  				    				  map.remove("NE");
				  				    				  map.remove("TABEL");
				  				    				  loader.onReadyModel(map, ctx);
				  				    				  map = new LinkedHashMap<String, Object>();
				  				    			  }
				  				    			  
				                                  map.put("MO", MOLangsung);
				                                  map.put("NE", NeNya);
				                                  map.put("TABEL", nmTabelLangsung);
				                                  ctx.setMo_id(MOLangsung);
				                                  ctx.setNe_id(NeNya);
				                                  ctx.setDatetimeid(convertDate(collectionBeginTimeNya));
				                                  int gran=convertGranularity(granularityPeriodNya);
				                                  ctx.setGranularity(gran>60?gran/60:gran);
				                                  ctx.setTableName(nmTabelLangsung);
		
		                                    }
		                                    //</READY MODEL>..
		                                    
		                                    
		                                    for (int i = 0; i < lsNamaKolomNya.size(); i++)
		                                    {
		                                    	if(cynapseProp.isGENERATE_SCHEMA_MODE()){
//		                                    		if(nmTabelLangsung.equals("C7SLSET"))
		                                    			System.out.print("\n\"" + collectionBeginTimeNya + "\",\"" + NELangsung.trim() + "\",\"" + nEUserNameNya.trim() + "\",\"" + MOLangsung.trim() + "\",\"" + granularityPeriodNya + "\",\"" + nmTabelLangsung + "\",\"" + lsNamaKolomNya.get(i).toString() + "\",\"" + lsNilaiKolomNya.get(i).toString() + "\"");
//		                                    		System.exit(1);
		                                    		PutModel(nmTabelLangsung, lsNamaKolomNya.get(i).trim(), lsNilaiKolomNya.get(i).trim());
		                                    	}else{
		                                    		String isi =lsNilaiKolomNya.get(i).trim();
		                                    			   isi=isi.equals("") ? null : isi;
		                                    		map.put(lsNamaKolomNya.get(i).trim(), isi);
		                                    	}
		                                    	
		                                    }
		                                    
		                                  }
		                                  else if ((indentNya == 28) && (isiHabisTag.indexOf("T=\"[2]\"") > 0))
		                                  {
		                                    strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		                                    suspectFlagNya = strLine;
		                                    if (suspectFlagNya.equals("&#x01;")) suspectFlagNya = "TRUE"; else suspectFlagNya = "FALSE";
		                                  }

		                                  strLine = br.readLine(); barisNya++;
		                                  indentNya = strLine.indexOf("<");
		                                  isiHabisTag = strLine.substring(indentNya);
		                                }
		                              }
		                            }

		                            strLine = br.readLine(); barisNya++;
		                            indentNya = strLine.indexOf("<");
		                            isiHabisTag = strLine.substring(indentNya);
		                          }

		                        }

		                        strLine = br.readLine(); barisNya++;
		                        indentNya = strLine.indexOf("<");
		                        isiHabisTag = strLine.substring(indentNya);
		                      }

		                    }

		                    strLine = br.readLine(); barisNya++;
		                    indentNya = strLine.indexOf("<");
		                    isiHabisTag = strLine.substring(indentNya);
		                  }

		                }

		                strLine = br.readLine(); barisNya++;
		                indentNya = strLine.indexOf("<");
		                isiHabisTag = strLine.substring(indentNya);
		              }

		            }
		            else if ((indentNya == 4) && (isiHabisTag.indexOf("T=\"[2]\"") > 0))
		            {
		              strLine = strLine.substring(strLine.indexOf(">") + 1); strLine = strLine.substring(0, strLine.indexOf("<"));
		            }
		          }
		          catch (Exception e0) {
		            System.out.println("e0:" + e0.toString());
		            e0.printStackTrace();
		          }

		      }
		    	//the last
			   if(!cynapseProp.isGENERATE_SCHEMA_MODE() && ctx.t_name!=null){
		    	   if(!map.isEmpty())
		    		   loader.onReadyModel(map, ctx);
				   loader.onEndFile();
			   }
		    br.close();
	        fstream.close();
		}catch (FileNotFoundException e1)
		      {
		        System.out.println("e1:" + e1.toString());
		      } catch (IOException e2) {
		        System.out.println("e2:" + e2.toString());
		      }
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"Ericsson2GSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(400) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n"; 
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

	
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	protected static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}
	
	private int convertGranularity(String gran){
		try{
			return Integer.parseInt(gran);
		}catch (NumberFormatException e){
			return 0;	
		}
	}
	
	private static String nilaiNya(String ini) {
	    ini.trim();
	    String str1 = "";
	    for (int i = 0; i < ini.length(); i++) {
	      if (ini.charAt(i) == '&') {
	        str1 = str1 + Character.toString(ini.charAt(i + 3)) + Character.toString(ini.charAt(i + 4));
	        i += 5;
	      } else {
	        str1 = str1 + Integer.toHexString(ini.charAt(i));
	      }
	    }
	    try
	    {
	      str1 = Long.toString(Long.parseLong(str1, 16));
	    } catch (Exception e) {
	      return "";
	    }
	    return str1;
	  }
}
