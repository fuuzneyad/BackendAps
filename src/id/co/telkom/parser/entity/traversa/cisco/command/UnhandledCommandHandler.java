package id.co.telkom.parser.entity.traversa.cisco.command;


import java.io.IOException;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;


public class UnhandledCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private final Parser reader;
	private static final Logger logger = Logger.getLogger(UnhandledCommandHandler.class);
	
	public UnhandledCommandHandler(Parser extractor,String command, String params) {
		super(command, params);
		this.reader=extractor;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		logger.info("Handling unhandled command :"+getCommand());
		StringBuilder sb = new StringBuilder();
		while (!reader.isEOF() && !reader.isEqual('>')) {
			reader.read(sb);
		}
		String ne = sb.toString().replace("-", "").toUpperCase();
		ctx.setNe_id(ne);
		done();
		return;
	}

}
