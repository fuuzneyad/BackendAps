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
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class TcelNetworkPerfExcel01 extends AbstractParser{
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(TcelNetworkPerfExcel01.class);
	
	@SuppressWarnings("unchecked")
	public TcelNetworkPerfExcel01(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, String> header = new LinkedHashMap<String, String>();
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
			ctx.setTableName(mdl.getTableName());
			logger.info("found measurement mapping for "+file.getName()+" "+mdl.getTableName());
			InputStream input = new BufferedInputStream(new FileInputStream(file));
			if(file.getName().endsWith(".xls")) {
				
				POIFSFileSystem fs = new POIFSFileSystem( input );
			    HSSFWorkbook wb = new HSSFWorkbook(fs);
			    
			    HSSFSheet sheet = wb.getSheetAt(0);
			    Iterator<Row> iterator = sheet.iterator();
			    while (iterator.hasNext()) {
		            Row nextRow = iterator.next();
		            Iterator<Cell> cellIterator = nextRow.cellIterator();
		             
		            while (cellIterator.hasNext()) {
		                Cell cell = cellIterator.next();
		                int posX = cell.getColumnIndex();
		                
		                Object o = getCellContent(cell);
		                
		                if(o!=null){
		                	String s = o.toString();
		                	Object cl = mdl.getFieldMap().get(s);
			                if(cl!=null){
			                		header.put("H"+posX, cl.toString());
			                }else
			                if(header.get("H"+posX)!=null)
			                		map.put(header.get("H"+posX), s);
		                }
		            }
	
		            if(!cynapseProp.isGENERATE_SCHEMA_MODE()&&!map.isEmpty()){
	//	            	System.out.println(ctx.t_name+map);
		            	loader.onReadyModel(map, ctx);
		            }
		            
		            map = new LinkedHashMap<String, Object>();
		        }
			    
			} else if(file.getName().endsWith(".xlsx")) {
				//xlsx
				XSSFWorkbook wbx = new XSSFWorkbook(input);
				XSSFSheet sheet  = wbx.getSheetAt(0);
		    	 @SuppressWarnings("rawtypes")
				Iterator rows = sheet.rowIterator();
			    while( rows.hasNext() ) {
			        	 Row row = (Row)rows.next();
			        	 @SuppressWarnings("rawtypes")
						 Iterator cells = row.cellIterator();
			             
			             while( cells.hasNext() ) {
				            	 Cell cell = (Cell)cells.next();
				            	 //cell.setCellType(Cell.CELL_TYPE_STRING);
				            	 
				            	 int posX=cell.getColumnIndex();
				            	 
				            	 Object o = getCellContent(cell);
				            	 if(o!=null) {
				            		 String s = o.toString();
					              Object cl = mdl.getFieldMap().get(s);
					              if(cl!=null){
					            		header.put("H"+posX, cl.toString());
					              }else
						                if(header.get("H"+posX)!=null)
						                	  map.put(header.get("H"+posX), s);
				            	 }
			             }
			             if (!map.isEmpty()){
			            	 //System.out.println(map);
			            	 loader.onReadyModel(map, ctx);
			             }
			             map = new LinkedHashMap<String, Object>();
			      }
			}
			
			
			input.close();
		}else{
			System.out.println("Mapping for "+file.getName()+" Not Found!");
		}
		
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiLTERadioSchema.sql";
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
//		double d = Double.parseDouble("3.4790039062500004E-5");
//		System.out.println(d);
//	}
	
	
}
