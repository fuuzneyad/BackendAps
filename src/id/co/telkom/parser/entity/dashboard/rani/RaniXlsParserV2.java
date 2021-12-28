package id.co.telkom.parser.entity.dashboard.rani;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class RaniXlsParserV2 extends AbstractParser{
	private final String T_NAME="RANI_TABLE";
	public RaniXlsParserV2(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		Map<Integer, String> header = new LinkedHashMap<Integer, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		ctx.setTableName(T_NAME);
		
		loader.onBeginFile();
		if(file.getName().endsWith("xls")){
			POIFSFileSystem fs = new POIFSFileSystem( input );
		    HSSFWorkbook wb = new HSSFWorkbook(fs);
		    HSSFSheet sheet = wb.getSheetAt(0);
		    System.out.println(sheet.getSheetName());
		    
		    @SuppressWarnings("rawtypes")
			Iterator rows = sheet.rowIterator();
	         while( rows.hasNext() ) {
	             HSSFRow row = (HSSFRow) rows.next();
	             @SuppressWarnings("rawtypes")
				 Iterator cells = row.cellIterator();
	             
	             int iRow=row.getRowNum();
	             while( cells.hasNext() ) {
	                 HSSFCell cell = (HSSFCell)cells.next();
	                 cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	                 
	                 int iCell=cell.getColumnIndex();
	                 
	                 if(iRow==0){// get header..
	                		header.put(iCell,getCellContent(cell).toString().trim().toUpperCase().replace(" ", "_").replace(".", "").replace("/", "_") );
	                 } else {//get data
	                	 if(header.get(iCell)!=null)
	                    	map.put(header.get(iCell).toString(), getCellContent(cell));
	                 }
	                
	             }
	             
	             if (!map.isEmpty())
	            	 loader.onReadyModel(map, ctx);
                 map.clear();
	         }
		}else if(file.getName().endsWith("xlsx")){
			//xlsx
			XSSFWorkbook wbx = new XSSFWorkbook(input);
			XSSFSheet sheet  = wbx.getSheetAt(0);
	    	 @SuppressWarnings("rawtypes")
				Iterator rows = sheet.rowIterator();
		         while( rows.hasNext() ) {
		        	 Row row = (Row)rows.next();
		        	 @SuppressWarnings("rawtypes")
					 Iterator cells = row.cellIterator();
		             
		             int iRow=row.getRowNum();
		             while( cells.hasNext() ) {
		            	 Cell cell = (Cell)cells.next();
		            	 cell.setCellType(Cell.CELL_TYPE_STRING);
		            	 int iCell=cell.getColumnIndex();
		            	 //header here
		            	 if(iRow==0){
		            		header.put(iCell, getCellContent(cell).toString().trim().toUpperCase().replace(" ", "_").replace(".", "").replace("/", "_"));
		            	 } else{
		            		 Object isi=getCellContent(cell);
		            		 		if(header.get(iCell)!=null)
		            		 			map.put(header.get(iCell), isi);
		            	 }
		             }
		             if (!map.isEmpty())
		            	 loader.onReadyModel(map, ctx);
		             map.clear();
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
		
	}

	private Object getCellContent(HSSFCell cell){
		if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
            	return cell.getNumericCellValue();
            else
            if(HSSFCell.CELL_TYPE_STRING==cell.getCellType())
            	return  cell.getStringCellValue();
            else
                if(HSSFCell.CELL_TYPE_BOOLEAN==cell.getCellType())
                	return  cell.getBooleanCellValue();
                else
                    if(HSSFCell.CELL_TYPE_BLANK==cell.getCellType())
                    	return  null;
                    else
                       	return  null;
	}
	
	private Object getCellContent(Cell cell){
		try{
			if(Cell.CELL_TYPE_STRING==cell.getCellType())
            	return  cell.getStringCellValue();
//			if(DateUtil.isCellDateFormatted(cell) )
//				return SDate(cell.getDateCellValue());
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
}
