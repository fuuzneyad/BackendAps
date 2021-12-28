package id.co.telkom.parser.entity.dashboard.oss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class OssDashboardZte10 extends AbstractParser{
	private static final Logger logger = Logger.getLogger(OssDashboardZte10.class);
	public OssDashboardZte10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		
	}

	@Override
	protected void ProcessFile(File file,final LoaderHandlerManager loader,
			Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());

		loader.onBeginFile();
		ctx.setNe_id(file.getName().split("_")[0]);
		String[] d = file.getName().split("_");
		if(d.length>=3)
			ctx.setDatetimeid(convertDate(d[1]+""+d[2].split("\\.")[0]));
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		DataListener listener = new DataListener(){
			@Override
			public void onReadyData(Context ctx, Map<String, Object> map, int line) {
				super.onReadyData(ctx, map, line);
				if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
					map.put("LINE", line);
					map.put("COMMAND_PARAM", ctx.commandParam);
					loader.onReadyModel(map, ctx);
				}else{
					for(Map.Entry<String, Object> mp:map.entrySet()){
						PutModel(ctx.t_name, mp.getKey(), mp.getValue()==null?"":mp.getValue().toString());
					}
				}
			}
				
			};
			new OssDashboardParserReader(reader, listener, ctx).parse();
			reader.close();
			loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"OSS_Schema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
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
	private static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return "0000-00-00 00:00:00";}
	}
}
