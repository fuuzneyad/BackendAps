package id.co.telkom.parser.entity.dashboard.mkios;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DateUtil;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.dashboard.mkios.model.MkiosMeasurementModel;
import id.co.telkom.parser.entity.dashboard.mkios.model.MkiosTableMappingMeasurementModel;

public class MkiosV1 extends AbstractParser{
	private static final Logger logger = Logger.getLogger(MkiosV1.class);
	private ParserPropReader cynapseProp;
	private Map<String, MkiosMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public MkiosV1(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, MkiosMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		String spt[] =  file.getName().split("_");
		ctx.setDatetimeid(convertDate(spt[spt.length-1].replace(".xls", "")));
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		FileInputStream fis =new FileInputStream(file);
		InputStream input = new BufferedInputStream(fis);
		POIFSFileSystem fs = new POIFSFileSystem( input );
	    HSSFWorkbook wb = new HSSFWorkbook(fs);
	     
	    int numberOfSheets=wb.getNumberOfSheets();
	    for (int x=0;x<numberOfSheets; x++){
			
		    HSSFSheet sheet = wb.getSheetAt(x);
		    MkiosMeasurementModel mdl = modelMap.get(sheet.getSheetName());
		    if(mdl!=null) {
		    	for(Map.Entry<String,MkiosTableMappingMeasurementModel> meas: mdl.getSheetTableMap().entrySet()){
		    		
		    		ctx.setTableName(meas.getValue().getTableName());
		    		
		    		int xPos=meas.getValue().getxPos()!=-1?meas.getValue().getxPos():0;
		    		int width=meas.getValue().getWidht()!=-1?xPos+meas.getValue().getWidht():0;
		    		width+=xPos;
		    		int yPost=meas.getValue().getyPos()!=-1?meas.getValue().getyPos():0;
		    		int depth=meas.getValue().getDepth()!=-1?meas.getValue().getDepth():0;
		    		depth+=yPost;
		    		
		    		String[] header=meas.getValue().getFieldSequence().split(",");
			    	for (int rowNum=yPost-1;rowNum<depth-1;rowNum++){
			    		HSSFRow row = sheet.getRow(rowNum);
			    		if(row!=null){
			    			int ctr=-1;
				    		for (int colNum=xPos-1; colNum<width-1; colNum++){
				    			ctr++;
				    			HSSFCell cell =row.getCell(colNum);
				    				if(ctr<header.length){
				    					String hdr =header[ctr].trim();
				    					String k = hdr.contains("|") ? hdr.split("\\|")[0] : hdr;
				    					Object v = hdr.contains("|") ? getCellDateContent(cell, hdr.split("\\|")[1]):getCellContent(cell);
				    					if(cynapseProp.isGENERATE_SCHEMA_MODE()){
				    						PutModel(meas.getValue().getTableName(), k, v!=null?v.toString():null);
				    					}else{
				    						map.put(k, v);
				    					}
				    				}
				    		}
				    		//ready here
				    		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
//				    			System.out.println(map);
				    			loader.onReadyModel(map, ctx);
					    		map = new LinkedHashMap<String, Object>();
				    		}
				    		
			    		}
			    	}
		    	}
		    }else//sheet
		    {
		    	System.out.println(sheet.getSheetName()+" Hasn't been mapped yet!!");
		    	logger.error(sheet.getSheetName()+" Hasn't been mapped yet!!");
		    }
		}
	    fis.close();
	    input.close();
		loader.onEndFile();
	}

	private Object getCellDateContent(final HSSFCell cell, String format){
		if(cell==null||cell.toString().equals(""))
			return null;
		if(DateUtil.isCellDateFormatted(cell)){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
		    return sdf.format(cell.getDateCellValue());			
		}
		return null;
	}
	private Object getCellContent(final HSSFCell cell){
		if (cell == null){
			return null;
		}
		//oke jg sebenernya
//		if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType())
////			return String.valueOf(cell.getNumericCellValue());
//			return cell.getNumericCellValue();
//	    if (HSSFCell.CELL_TYPE_STRING == cell.getCellType())
//			return cell.getStringCellValue();else
//		if (HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType())
//			return cell.getBooleanCellValue();else
//		if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType())
//			return null;else
//		if (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType()) {
//			switch (cell.getCachedFormulaResultType()) {
//			case HSSFCell.CELL_TYPE_NUMERIC:
//				return cell.getNumericCellValue();
//			case HSSFCell.CELL_TYPE_STRING:
//				return cell.getRichStringCellValue();
//			}
//			return null;
//		} 
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			return cell.getStringCellValue();
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {

	}

	@Override
	public void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"MkiosSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
//				sb.append("\t`GRANULARITY` int(40) ,\n");
//				sb.append("\t`NE_ID` varchar(300) DEFAULT NULL,\n");
//				sb.append("\t`MO_ID` varchar(400) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData =  "VARCHAR(50),\n"; 
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

	@SuppressWarnings("unused")
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	private static String convertDate(String val) {
		String format = "yyyyMMdd";

		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return myFormat.format(fromUser.parse(val));
		} catch (ParseException e) {
			return val;
		}
	}
}
