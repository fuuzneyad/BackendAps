package id.co.telkom.parser.entity.pm.tcel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class UntlStudents extends AbstractParser{
	private static final Logger logger = Logger.getLogger(UntlStudents.class);
	
	public UntlStudents(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		logger.info("Processing "+file.getName());
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		ctx.setTableName("UNTL_STUDENTS");
		String departement="";
		boolean parse = false;
		
		loader.onBeginFile();
			
		if(file.getName().endsWith("xls")){
			POIFSFileSystem fs = new POIFSFileSystem( input );
		    HSSFWorkbook wb = new HSSFWorkbook(fs);
		    int jumlahSheet= wb.getActiveSheetIndex();
		    for (int sheetNum = 0; sheetNum<=jumlahSheet;sheetNum++){
		    HSSFSheet sheet = wb.getSheetAt(sheetNum);
		    
		    String sheetname =sheet.getSheetName();
		    System.out.println(sheetname);
		    
		    @SuppressWarnings("rawtypes")
			Iterator rows = sheet.rowIterator();
	         while( rows.hasNext() ) {
	             HSSFRow row = (HSSFRow) rows.next();
	             @SuppressWarnings("rawtypes")
				 Iterator cells = row.cellIterator();
	             
	             @SuppressWarnings("unused")
				int iRow=row.getRowNum();
	             while( cells.hasNext() ) {
	                 HSSFCell cell = (HSSFCell)cells.next();
	                 cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	                 
	                 int iCell=cell.getColumnIndex();
	                 
	                 if(iCell==0){
	                	 String s = (getCellContent(cell).toString().toUpperCase());
	                	 if(s.contains("DEPARTEMENTU")||s.contains("DEPARTAMENTU")){
	                		 departement = s.replace("DEPARTEMENTU", "").replace("DEPARTAMENTU", "").trim();
	                	 }
	                	 //angka
	                	 if(isInt(s)){
	                		 parse=true;
	                	 }
	                 }
	                 if(parse){
	                	 if(iCell==0){
	                		 map.put("FILENAME",file.getName());
	                		 map.put("SHEETNAME",sheetname);
	                		 map.put("DEPARTEMENTU", departement);
	                		 map.put("NO", getCellContent(cell));
	                	 }if(iCell==1)
	                		 map.put("NARAN", getCellContent(cell));
	                	 if(iCell==2)
	                		 map.put("NRE", getCellContent(cell));
	                	 if(iCell==3)
	                		 map.put("SEXU", getCellContent(cell));
	                	 if(iCell==4)
	                		 map.put("TTL", getCellContent(cell));
	                	 if(iCell==5){
	                		 map.put("MUNICIPU", getCellContent(cell));
	                	 }
	                	 if(iCell==6){
	                		 map.put("REJISTU", getCellContent(cell));
	                	 }
	                	 if(iCell==7){
	                		 map.put("LAREJISTU", getCellContent(cell));
	                		 parse = false;
	                		 loader.onReadyModel(map, ctx);
//	                		 System.out.println(map);
	                		 map = new LinkedHashMap<String, Object>();
	                	 }
	                 }
	              }
	             }
	         }
		}
		else if(!file.getName().startsWith("~") && file.getName().endsWith("xlsx")){
			//xlsx
			XSSFWorkbook wbx = new XSSFWorkbook(input);
			int jumlahSheet= wbx.getActiveSheetIndex(); 
			for (int sheetNum = 0; sheetNum<=jumlahSheet;sheetNum++){
			XSSFSheet sheet  = wbx.getSheetAt(sheetNum);
			
			String sheetname =sheet.getSheetName();
		    System.out.println(sheetname);
		    
	    	 @SuppressWarnings("rawtypes")
				Iterator rows = sheet.rowIterator();
		         while( rows.hasNext() ) {
		        	 Row row = (Row)rows.next();
		        	 @SuppressWarnings("rawtypes")
					 Iterator cells = row.cellIterator();
		             
		             @SuppressWarnings("unused")
					int iRow=row.getRowNum();
		             while( cells.hasNext() ) {
		            	 Cell cell = (Cell)cells.next();
		            	 cell.setCellType(Cell.CELL_TYPE_STRING);
		            	 int iCell=cell.getColumnIndex();
		            	 
		            	 if(iCell==0){
		            		 String s = (getCellContent(cell).toString()).toUpperCase();
		            		 if(s.contains("DEPARTEMENTU")||s.contains("DEPARTAMENTU")){
		                		 departement = s.replace("DEPARTEMENTU", "").replace("DEPARTAMENTU", "").trim();
		                	 }
		                	 //angka
		                	 if(isInt(s)){
		                		 parse=true;
		                	 }
		            	 }
		            	 
		            	 if(parse){
		                	 if(iCell==0){
		                		 map.put("FILENAME",file.getName());
		                		 map.put("SHEETNAME",sheetname);
		                		 map.put("DEPARTEMENTU", departement);
		                		 map.put("NO", getCellContent(cell));
		                	 }if(iCell==1)
		                		 map.put("NARAN", getCellContent(cell));
		                	 if(iCell==2)
		                		 map.put("NRE", getCellContent(cell));
		                	 if(iCell==3)
		                		 map.put("SEXU", getCellContent(cell));
		                	 if(iCell==4)
		                		 map.put("TTL", getCellContent(cell));
		                	 if(iCell==5){
		                		 map.put("MUNICIPU", getCellContent(cell));
		                	 }
		                	 if(iCell==6){
		                		 map.put("REJISTU", getCellContent(cell));
		                	 }
		                	 if(iCell==7){
		                		 map.put("LAREJISTU", getCellContent(cell));
		                		 parse = false;
		                		 loader.onReadyModel(map, ctx);
//		                		 System.out.println(map);
		                		 map = new LinkedHashMap<String, Object>();
		                	 }
		                 }
		             }
//		             if (!map.isEmpty()){
//		            	 loader.onReadyModel(map, ctx);
//		             }
//		             map = new LinkedHashMap<String, Object>();
		         }
			  }
			}
		loader.onEndFile();
		input.close();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"Schema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
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
	
	private boolean isInt(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	private Object getCellContent(Cell cell){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try{
			if(Cell.CELL_TYPE_STRING==cell.getCellType())
            	return  cell.getStringCellValue();
			if(DateUtil.isCellDateFormatted(cell) )
				return  sdf.format(cell.getDateCellValue());
			if(Cell.CELL_TYPE_NUMERIC==cell.getCellType())
            	return cell.getNumericCellValue();
            if(Cell.CELL_TYPE_BOOLEAN==cell.getCellType())
                return  cell.getBooleanCellValue();
            if(Cell.CELL_TYPE_BLANK==cell.getCellType())
                return  null;
			if(Cell.CELL_TYPE_FORMULA==cell.getCellType())
				return cell.getNumericCellValue();
                return  null;
		} catch (IllegalStateException e){
			return null;
		}
	}
	
//	public static void main(String[] args){
//		String s="asbc|yyy";
//		System.out.println(s.split("\\|")[1]);
//	}
	
}
