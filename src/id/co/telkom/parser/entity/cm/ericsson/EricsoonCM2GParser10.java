package id.co.telkom.parser.entity.cm.ericsson;

import java.io.File;
import java.io.FileReader;

import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class EricsoonCM2GParser10 extends AbstractParser {

	public EricsoonCM2GParser10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		String date_time= file.getName().contains("_201")? file.getName().substring(file.getName().indexOf("201"), file.getName().indexOf("201")+8):null;
		ctx.setDatetimeid(date_time);
		CSVReader reader = new CSVReader(new FileReader(file), ' ', '"');
		 String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		        System.out.println(nextLine[0] + nextLine[1] + "etc...");
		    }
		    reader.close();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
	}

}
