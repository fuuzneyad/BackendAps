package id.co.telkom.parser.entity.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class MyParser extends AbstractParser {

	public MyParser(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		//tempat masukin logic
		loader.onBeginFile();
		System.out.println(file.getName());
		System.out.println(ctx.date);
		ctx.setTableName("HENDRA");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("MYFIELD1", file.getName());
		map.put("MYFIELD2", 2);
		map.put("MYFIELD3", 3);
//		System.out.println(map);
		ctx.setLoadWithPrefix(false);
		loader.onReadyModel(map, ctx);
		loader.onEndFile();
		
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
	}

}
