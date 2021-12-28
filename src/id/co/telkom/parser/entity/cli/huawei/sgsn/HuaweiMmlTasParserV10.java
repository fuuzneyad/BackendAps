package id.co.telkom.parser.entity.cli.huawei.sgsn;

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

public class HuaweiMmlTasParserV10 extends AbstractParser {
	private ParserPropReader cynapseProp;
	private static final Logger logger = Logger.getLogger(HuaweiMmlTasParserV10.class);
	
	public HuaweiMmlTasParserV10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.cynapseProp=cynapseProp;
	}
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());

		loader.onBeginFile();
		String tgl =file.getName().replace("MmlTaskResult_", "").replace(".txt", "");
			   tgl =tgl.substring(0,tgl.length()-3).replace("T", " ");
			   ctx.setDatetimeid(convertDate(tgl));
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
			
			DataListener listener = new DataListener(){
				@Override
				public void onReadyData(Context ctx, Map<String, Object> map, int line) {
					super.onReadyData(ctx, map, line);
						map.put("LINE", line);
						map.put("COMMAND_PARAM", ctx.commandParam);
						loader.onReadyModel(map, ctx);
				}
			};
			new HuaweiMmlTaskParserReaderV10(reader, listener, ctx).parse();
			reader.close();
			loader.onEndFile();
		}
		
		
	}
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiCLISchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			StringBuffer schema = new HuaweiMmlTaskParserReaderV10(null, null, null).GenerateSchema();
			out.write(schema.toString());
			System.out.println(schema);
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private  String convertDate(String val) {
		String format;
		if(val.length()=="ddMMyy".length())
			format="ddMMyy";
		else
			format="yyyy-MM-dd HH-mm";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		try{
			return myFormat.format(fromUser.parse(val)).trim();
		}catch(ParseException e){return val;}
	}

}
