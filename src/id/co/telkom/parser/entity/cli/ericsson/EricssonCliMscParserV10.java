package id.co.telkom.parser.entity.cli.ericsson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class EricssonCliMscParserV10 extends AbstractParser {
	private ParserPropReader cynapseProp;
	private static final Logger logger = Logger.getLogger(EricssonCliMscParserV10.class);
	
	public EricssonCliMscParserV10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.cynapseProp=cynapseProp;
	}
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());

		loader.onBeginFile();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
			
			DataListener listener = new DataListener(){
				@Override
				public void onReadyData(Context ctx, Map<String, Object> map, int line) {
					super.onReadyData(ctx, map, line);
					map.put("LINE", line);
					map.put("COMMAND_PARAM", ctx.commandParam);
//					System.out.println(ctx.toString()+map);
					loader.onReadyModel(map, ctx);
				}
				
			};
			new EricssonCliMscParserReaderV10(reader, listener, ctx).parse();
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
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"EricssonCLISchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			StringBuffer schema = new EricssonCliMscParserReaderV10(null, null, null).GenerateSchema();
			out.write(schema.toString());
			System.out.println(schema);
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
