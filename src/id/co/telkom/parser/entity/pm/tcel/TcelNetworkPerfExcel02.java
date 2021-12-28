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

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class TcelNetworkPerfExcel02 extends AbstractParser{
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(TcelNetworkPerfExcel02.class);
	
	@SuppressWarnings("unchecked")
	public TcelNetworkPerfExcel02(ParserPropReader cynapseProp,
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
			logger.info("found measurement mapping for "+file.getName()+" "+mdl.getTableName());
			FileInputStream fis =new FileInputStream(file);
			InputStream input = new BufferedInputStream(fis);
			POIFSFileSystem fs = new POIFSFileSystem( input );
		    HSSFWorkbook wb = new HSSFWorkbook(fs);
		    
		    int sheetCount = wb.getNumberOfSheets();
		    for(int sheetNo=0;sheetNo<sheetCount;sheetNo++){
		    	
			    HSSFSheet sheet = wb.getSheetAt(sheetNo);
			    String sheetName = sheet.getSheetName();
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
		                	//get header by sheetName and read cell
		                	Object cl = mdl.getFieldMap().get(sheetName+"|"+s);
			                if(cl!=null){
			                	header.put(sheetName+posX, cl.toString());
			                }else
			                if(header.get(sheetName+posX)!=null){
			                	String m = header.get(sheetName+posX);
			                	String t_name = m.split("\\|")[0];
			                	String conter_name = m.contains("|") ? m.split("\\|")[1]:m;
			                	ctx.setTableName(t_name);
			                	map.put(conter_name, s);
			                }
		                }
		            }

		            if(!cynapseProp.isGENERATE_SCHEMA_MODE()&&!map.isEmpty()){
//		            	System.out.println(ctx.t_name+map);
		            	loader.onReadyModel(map, ctx);
		            }
		            
		            map = new LinkedHashMap<String, Object>();
		        }
			}
		    input.close();
		    fis.close();
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
