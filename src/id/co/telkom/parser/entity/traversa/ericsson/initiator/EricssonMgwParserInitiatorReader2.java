package id.co.telkom.parser.entity.traversa.ericsson.initiator;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class EricssonMgwParserInitiatorReader2 extends Parser {
	private Map<String, InitiatorCommandHandlerFactory> commands = new HashMap<String, InitiatorCommandHandlerFactory>();
	private final Context ctx;
	private GlobalBuffer buf;
	
	public EricssonMgwParserInitiatorReader2(Reader reader, Context ctx, GlobalBuffer buf) {
		super(reader);
		this.ctx=ctx;
		this.buf=buf;
	}
	
	public void parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		read();
		CommandHandler commandHandler = null;
		while (!isEOF()) {
			notEOL : {
				if (commandHandler != null) {
					commandHandler.handle(ctx);
					if (commandHandler.isDone()) {
						commandHandler = null;
					}
					break notEOL;
				}
				sb.setLength(0);
				while (isAlphaNumeric() || isEqual('_')) {
					appendTo(sb).read();
				}
				if (isEqual('>')) {
					if (sb.length() > 0) {
						read();
						if (isWhiteSpace()) {
							String nodeElement = sb.toString();
							System.out.println("NODE ELEMENT :"+nodeElement);
							skipWhile(' ').readUntil(' ', sb);//read command
							final String command = sb.toString().trim();
							readUntilEOL(sb);//read param
//							System.out.println("COMMAND:"+command+" PARAM :"+sb);
							final String params = sb.toString().trim();
							
							InitiatorCommandHandlerFactory commandHandlerFactory = getCommands().get(command);
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, ctx, buf);
							} else {
								commandHandlerFactory = getCommands().get("*");
								if (commandHandlerFactory != null) {
									commandHandler = commandHandlerFactory.create(this,command, params, ctx, buf);
								} else {
									//throw new UnhandledCommandException(command, params);
								}
							}
						} else if (!isEOL()) {
							throw new Error();
						}
					}
				} else {
					sb.setLength(0);
					readUntilEOL(sb);
				}
			}
			skipEOLs();
		}
	}

	public Map<String, InitiatorCommandHandlerFactory> getCommands() {
		return commands;
	}
}
