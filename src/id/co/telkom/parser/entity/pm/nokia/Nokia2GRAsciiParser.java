package id.co.telkom.parser.entity.pm.nokia;

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
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class Nokia2GRAsciiParser extends AbstractParser{
	private static final Logger logger = Logger.getLogger(Nokia2GRAsciiParser.class);
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public Nokia2GRAsciiParser(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
	    FileInputStream fstream = null;
	    BufferedReader br = null;
	    try {
	        loader.onBeginFile();
	        fstream = new FileInputStream(file);
	        br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));

	        String tableName = null;

	        int line = 0;
	        String stringLine;
	        while ((stringLine = br.readLine()) != null)
	        {
	          if (stringLine.indexOf(",") > 0) {
	            String[] splitted = stringLine.split(",");

	            if (splitted.length >= 1) {
	              if ((line == 0) && (splitted.length > 3)) {
	                StandardMeasurementModel mdl = (StandardMeasurementModel)this.modelMap.get(splitted[1]);
	                if (mdl != null) {
	                  tableName = mdl.getTableName();
	                  ctx.setTableName(tableName);
	                } else {
	                  System.err.println("Cannot get Table Mapping for Measurement [" + splitted[1] + "]");
	                  logger.error("Cannot get Table Mapping for Measurement [" + splitted[1] + "]");
	                  throw new InterruptedException();
	                }

	                if (!this.cynapseProp.isGENERATE_SCHEMA_MODE()) {
	                  ctx.setNe_id(splitted[0]);
	                  ctx.setDatetimeid(convertDate(splitted[2]));
	                  int gran = Integer.parseInt(splitted[3].replace(";", ""));
	                  ctx.setGranularity(gran);
	                }
	              }
	              else if (isDouble(splitted[0])) {
	                ctx.setMo_id(stringLine.substring(0, stringLine.length() - 1));
	              }
	              else if (stringLine.endsWith(";")) {
	                if (this.cynapseProp.isGENERATE_SCHEMA_MODE()) {
	                  PutModel(tableName, splitted[0], splitted[1].replace(";", ""));
	                } else {
	                  String isi = "PERIOD_START_TIME|PERIOD_STOP_TIME|PERIOD_REAL_START_TIME|PERIOD_REAL_STOP_TIME".contains(splitted[0]) ? convertDate(splitted[1].replace(";", "")) : splitted[1].replace(";", "");
	                  map.put(splitted[0], isi);
	                }

	                if ((line != 1) && (!this.cynapseProp.isGENERATE_SCHEMA_MODE())) {
	                  if(ctx.mo_id==null)
	                	  ctx.setMo_id(ctx.ne_id);
	                  loader.onReadyModel(map, ctx);
	                  map = new LinkedHashMap<String, Object>();
	                }

	              }
	              else if (this.cynapseProp.isGENERATE_SCHEMA_MODE()) {
	                PutModel(tableName, splitted[0], splitted[1].replace(";", ""));
	              } else {
	                String isi = "PERIOD_START_TIME|PERIOD_STOP_TIME|PERIOD_REAL_START_TIME|PERIOD_REAL_STOP_TIME".contains(splitted[0]) ? convertDate(splitted[1].replace(";", "")) : splitted[1].replace(";", "");

	                map.put(splitted[0], isi);
	              }

	            }

	          }

	          line++;
	        }

	        this.cynapseProp.isGENERATE_SCHEMA_MODE();
	      }
	      catch (InterruptedException e)
	      {
	        System.err.println("Thread " + file.getName() + " are killed..");
	        logger.error("Thread " + file.getName() + " are killed..");

	        try {
	          br.close();
	          fstream.close();
	        } catch (IOException ie) {
	          System.err.println(ie.getMessage());
	        }
	      }
	      finally
	      {
	        loader.onEndFile();
	        try {
	          br.close();
	          fstream.close();
	        } catch (IOException e) {
	          System.err.println(e.getMessage());
	        }
	      }

	}
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	 @Override
	 public void CreateSchemaFromMap()
	  {
	    try {
	      String location = this.cynapseProp.getFILE_SCHEMA_LOC() + "Nokia2GSchema.sql";
	      System.out.println("Generating Schema to " + location + "..");
	      FileWriter out = new FileWriter(location);

	      StringBuilder sb = new StringBuilder();

	      for (Map.Entry<String, Map<String,String>> entry : this.tableModel.entrySet())
	      {
	        sb.append("DROP TABLE IF EXISTS " + this.cynapseProp.getTABLE_PREFIX() + (String)entry.getKey() + ";\n");
	        sb.append("CREATE TABLE " + this.cynapseProp.getTABLE_PREFIX() + (String)entry.getKey() + " (\n");
	        sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
	        sb.append("\t`SOURCE_ID` varchar(100) DEFAULT '',\n");
	        sb.append("\t`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',\n");
	        sb.append("\t`GRANULARITY` int(40) ,\n");
	        sb.append("\t`NE_ID` varchar(200) DEFAULT '',\n");
	        sb.append("\t`MO_ID` varchar(300) DEFAULT '',\n");

	        for (Map.Entry<String, String> entry2 : entry.getValue().entrySet())
	        {
	          if (((String)entry2.getKey()).length() > 30) {
	            System.err.println("warning field " + (String)entry2.getKey() + "'s  lenght >30, Mapping field is recommended!!");
	          }
	          String typeData = isDouble((String)entry2.getValue())? "DOUBLE DEFAULT NULL,\n" : "VARCHAR(" + ((String)entry2.getValue()).length() + 20 + "),\n";

	          typeData = "PERIOD_START_TIME|PERIOD_STOP_TIME|PERIOD_REAL_START_TIME|PERIOD_REAL_STOP_TIME".contains(entry2.getKey()) ? "DATETIME NULL DEFAULT NULL,\n" : typeData;
	          sb.append("\t`" + entry2.getKey() + "` " + typeData);
	        }
	        sb.setLength(sb.length() - 2);
	        sb.append("\n)Engine=MyIsam;\n");
	        out.write(sb.toString());
	        out.flush();
	        sb = new StringBuilder();
	      }

	      out.close();
	    } catch (IOException e) {
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
}
