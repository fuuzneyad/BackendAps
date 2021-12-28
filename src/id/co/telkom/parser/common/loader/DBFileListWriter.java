package id.co.telkom.parser.common.loader;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;



public class DBFileListWriter {
	private Connection conn;
	private DataSource ds;
	private SimpleJdbcInsert sdc;
	public DBFileListWriter(DataSource ds){
		try{
			this.ds=ds;
			conn = ds.getConnection();
			sdc = new SimpleJdbcInsert(ds).withTableName("TPROCESS_FILE");
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	

	public boolean isFileAlreadyProcessed(String filename, String Modul){
		String SQL ="SELECT COUNT(*) FROM TPROCESS_FILE WHERE FILENAME='"+filename+"' AND SOURCE_ID='"+Modul+"'";
		try{
			if(conn.isClosed())
				conn = ds.getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(SQL);
			int result=0;
			rs.next();
			result =rs.getInt(1);			
			rs.close();
			st.close();
				if (result>0)				
					return true;else
					return false;
				
			
		}catch(SQLException e){
			System.out.println("an ERROR Check File from DB! "+ SQL);
			System.out.println(e.getMessage());
			return true;
		}
	}
	
	public synchronized void writeFileLog(String filename, String modul, Timestamp t,  String message, String status){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("PROCESSDATE", t);
			map.put("SOURCE_ID", modul);
			map.put("FILENAME", filename);
			map.put("MESSAGE", message);
			map.put("STATUS", status);
			try{
				sdc.execute(map);
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public synchronized void writeFileLog(File f, String modul, Timestamp t,  String message, String status){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("PROCESSDATE", t);
			map.put("SOURCE_ID", modul);
			map.put("FILENAME", f.getName());
			map.put("SIZE", f.length());
			map.put("MESSAGE", message);
			map.put("STATUS", status);
			try{
				sdc.execute(map);
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	public synchronized void writeFileLog(File f, String modul, Timestamp starttime,  
			String message, String status, int rowCount, Timestamp endTime){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("PROCESSDATE", starttime);
			map.put("SOURCE_ID", modul);
			map.put("FILENAME", f.getName());
			map.put("FILESIZE", f.length());
			map.put("MESSAGE", message);
			map.put("STATUS", status);
			map.put("START_PROCESS", starttime);
			map.put("END_PROCESS", endTime);
			map.put("ROW_NUM", rowCount);
			map.put("PROCESS_TYPE", "NORMAL");
			try{
				sdc.execute(map);
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public synchronized void writeFileLog2(String filename, String modul, Timestamp t,  String message, String status){
		String SQL="INSERT INTO  TPROCESS_FILE(PROCESSDATE, SOURCE_ID, FILENAME, MESSAGE, STATUS) values" +
				" ('"+t+"', '"+modul+"', '"+filename+"', '"+message+"', '"+status+"')";
		
		try{
			if(conn.isClosed())
				conn = ds.getConnection();
			Statement st = conn.prepareStatement(SQL);
			st.execute(SQL);
			st.close();	
//			conn.commit();
		}catch(SQLException e){
			System.out.println("an ERROR writeFileLog! "+ SQL);
			e.printStackTrace();
		}
	}
	
	
	public synchronized boolean isFileAlreadyDownloaded(String filename, String Modul){
		String SQL ="SELECT COUNT(*) FROM TPROCESS_FILE WHERE FILENAME='"+filename+"' AND SOURCE_ID='"+Modul+"'";
//		String SQL ="SELECT COUNT(*) FROM TGET_FILE WHERE FILENAME='"+filename+"' AND SOURCE_ID='"+Modul+"'";
		try{
			if(conn.isClosed())
				conn = ds.getConnection();
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(SQL);
			int result=0;
			rs.next();
			result =rs.getInt(1);			
			rs.close();
			st.close();
				if (result>0)				
					return true;else
					return false;
				
			
		}catch(SQLException e){
			System.out.println("an ERROR Check File from DB! "+ SQL);
			System.out.println(e.getMessage());
			return true;
		}
	}
	
	public void writeFileDownload(String filename, String modul, Timestamp t){
//		String SQL="INSERT INTO TPROCESS_FILE(SOURCE_ID, FILENAME) values(" +
//				"'"+modul+"', '"+filename+"');";
		String SQL="INSERT INTO TGET_FILE(SOURCE_ID, FILENAME) values(" +
			"'"+modul+"', '"+filename+"');";
		System.out.println(SQL);
		try{
			if(conn.isClosed())
				conn = ds.getConnection();
			Statement st = conn.prepareStatement(SQL);
			st.execute(SQL);
			st.close();	
//			conn.commit();
		}catch(SQLException e){
			System.out.println("an ERROR writeFileLog! "+ SQL);
			e.printStackTrace();
		}
	}
	
	public void ExecuteAQuery(String SQL){
		
		try{
			if(conn.isClosed())
				conn = ds.getConnection();
			Statement st = conn.prepareStatement(SQL);
			st.execute(SQL);
			st.close();	
		}catch(SQLException e){
			System.out.println("an ERROR Execute! "+ SQL);
			e.printStackTrace();
		}
	}
	
}
