package id.co.telkom.parser.common.tableoutput;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import id.co.telkom.parser.common.model.DBMetadata;
import id.co.telkom.parser.common.model.DBTableModel;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;

import java.util.Calendar;
import java.util.Map;

public class MetadataOperation {
	private ParserPropReader cynapseProp;
	private OutputMethodPropReader om ;
	private DataSource ds;
	
	public MetadataOperation(ParserPropReader cynapseProp, OutputMethodPropReader om, DataSource ds ){
		 this.cynapseProp=cynapseProp;
		 this.om=om;
		 this.ds=ds;
	}
	
	public DBTableModel GetDBTableModel(){
		DBTableModel  dbModel = null;
		if(om.getMETADATA_METHOD().equalsIgnoreCase("JDBC")){
		System.out.println("Reading Metadata from JDBC..");
		dbModel = new DBTableModel();
		 try{
			    Connection conn = ds.getConnection();
			    DatabaseMetaData mtdt = conn.getMetaData();
			    
			    ResultSet rsColum = mtdt.getColumns(conn.getCatalog(), om.getMETADATA_JDBC_SCHEMA(), om.getMETADATA_JDBC_TABLE(), "%");
			    ResultSetMetaData rsmd = rsColum.getMetaData();
			    int numColsClm = rsmd.getColumnCount();

			    String[] header = new String[numColsClm+2]; 
			    for (int i = 1; i <= numColsClm; i++) {
			    	 header[i]=rsmd.getColumnLabel(i);
				}
				    
				while (rsColum.next()) {
				    DBMetadata meta = new DBMetadata();
				    
				      for (int i = 1; i <= numColsClm ; i++) {
					        if(header[i].equalsIgnoreCase("TABLE_CAT")){
					        	meta.setTABLE_CAT(rsColum.getString(i));
					        }else
					        if(header[i].equalsIgnoreCase("TABLE_SCHEM")){
						        meta.setTABLE_SCHEM(rsColum.getString(i));
						    }else
						    if(header[i].equalsIgnoreCase("TABLE_NAME")){
							    meta.setTABLE_NAME(rsColum.getString(i));
							}else	
						    if(header[i].equalsIgnoreCase("DATA_TYPE")){
							    meta.setDATA_TYPE(rsColum.getString(i));
							}else	
							if(header[i].equalsIgnoreCase("COLUMN_NAME")){
								meta.setCOLUMN_NAME(rsColum.getString(i));
							}	
							if(header[i].equalsIgnoreCase("DATA_TYPE")){
								meta.setDATA_TYPE(rsColum.getString(i));
							}else	
							if(header[i].equalsIgnoreCase("TYPE_NAME")){
								meta.setTYPE_NAME(rsColum.getString(i));
							}else	
							if(header[i].equalsIgnoreCase("COLUMN_SIZE")){
								meta.setCOLUMN_SIZE(rsColum.getString(i));
							}
				      }
				      meta.setDATABASE_PRODUCT(mtdt.getDatabaseProductName());
				      dbModel.PutModel(meta.getTABLE_NAME().toUpperCase(), meta.getCOLUMN_NAME(), meta);
				}
			    conn.close();
			 } catch (Exception e){
				 e.printStackTrace();
			 }
		}else if(om.getMETADATA_METHOD().equalsIgnoreCase("FILE")){
			dbModel = new DBTableModel();
			System.out.println("Reading Table Metadata from FILE "+om.getMETADATA_FILELOC()+"...");
			try {
				  FileInputStream fstream = new FileInputStream(om.getMETADATA_FILELOC());
			      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
			      String stringLine;
			      DBMetadata meta = new DBMetadata();
			      while (( stringLine = br.readLine()) != null)
			      {
			    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
			    		  String[] arr = stringLine.split("\\[*\\]");
			    		  for (int x=0; x<arr.length; x++){
			    			  String s=arr[x].trim().replace("[", "").trim();
			    			  if(x==0){
			    				  meta.setTABLE_NAME(s);
			    			  }else
			    			  if(x==1){
			    				  meta.setCOLUMN_NAME(s);
				    		  }else
				    		  if(x==2){
				    			  meta.setTYPE_NAME(s);
					    	  }else
					    	  if(x==3){
					    		  meta.setCOLUMN_SIZE(s);
						      }
			    		  }
			    		  dbModel.PutModel(meta.getTABLE_NAME().toUpperCase(), meta.getCOLUMN_NAME().toUpperCase(), meta);
			    		  meta = new DBMetadata();
			    	  }	  
			      }
			      br.close();
			}catch (IOException  e){
				  e.printStackTrace();
			}
		}
		if(dbModel==null)
			System.err.println("Warning! Reading Table Output Metadata Problem, Please Check the Config!!");
		return dbModel;
	}
	
	public void WriteMetaDataToFile(DBTableModel dbTableModel){
		String location=om.getMETADATA_FILELOC();
		FileWriter out;
		System.out.println("Write Metadata Table to "+location+"..");
		try {
			String waktu =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			out = new FileWriter(location);
			out.write("############################################################\n");
			out.write("#DB Metadata File For "+cynapseProp.getPARSER_ID()+"\n");
			out.write("#Generated By Cynapse at "+waktu+"\n");
			out.write("#Copyright 2015 @Telkomcel\n");
			out.write("############################################################\n");
			out.write("#Format [TableName][TableField][TypeData][ColumnSize]\n");
			out.write("\n\n");
			out.flush();
			
			for(Map.Entry<String, Map<String,DBMetadata>> objMap : dbTableModel.tableModel.entrySet()){
				out.write("#Table "+objMap.getKey()+"\n");
				for(Map.Entry<String,DBMetadata> oCols : objMap.getValue().entrySet()){
//					out.write("["+objMap.getKey()+"]["+oCols.getKey()+"]["+oCols.getValue().getTYPE_NAME()+"]\n");
					out.write("["+oCols.getValue().getTABLE_NAME()+"]["+oCols.getValue().getCOLUMN_NAME()+"]["+oCols.getValue().getTYPE_NAME()+"]["+oCols.getValue().getCOLUMN_SIZE()+"]\n");
				}
				out.write("\n\n");
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}
