package id.co.telkom.parser.entity.traversa.ericsson;

import java.io.IOException;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;


public class UnhandledSgsnCommandHandler extends AbstractCommandHandler implements CommandHandler{
	
	private final Parser reader;
	public UnhandledSgsnCommandHandler(Parser parser, String command, String params) {
		super(command, params);
		this.reader=parser;
	}

	@Override
	public void handle(Context ctx)
			throws IOException {
		while (!reader.isEOF() && !reader.isEqual('=')) {
			StringBuilder sb = new StringBuilder();
			reader.readUntilEOL(sb).skipEOLs();
		}
		done();
	}

}
