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

public class ImocParserXls extends AbstractParser{
	//Buffer:
	//date, map isi
	Map<String,Map<String,Object>> bufffer =new LinkedHashMap<String, Map<String,Object>>();
//	private static final String T_NAME="SUBS_DAILY_REPORT";
	//get only 14 days
    private static final int MAX_DAY_TO_GET=14;
    private Map<String, StandardMeasurementModel> modelMap ;
    private String T_NAME;
    private Map<String, Integer> doubleCounterName = new LinkedHashMap<String, Integer>();
    
	@SuppressWarnings("unchecked")
	public ImocParserXls(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
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
			// TODO Auto-generated method stub
			Map<Integer, String> headerDate = new LinkedHashMap<Integer, String>();
			Map<Integer, String> headerColumn = new LinkedHashMap<Integer, String>();
			InputStream input = new BufferedInputStream(new FileInputStream(file));
			loader.onBeginFile();
			T_NAME=mdl.getTableName().split("\\|")[0];
			final int rowTable = Integer.parseInt(mdl.getTableName().split("\\|")[1]);
			final int colTable = Integer.parseInt(mdl.getTableName().split("\\|")[2]);
			
			ctx.setTableName(T_NAME);
			if(file.getName().endsWith("xlsx")){
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
			             //System.out.println("vvvvv "+row.getLastCellNum());
			             int lastCellNum = row.getLastCellNum();
			             int i=0;
			             while( cells.hasNext() ) {
			            	 Cell cell = (Cell)cells.next();
			            	 i++;
			            	     
				            	 cell.setCellType(Cell.CELL_TYPE_STRING);
				            	 int iCell=cell.getColumnIndex();
				            	 if(iRow==rowTable){//header date
				            		 if(lastCellNum>0 && (i+MAX_DAY_TO_GET) >= lastCellNum){
					            		 String o = getCellContent(cell).toString();
					            		 String tanggal;
					            		 if(isInteger(o)){
					            			 tanggal = convertIntToDate(Integer.parseInt(o));
					            			 headerDate.put(iCell, tanggal);
					            		 }else{
					            			 tanggal = convertDate(o);
					            			 headerDate.put(iCell, tanggal);
					            		 }
	//			            		 System.out.println(iCell+"|"+tanggal);
				            		 }
				            	 } else if(iRow>rowTable){
				            		 if(iCell==colTable){//header table
//				            			 if(!getCellContent(cell).toString().equals(""))
				            			 String counterName = getCellContent(cell).toString().trim()
			            					 		.toUpperCase()
			            					 		.replace(" ", "_")
			            					 		.replace("/", "_")
			            					 		.replace("(", "")
			            					 		.replace(")", "")
			            					 		.replace("-", "_")
			            					 		.replace("%", "PERCENT")
			            					 		.replace("SUBSCRIBER", "SUBS")
			            					 		.replace("DISTRIBUTION", "DIST");
				            			 if(counterName!=null && !counterName.equals("")){
				            			 Integer ccc = doubleCounterName.get(counterName);
					            			 if(ccc==null){
					            				 doubleCounterName.put(counterName, 1);
					            			 }else{
					            				 counterName+="_"+ccc;
					            				 doubleCounterName.put(counterName, ccc+1);
					            			 }
				            			 }
				            			 headerColumn.put(iRow,counterName);
				            			 
	//			            			 System.out.println(headerColumn);
				            		 }else{//data
				            			 if(headerDate.get(iCell)!=null){
				            				 putBuffer(headerDate.get(iCell), headerColumn.get(iRow), getCellContent(cell).toString());
				            				 if(cynapseProp.isGENERATE_SCHEMA_MODE() && !headerColumn.get(iRow).equals(""))
				            					 PutModel(T_NAME, headerColumn.get(iRow), getCellContent(cell).toString());
				            			 }
				            			 
				            		 }
				            	 }
			            	 }
			         }
			}
			
			//here were go
			if(!cynapseProp.isGENERATE_SCHEMA_MODE())
			for(Map.Entry<String, Map<String,Object>> mp : bufffer.entrySet()){
				Map<String, Object> map = mp.getValue();
				ctx.setDatetimeid(mp.getKey());
				loader.onReadyModel(map, ctx);
			}
			loader.onEndFile();
			input.close();
			}
		else
			System.err.println(file.getName() +" Not Mapped yet!!");
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		// TODO Auto-generated method stub
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"ImocSubs.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
				
			StringBuilder sb = new StringBuilder();
				
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
					
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` DATE NULL DEFAULT NULL,\n");
					
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
							
					if(entry2.getKey().length()>30)
							System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
							
					String typeData = "VARCHAR("+(entry2.getValue().length()+20)+"),\n"; 
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
	
	
	private static String convertIntToDate(int i){
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM");
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
	
//	public static void main (String args[]){
//		System.out.println(convertIntToDate(41573));
//	}
	
	private void putBuffer(String date, String key, String val){
		Map<String, Object> mp = bufffer.get(date);
		if(mp == null)
			mp = new LinkedHashMap<String, Object>();
		mp.put(key, val);
		bufffer.put(date, mp);
	}
}
