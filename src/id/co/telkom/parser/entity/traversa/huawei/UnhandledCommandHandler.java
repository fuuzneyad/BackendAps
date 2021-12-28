package id.co.telkom.parser.entity.traversa.huawei;

import java.io.IOException;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;


public class UnhandledCommandHandler extends AbstractCommandHandler implements CommandHandler{
	
	private final Parser reader;
	public UnhandledCommandHandler(Parser parser, String command, String params) {
		super(command, params);
		this.reader=parser;
	}

	@Override
	public void handle(Context ctx)
			throws IOException {
		while (!reader.isEOF() && !reader.isEqual('(')) {
			StringBuilder sb = new StringBuilder();
			reader.readUntilEOL(sb).skipEOLs();
			String s = sb.toString();
			if (s.equals("NOT ACCEPTED")) {//What in HLR
				reader.readUntilEOL(sb);
				throw new CommandErrorException();
			}
		}
		done();
	}

}
