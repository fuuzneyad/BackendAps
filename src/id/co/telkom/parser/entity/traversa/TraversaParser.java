package id.co.telkom.parser.entity.traversa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.charparser.InputStreamWrapper;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.traversa.cisco.CiscoTraversaItpParserReader;
import id.co.telkom.parser.entity.traversa.common.EdgeContext;
import id.co.telkom.parser.entity.traversa.ericsson.EricssonSgsnTraversaParserReader;
import id.co.telkom.parser.entity.traversa.ericsson.EricssonTraversaMscParserReader;
import id.co.telkom.parser.entity.traversa.huawei.HuaweiHlrTraversaParserReader;
import id.co.telkom.parser.entity.traversa.huawei.HuaweiSgsnTraversaParserReader;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.nokia.NokiaTraversaMgwParserReader;
import id.co.telkom.parser.entity.traversa.nokia.NokiaTraversaMscParserReader;
import id.co.telkom.parser.entity.traversa.nokia.NokiaTraversaSgsnParserReader;
import id.co.telkom.parser.entity.traversa.siemens.SiemensTraversaRGParserReader;
import id.co.telkom.parser.entity.traversa.siemens.SiemensTraversaSTPParserReader;

public class TraversaParser extends AbstractParser {

	public TraversaParser(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, final LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		
		InputStreamReader reader=null;
		DataListener listener = new DataListener(){
				@Override
				public void onReadyData(Context ctx, Map<String, Object> map, int line) {
					super.onReadyData(ctx, map, line);
					ctx.setLoadWithPrefix(true);
					if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
						map.put("LINE", line);
						map.put("COMMAND_PARAM", ctx.commandParam);
						loader.onReadyModel(map, ctx);
					}else{
						for(Map.Entry<String, Object> mp:map.entrySet()){
							PutModel(ctx.t_name, mp.getKey(), mp.getValue()==null?"":mp.getValue().toString());
						}
					}
				}
				
				@Override
				public void onTopologyData(Context ctx, EdgeContext edctx){
					ctx.setTableName(edctx.getT_name());
					ctx.setLoadWithPrefix(true);
					Map<String, Object> map = new LinkedHashMap<String, Object>();
						map.put("EDGE_ID", edctx.getEdge_id());
						map.put("SOURCE", edctx.getEdge_source());
						map.put("DEST", edctx.getEdge_dest());
						map.put("DEST_PC", edctx.getEdge_destPC());
						map.put("TYPE", edctx.getEdge_type());
						map.put("EDGE_TYPE", edctx.getEdge_link_type());
						map.put("WEIGHT", edctx.getEdgeWeight());
						map.put("ADDITIONAL_INFO", edctx.getEdge_additional_param());
					if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
						map.put("COMMAND_PARAM", ctx.commandParam);
						loader.onReadyModel(map, ctx);
					}else{
						for(Map.Entry<String, Object> mp:map.entrySet())
							PutModel(edctx.getT_name(), mp.getKey(), mp.getValue()==null?"":mp.getValue().toString());
					}
				}
				
			};
			
		//Set Datetime ID & NE_ID
		String delimiter = file.getName().contains("_")?"_":"-"; 
		String[] spt = file.getName().split(delimiter);
		ctx.setNe_id(spt[0]);
		String dt = "0000-00-00 00:00:00"; 
		for(String s:spt){
			dt = convertDate(cynapseProp.getSOURCE_ID(), s.split("\\.")[0]);
			if(dt!=null)
				break;
		}
		ctx.setDatetimeid(dt);

		//process files by specific SOURCE_ID
		if(cynapseProp.getSOURCE_ID().equals("E_MSC")){
			reader = new InputStreamReader(new FileInputStream(file));
			new EricssonTraversaMscParserReader(reader, listener, ctx, parserInit).parse();
		}else
		if(cynapseProp.getSOURCE_ID().equals("E_SGSN")){
			reader = new InputStreamReader(new FileInputStream(file));
			new EricssonSgsnTraversaParserReader(reader, listener, ctx, parserInit).parse();
		}else	
		if(cynapseProp.getSOURCE_ID().equals("N_MSC")){
			InputStreamWrapper wrapper = new InputStreamWrapper(new FileInputStream(file));
			reader = new InputStreamReader(wrapper);
			new NokiaTraversaMscParserReader(reader, listener, ctx, parserInit).parse();
			wrapper.close();
		}else
		if(cynapseProp.getSOURCE_ID().equals("N_MGW")){
			InputStreamWrapper wrapper = new InputStreamWrapper(new FileInputStream(file));
			reader = new InputStreamReader(wrapper);
			new NokiaTraversaMgwParserReader(reader, listener, ctx, parserInit).parse();
			wrapper.close();
		}else
		if(cynapseProp.getSOURCE_ID().equals("N_SGSN")){
			InputStreamWrapper wrapper = new InputStreamWrapper(new FileInputStream(file));
			reader = new InputStreamReader(wrapper);
			new NokiaTraversaSgsnParserReader(reader, listener, ctx, parserInit).parse();
			wrapper.close();
		}else
		if(cynapseProp.getSOURCE_ID().equals("S_STP")){
			reader = new InputStreamReader(new FileInputStream(file));
			new SiemensTraversaSTPParserReader(reader, listener, ctx, parserInit).parse();
		}else
		if(cynapseProp.getSOURCE_ID().equals("H_HLR")){
			reader = new InputStreamReader(new FileInputStream(file));
			new HuaweiHlrTraversaParserReader(reader, listener, ctx, parserInit).parse();
		}else
		if(cynapseProp.getSOURCE_ID().equals("H_SGSN")){
			reader = new InputStreamReader(new FileInputStream(file));
			new HuaweiSgsnTraversaParserReader(reader, listener, ctx, parserInit).parse();
		}else	
		if(cynapseProp.getSOURCE_ID().equals("C_ITP")){
			reader = new InputStreamReader(new FileInputStream(file));
			new CiscoTraversaItpParserReader(reader, listener, ctx, parserInit).parse();
		}else
		if(cynapseProp.getSOURCE_ID().equals("S_RG")){
			reader = new InputStreamReader(new FileInputStream(file));
			new SiemensTraversaRGParserReader(reader, listener, ctx, parserInit, file.getName()).parse();
		}else	
			System.err.println("There is no parser handler for :"+cynapseProp.getSOURCE_ID()+" !");
		
		if(reader!=null)
			reader.close();
		
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(final LoaderHandlerManager loader, Context ctx)
			throws Exception {
		//Load buffer if any..
		loader.onBeginFile();
		DataListener listener = new DataListener(){
			@Override
			public void onReadyData(Context ctx, Map<String, Object> map, int line) {
				super.onReadyData(ctx, map, line);
				if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
					map.put("LINE", line);
					map.put("COMMAND_PARAM", ctx.commandParam);
					loader.onReadyModel(map, ctx);
				}else{
					for(Map.Entry<String, Object> mp:map.entrySet()){
						PutModel(ctx.t_name, mp.getKey(), mp.getValue()==null?"":mp.getValue().toString());
					}
				}
			}
			@Override
			public void onTopologyData(Context ctx, EdgeContext edctx){
				ctx.setTableName(edctx.getT_name());
				ctx.setLoadWithPrefix(true);
				Map<String, Object> map = new LinkedHashMap<String, Object>();
					map.put("EDGE_ID", edctx.getEdge_id());
					map.put("SOURCE", edctx.getEdge_source());
					map.put("DEST", edctx.getEdge_dest());
					map.put("DEST_PC", edctx.getEdge_destPC());
					map.put("TYPE", edctx.getEdge_type());
					map.put("EDGE_TYPE", edctx.getEdge_link_type());
					map.put("WEIGHT", edctx.getEdgeWeight());
					map.put("ADDITIONAL_INFO", edctx.getEdge_additional_param());
				if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
					map.put("COMMAND_PARAM", ctx.commandParam);
					loader.onReadyModel(map, ctx);
				}else{
					for(Map.Entry<String, Object> mp:map.entrySet()){
						PutModel(edctx.getT_name(), mp.getKey(), mp.getValue()==null? "" : mp.getValue().toString());
					}
				}
			}
		};
		//load vertex
			TraversaBufferLoader.LoadVertex(listener, ctx, (GlobalBuffer)parserInit.getMappingModel());
		//Then, Load Buffer by specific SOURCE_ID
		if(cynapseProp.getSOURCE_ID().equals("E_MSC")){
			TraversaBufferLoader.LoadEMscTopologyV10(listener, ctx, (GlobalBuffer)parserInit.getMappingModel());
		}else if(cynapseProp.getSOURCE_ID().equals("C_ITP")){
			TraversaBufferLoader.LoadCItpTopologyV10(listener, ctx, (GlobalBuffer)parserInit.getMappingModel());
		}else if(cynapseProp.getSOURCE_ID().equals("S_STP")){
			TraversaBufferLoader.LoadSStpTopologyV10(listener, ctx, (GlobalBuffer)parserInit.getMappingModel());
		}
		loader.onEndFile();
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+cynapseProp.getSOURCE_ID()+"_TraversaSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
				sb.append("-- Common Schemas..\n");
				sb.append("/*Schema for Vertex*/\n");
				sb.append("CREATE TABLE IF NOT EXISTS VERTEX (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',\n");
				sb.append("\t`NE_ID` varchar(50) DEFAULT NULL,\n");
				sb.append("\t`NE_NAME` varchar(50) DEFAULT NULL,\n");
				sb.append("\t`VENDOR` varchar(50) DEFAULT NULL,\n");
				sb.append("\t`OWN_SP_DEC` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`OWN_GT` text DEFAULT NULL,\n");
				sb.append("\t`OWN_MSRN` text DEFAULT NULL,\n");
				sb.append("\t`IP` varchar(400) DEFAULT NULL\n");
				sb.append("\n)Engine=InnoDB;\n");
				sb.append("\n");
			sb.append("-- Raw Schemas..\n");
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE  IF NOT EXISTS "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT '',\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`LINE` Integer(20) DEFAULT 0,\n");
				sb.append("\t`COMMAND_PARAM` varchar(100) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					String typeData = "VARCHAR("+(entry2.getValue().length()+100)+"),\n"; 
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
				}
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=InnoDB;\n");
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
					
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private static String convertDate(String OSS, String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		if(val.length()=="yyyyMMdd".length())
			format="yyyyMMdd";else
		format="yyyyMMddHHmmss";
		
		if(OSS.equals("S_STP"))
			format="yyyyddMM";
		if(OSS.equals("S_RG"))//201406041401818401
			format="yyyyMMddHHS";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return null /*"0000-00-00 00:00:00"*/;}
	}
	
//	public static void main (String[] args){
////		System.out.println(convertDate("S_RG","201406041401818401"));
//	}
}
