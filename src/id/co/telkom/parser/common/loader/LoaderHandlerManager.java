package id.co.telkom.parser.common.loader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import id.co.smltech.praxis.repositio.bds.collector.CynapseInterface;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DBMetadata;
import id.co.telkom.parser.common.model.GroupedModels;
import id.co.telkom.parser.common.model.ParrentModel;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.JsonParser;
import id.co.telkom.parser.common.util.RowIncrementor;


public class LoaderHandlerManager implements  CynapseLoader, Serializable {
	
	private static final long serialVersionUID = 2274594208215105708L;
	private static final Logger logger = Logger.getLogger(LoaderHandlerManager.class);
	private OutputMethodPropReader om;
	private RowIncrementor ri;
	private DataSource ds;
	private GroupedModels gmdl;
	private FileWriter outLoa;
	private ParserPropReader cynapseProp;
	private CynapseInterface intf;
	private JsonParser jp;
	private AbstractInitiator cynapseInit;
	private Map<String, SimpleJdbcInsert> sdcs = new HashMap<String, SimpleJdbcInsert>();
	
	public LoaderHandlerManager(OutputMethodPropReader om, DataSource ds, ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		this.ds=ds;
		this.om = om;
		this.ri = new RowIncrementor(om.GetJdbcLoad());
		this.gmdl = new GroupedModels();
		this.cynapseProp =cynapseProp;
		this.cynapseInit=cynapseInit;
		this.jp = new JsonParser();
		this.jp.initGenerator();
		if(om.isRpc() && !cynapseProp.isGENERATE_SCHEMA_MODE()){
				this.intf=new CynapseInterface(cynapseProp.getAcquiredDatetimes());
		}
	}

	@Override
	public void onBeginFile() {
		ri = new RowIncrementor(om.GetJdbcLoad());
		
		if (om.isDelimited()){

		}
		if(om.isJson()){
			
		}
		if (om.isJdbc()){
			gmdl = new GroupedModels();
		}
		if(om.isRpc()&& !cynapseProp.isGENERATE_SCHEMA_MODE()){
			intf.init(om.getRPC_HOST(), om.getRPC_PORT());
		}
		
	}

	@Override
	public void onEndFile() {
		
		if (om.isDelimited()){
				
		}
		if(om.isJson()){
		}
		if (om.isJdbc()){
			DoInsertBatch();
		}
		if(om.isRpc() && !cynapseProp.isGENERATE_SCHEMA_MODE()){
			intf.close();
		}
	}

	@Override
	public void onNewTable(String tableName, Context context) {
		context.setTableName(tableName);
		if (om.isDelimited()){
		}
		if (om.isJson()){
			
		}
		if (om.isJdbc()){
			
		}
	}
	
	@Override
	public void onEndTable() {
	}
	
