package id.co.telkom.parser.entity.imoc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class ImocParserXlsBTSPerformanceV2 extends AbstractParser{
	
	private Map<String, StandardMeasurementModel> modelMap ;
	
	@SuppressWarnings("unchecked")
	public ImocParserXlsBTSPerformanceV2(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap=(Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader, Context ctx) throws Exception {
		StandardMeasurementModel mdl=null;
		for (Map.Entry<String, StandardMeasurementModel>entry : this.modelMap.entrySet()){
			mdl = null ;
			if((StandardMeasurementModel)entry.getValue()!=null &&
				file.getName().matches(((StandardMeasurementModel)entry.getValue()).getMeasurementType())
			){
				 mdl=(StandardMeasurementModel)entry.getValue();
				 break;
			}
		}
		
		if(mdl!=null){
			
			if(file.getName().endsWith("xlsx")){
				loader.onBeginFile();
				Map<Integer, String> header = new LinkedHashMap<Integer, String>();
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				
				for(String t:mdl.getTableName().split("\\|")){
					int sheetNo = Integer.parseInt(t.split("=")[0]);
					String T_NAME = t.split("=")[1];
					ctx.setTableName(T_NAME);
					
					InputStream input = new BufferedInputStream(new FileInputStream(file));
					
					XSSFWorkbook wbx = new XSSFWorkbook(input);
					XSSFSheet sheet  = wbx.getSheetAt(sheetNo);
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
				            		String counterName =getCellContent(cell).toString().trim()
											.toUpperCase()
											.replace(" ", "_")
											.replace(".", "")
											.replace("&", "")
											.replace("/", "_")
											.replace("(", "")
											.replace(")", "");
				            		header.put(iCell, counterName);
				            		if(cynapseProp.isGENERATE_SCHEMA_MODE()){
				            			PutModel(T_NAME, counterName, "0");
				            		}
				            	 } else{
				            		 if(cynapseProp.isGENERATE_SCHEMA_MODE())
				            			 break;
				            		 Object isi=getCellContent(cell);
				            		 String headerName = header.get(iCell);
				            		 if(headerName!=null){
				            		 	if(headerName.equals("DATE")||headerName.equals("DATE_ID")){
				            		 		if(isInteger(isi.toString()))
				            		 			ctx.setDatetimeid(convertIntToDate(Integer.parseInt(isi.toString())));
				            		 		else
				            		 			ctx.setDatetimeid(convertDate(isi.toString()));
				            		 	}else
				            		 		map.put(headerName, isi);
				            		 }
				            	 }
				             }
				             if (!map.isEmpty() /*todo: filter 14 day here..*/ && !cynapseProp.isGENERATE_SCHEMA_MODE())
				            	 loader.onReadyModel(map, ctx);
				             map = new LinkedHashMap<String, Object>();
				         }
				         input.close();
				         loader.onEndFile();
				}
			}
		}
		else
			System.err.println(file.getName() +" Not Mapped yet!!");
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx) throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"ImocBTSPerf.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
				
			StringBuilder sb = new StringBuilder();
				
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
					
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` date DEFAULT NULL,\n");
					
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
							
					if(entry2.getKey().length()>30)
							System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
							
					String typeData = "VARCHAR("+(entry2.getValue().length()+20)+"),\n"; 
					if(!entry2.getKey().equals("") && !entry2.getKey().equals("DATE"))
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
	
//	private static String convertDate(String val) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//	    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//
//	    try {
//	        return dateFormat2.format(dateFormat.parse(val));
//	    } catch (ParseException e) {
////	        e.printStackTrace();
//	    	return null;
//	    }
//	}
	
	private static String convertIntToDate(Integer i){
//		int i = 0;
//		try{
//			i= Integer.parseInt(s);
//			return s;
//		}catch (NumberFormatException e){}
		
		try {
		String dt = "1899-12-30";  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		
			c.setTime(sdf.parse(dt));
			c.add(Calendar.DATE, i);  
			dt = sdf.format(c.getTime()); 
			return dt;
		} catch (ParseException e) {
			return null;
		}
	}
	
	private static String convertDate(String val) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

	    try {
	        return dateFormat2.format(dateFormat.parse(val));
	    } catch (ParseException e) {
//	        e.printStackTrace();
	    	return null;
	    }
	}
	
	private boolean isInteger(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
}
