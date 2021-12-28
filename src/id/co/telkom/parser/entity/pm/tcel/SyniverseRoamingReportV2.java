package id.co.telkom.parser.entity.pm.tcel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class SyniverseRoamingReportV2  extends AbstractParser{
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public SyniverseRoamingReportV2(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		ctx.setTableName("ROAMING_TEMP");
		Map<Integer, String> header = new LinkedHashMap<Integer, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		StandardMeasurementModel mapField = this.modelMap.get("default_mapping_field");
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		loader.onBeginFile();
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
	            		String field = mapField.getFieldMap().get(getCellContent(cell).toString().trim());
	            		header.put(iCell, field);
	            	 } else{
	            		 Object isi=getCellContent(cell);
	            		 		if(header.get(iCell)!=null)
	            		 			map.put(header.get(iCell), isi);
	            	 }
	             }
	             if (!map.isEmpty()){
	            	 //System.out.println(map);
	            	 loader.onReadyModel(map, ctx);
	            	 map = new LinkedHashMap<String, Object>();
	             } 
	         }
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
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
