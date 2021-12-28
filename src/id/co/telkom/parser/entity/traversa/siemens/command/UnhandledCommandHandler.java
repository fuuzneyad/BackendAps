package id.co.telkom.parser.entity.traversa.siemens.command;

import java.io.IOException;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;

public class UnhandledCommandHandler extends AbstractCommandHandler implements CommandHandler{
	private Parser reader;
	public UnhandledCommandHandler(Parser reader,String command, String params) {
		super(command, params);
		this.reader=reader;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		while (!reader.isEOF() && !reader.isEqual('D')) {
			reader.skipUntilEOL().skipEOLs();
		}
		done();
	}

}
