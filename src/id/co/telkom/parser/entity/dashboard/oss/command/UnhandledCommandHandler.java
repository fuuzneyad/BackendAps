package id.co.telkom.parser.entity.dashboard.oss.command;


import java.io.IOException;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;


public class UnhandledCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	
	public UnhandledCommandHandler(Parser extractor,String command, String params) {
		super(command, params);
		this.reader=extractor;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		while (!reader.isEOF() && !reader.isEqual('#')) {
			StringBuilder sb = new StringBuilder();
			reader.readUntilEOL(sb).skipEOLs();
		}
		done();
	}

}