	@Override
	public void onParserEnded(Context context) {
		//reinit & create active partition
		if(om.isRpc() && !cynapseProp.isGENERATE_SCHEMA_MODE()){
			CynapseInterfaceExtended ci = new CynapseInterfaceExtended(om.getRPC_HOST(), om.getRPC_PORT());
			ci.createActivePartInfo(cynapseInit.getDatetimeMap(), getSourceRPC(context, om));
			ci.close();
		}
		if(om.isCsv()){
			System.out.println("Rewrite CSV Header..");
			logger.info("Rewrite CSV Header..");
			for( Map.Entry<String, Map<String,DBMetadata>> tablesMeta : cynapseInit.getDBTableModel().tableModel.entrySet()){
				String table = tablesMeta.getKey().replace(cynapseProp.getTABLE_PREFIX(), "");
				String fileLoc=om.getCSV_FILE_LOCATION()+"/"+table+".hdr";
				try {
					FileWriter out = new FileWriter(fileLoc,false);
					out.write(PostProcessGenerator.GenerateMysqlHeaderDelimiter(tablesMeta.getValue())+"\n");
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
		if(om.isLoa() && om.isLoaOra10()){
			System.out.println("Writing Oracle LOA Header..");
			logger.info("Writing Oracle LOA Header..");
			for( Map.Entry<String, Map<String,DBMetadata>> tablesMeta : cynapseInit.getDBTableModel().tableModel.entrySet()){
				String table = tablesMeta.getKey();
				String fileLoc=om.getLOA_FILE_LOCATION()+"/"+table+".hdr";
				try {
					FileWriter out = new FileWriter(fileLoc,false);
					out.write(PostProcessGenerator.GenerateOraHeader(tablesMeta.getValue(), /*cynapseProp.getTABLE_PREFIX()+*/table));
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else
			if(om.isLoa() && om.isLoaMysql10()){
				logger.info("Writing Mysql LOA Header..");
				for( Map.Entry<String, Map<String,DBMetadata>> tablesMeta : cynapseInit.getDBTableModel().tableModel.entrySet()){
					String table = tablesMeta.getKey().replace(cynapseProp.getTABLE_PREFIX(), "");
					String fileLoc=om.getLOA_FILE_LOCATION()+"/"+table+".hdr";
					try {
						FileWriter out = new FileWriter(fileLoc,false);
						out.write(PostProcessGenerator.GenerateMysqlHeaderDelimiter(tablesMeta.getValue())+"\n");
						out.flush();
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	}
	
	

	@Override
	public synchronized void onReadyModel(Map<String, Object> map, Context context) {
		final String tPrefix = context.isloadWithPrefix?cynapseProp.getTABLE_PREFIX():"";
		ri.incrementRow();
		
		if (om.isDelimited()){
		}
		if (om.isJdbc()){
			MergedModel(map,context);
			if (ri.checkFactors()){
				DoInsertBatch();
			}
		}
		if(om.isRpc()){
			cynapseInit.setDatetimeMap(context.dateTimeid.substring(0, 13)+":00:00");
			rpcSendData(map,context);			
		}
		if(om.isLoa()){
			Map<String,DBMetadata> tableMeta=cynapseInit.getDBTableModel().tableModel.get((tPrefix+context.t_name).toUpperCase());
			String filename = om.getLOA_FILE_PATTERN().replace("$TABLENAME", tPrefix+context.t_name);
			String dateFile=filename.contains("$DATETIMEID(")?filename.substring(filename.indexOf("$DATETIMEID(")+12, filename.indexOf(")")):"";
			String replacedDateFile=PostProcessGenerator.convertDate(context.dateTimeid,dateFile);
			filename=filename.replace("$DATETIMEID("+dateFile+")", replacedDateFile);
			
			int intOrder=cynapseInit.rowCounter.getCounter(context.t_name+replacedDateFile);
			
			int page=(intOrder-1)/om.GetLoaSize();
//			boolean isTurnHeader=intOrder==page*om.GetLoaSize()+1;
			
				if(tableMeta!=null){
					if(om.isLoaMysql10()){
						filename=filename+"."+page;
						try {
								outLoa = new  FileWriter(om.getLOA_FILE_LOCATION()+"/"+filename, true);
								outLoa.write(PostProcessGenerator.GenerateCsvMapMysql(context, tableMeta, map)+"\n");
								outLoa.flush();
								outLoa.close();	
							
						} catch (IOException e) {
							logger.error(e);
							e.printStackTrace();
							System.exit(1);
						}
					}else 
					if(om.isLoaMysql11()){
							try {
									outLoa = new  FileWriter(om.getLOA_FILE_LOCATION()+"/"+filename, true);
									outLoa.write(PostProcessGenerator.GenerateCsvMapMysql(context, tableMeta, map)+"\n");
									outLoa.flush();
									outLoa.close();	
								
							} catch (IOException e) {
								logger.error(e);
								e.printStackTrace();
								System.exit(1);
							}
					}else if (om.isLoaOra10()){
						filename=filename+"."+page;
						try {
//							filename=filename+"."+context.date.getTime();
							outLoa = new  FileWriter(om.getLOA_FILE_LOCATION()+"/"+filename, true);
//							if(intOrder==page*om.GetLoaSize()+1)
//								outLoa.write(PostProcessGenerator.GenerateOraHeader(tableMeta, cynapseProp.getTABLE_PREFIX()+context.t_name));
							outLoa.write(PostProcessGenerator.GenerateOraDelimited(context,tableMeta, map)+"\n");
							outLoa.flush();
							outLoa.close();
						} catch (IOException e) {
							logger.error(e);
							e.printStackTrace();
							System.exit(1);
						}
					}
					else{
						logger.error(om.getLOA_TYPE()+" Not Supported!!");
						System.err.println(om.getLOA_TYPE()+" Not Supported!!");
						System.exit(1);
					}
				}
		}
		if(om.isCsv()){
			Map<String,DBMetadata> tableMeta=cynapseInit.getDBTableModel().tableModel.get((tPrefix+context.t_name).toUpperCase());
			String filename = om.getCSV_FILE_PATTERN().replace("$TABLENAME",context.t_name);
			String dateFile=filename.contains("$DATETIMEID(")?filename.substring(filename.indexOf("$DATETIMEID(")+12, filename.indexOf(")")):"";
			String replacedDateFile=PostProcessGenerator.convertDate(context.dateTimeid,dateFile);
			filename=filename.replace("$DATETIMEID("+dateFile+")", replacedDateFile);
			
			if(tableMeta!=null)
				try {
					FileWriter out =  new  FileWriter(om.getCSV_FILE_LOCATION()+"/"+filename, true);
					out.write(PostProcessGenerator.GenerateCsvMapMysql(context,tableMeta, map)+"\n");
					out.flush();
					out.close();	
				} catch (IOException e) {
					logger.error(e);
					e.printStackTrace();
					System.exit(1);
				}
		}
		if(om.isJson()){
			String filename = om.GetJsonHeaderFile().replace("$TABLENAME",context.t_name);
			String dateFile=filename.contains("$DATETIMEID(")?filename.substring(filename.indexOf("$DATETIMEID(")+12, filename.indexOf(")")):"";
			String replacedDateFile=PostProcessGenerator.convertDate(context.dateTimeid,dateFile);
			filename=filename.replace("$DATETIMEID("+dateFile+")", replacedDateFile);
			
				try {
					FileWriter out =  new  FileWriter(om.GetJsonFileLocation()+"/"+filename, true);
					out.write(PostProcessGenerator.GenerateJson(map, context, om, jp)+"\n");
					out.flush();
					out.close();	
				} catch (IOException e) {
					logger.error(e);
					e.printStackTrace();
					System.exit(1);
				}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void DoInsertBatch(){
		for(Map.Entry<String, List<Map<String, Object>>> mdl :gmdl.groupedModel.entrySet()){
				SimpleJdbcInsert sdc = sdcs.get(mdl.getKey());
		        if(sdc==null){
			        sdc = new SimpleJdbcInsert(ds).withTableName(mdl.getKey());
			        sdcs.put(mdl.getKey(), sdc);
		        }
		    try{
		        List<Map<String, Object>> list = mdl.getValue();
		        logger.info("Loading data to table "+mdl.getKey()+" size:"+list.size());
	        		sdc.executeBatch(list.toArray(new LinkedHashMap[0]));
	        } catch (Exception  e){
				logger.error(e);
				System.out.println(e.getMessage());
//				e.printStackTrace();
			}
		}
	    
	    gmdl = new GroupedModels();
	}
	
	private void MergedModel(Map<String, Object> map, Context context){
		final String tPrefix = context.isloadWithPrefix?cynapseProp.getTABLE_PREFIX():"";
			ParrentModel parent = new ParrentModel(tPrefix+context.t_name,map);
				parent.SetEntryDate(context.date);
				parent.SetDateTimeId(context.dateTimeid);
				parent.SetGranularity(context.granularity);
				parent.SetSource(context.source);
				parent.SetNeId(context.ne_id);
				parent.SetSubNeId(context.sub_ne_id);
				parent.SetMoId(context.mo_id);
				parent.SetHashValue(context.hash_value);
				parent.SetVersion(context.version);
			gmdl.PutIt(parent);
	}
	
	
	private void rpcSendData(Map<String, Object> map, Context context){
		Map<String,String> headers =new LinkedHashMap<String, String>();
			headers.put("path",om.getRPC_PATH().replaceAll("\\$source",getSourceRPC(context, om)).replaceAll("\\$datetime",context.dateTimeid.substring(0, 13).replaceAll(":", "-")+"-00-00"));
	    
		Map<String, Object> json=new LinkedHashMap<String, Object>();
			json.put("TABLE_NAME",context.t_name);
			json.put("ENTRY_DATE",String.valueOf(context.date));
			json.put("DATETIME_ID",context.dateTimeid);
			json.put("SOURCE",context.source);
			json.put("VERSION", context.version);
			json.put("NE_ID",context.ne_id);
			json.put("MO_ID",context.mo_id);
			map.put("GRANULARITY",String.valueOf(context.granularity));
			json.put("DATA", map);
//			System.out.println(json.toString());
			if(intf!=null){
//				System.out.println(JSONValue.toJSONString(json));
				String row = jp.getJsonFormat(json);
				row = row.substring(1, row.length()-1);
				intf.sendData(row.getBytes(), headers);
			}
	}
	
	private String getSourceRPC(Context context, OutputMethodPropReader om){
		return om.getRPC_SOURCE_ID() ==null ? context.source : om.getRPC_SOURCE_ID();
	}

	public RowIncrementor getRowIncrementor(){
		return ri;
	}
}
