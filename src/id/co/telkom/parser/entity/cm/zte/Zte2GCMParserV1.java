package id.co.telkom.parser.entity.cm.zte;

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

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class Zte2GCMParserV1 extends AbstractParser{

	public Zte2GCMParserV1(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		Map<Integer, Object>  header = new LinkedHashMap<Integer, Object>();
	    Map<String, Object>  map = new LinkedHashMap<String, Object>();
	    String filename=file.getName().toUpperCase();
		String ne_id=filename.contains("-")?filename.substring(0,filename.indexOf("-")):filename;
		ctx.setNe_id(ne_id);
		ctx.setFileName(filename);
		 InputStream input = new BufferedInputStream(
                 new FileInputStream(file));
	     POIFSFileSystem fs = new POIFSFileSystem( input );
	     HSSFWorkbook wb = new HSSFWorkbook(fs);
	     
	     int numberOfSheets=wb.getNumberOfSheets();
	     
	for (int x=1;x<numberOfSheets; x++){
		
	     HSSFSheet sheet = wb.getSheetAt(x);
	     String tableName=cynapseProp.getTABLE_PREFIX()+sheet.getSheetName().toUpperCase();
	     loader.onNewTable(tableName, ctx);
//	     System.out.println(tableName);
	     
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
                 
                 if(iRow==1){// get header..
                	 if(iCell!=0)
                		 header.put(iCell,getCellContent(cell).toString().toUpperCase() );
                 }else 
                 if(iRow>1){//get data
                	 if(iCell!=0){
                    	map.put(header.get(iCell).toString(), getCellContent(cell));
                    	
                    	if( iCell<5 && header.get(iCell).toString().trim().endsWith("ID")){
                    		String MOID=map.get("MO_ID")==null ? header.get(iCell).toString().trim()+"="+getCellContent(cell) : map.get("MO_ID") +"/"+header.get(iCell).toString().trim()+"="+getCellContent(cell);
                    		map.put("MO_ID",MOID);
                    		ctx.setMo_id(MOID);
                    	}
                	 }
                 }
                 
                
             }
             // ok, we got all off them
             if(cynapseProp.isGENERATE_SCHEMA_MODE() && iRow==1){
//            	 PutModel(tableName, header);
            	 for(Map.Entry<Integer, Object>mp:header.entrySet()){
            		 PutModel(tableName,mp.getValue().toString(),"");
            	 }
            	 header = new LinkedHashMap<Integer, Object>();
            	 break;
             }else
              if(iRow>1 && map!=null){
//            	  System.out.println(map);
            	  loader.onReadyModel(map, ctx);
            	  map  = new LinkedHashMap<String, Object>();
              }
         }
         //insert it..
         loader.onEndFile();
		}
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
}
