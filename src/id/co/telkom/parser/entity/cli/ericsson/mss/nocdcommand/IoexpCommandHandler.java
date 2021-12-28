package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;


import java.io.IOException;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class IoexpCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	
	public IoexpCommandHandler(Parser reader, DataListener listener, String command, String params) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
				reader.skipLines(2);
				StringBuilder sb = new StringBuilder();
				reader.readUntil(' ', sb);
				String nodeElement = sb.toString().trim();
				ctx.setNe_id(nodeElement);
				reader.readUntilEOL(sb);
				String desc = sb.toString();
				listener.onNodeElement(reader.getLine(), ctx, nodeElement, desc);
				reader.skipLines(1);
				done();
	}

}
