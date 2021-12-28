package id.co.telkom.parser.entity.traversa.ericsson.initiator;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.ParseException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.InitiatorC7SPPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.InitiatorMGCAPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.InitiatorMGRRPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.UnhandledCommandHandler;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;


public class EricssonMscParserInitiatorReader extends CommandParser  {
	private final Context ctx;
	private Map<String, InitiatorCommandHandlerFactory> commands = new HashMap<String, InitiatorCommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(EricssonMscParserInitiatorReader.class);
	private GlobalBuffer buf;
	
	private void addCommand(InitiatorCommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	@SuppressWarnings("unused")
	private void addCommand(String key, InitiatorCommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public EricssonMscParserInitiatorReader(Reader reader, Context ctx, GlobalBuffer buf) {
		super(reader);
		this.ctx=ctx;
		this.buf=buf;
		
		addCommand(new InitiatorC7SPPCommandHandlerFactory());//Own SP
		addCommand(new InitiatorMGCAPCommandHandlerFactory());//Own GT
		addCommand(new InitiatorMGRRPCommandHandlerFactory());//Own Msrn//TODO: check logic
		
	}
	
	public void parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		read();
		CommandHandler commandHandler = null;
		while (!isEOF()) {
			try {
				notEOL : {
					if (commandHandler != null) {
						commandHandler.handle(ctx);
						if (commandHandler.isDone()) {
							commandHandler = null;
						}
						break notEOL;
					}
					if (isEqual('<')) {
						readUntilEOL(sb);
						trimRight(sb);
						String s = sb.toString();
						if (s.charAt(s.length() - 1) != ';') {
							int end = s.indexOf(';');
							s = s.substring(end + 1).trim();
							if (s.charAt(0) != '!') {
								throw new ParseException("Found unexpected characters at line " + getLine() + " : " + sb);
							}
							s = sb.substring(1, end);
						} else {
							s = s.substring(1, s.length() - 1);
						}
						int i = s.indexOf(':');
						String command = i == -1 ? s : s.substring(0, i);
						command = command.toUpperCase().trim();
						String params = i == -1 ? null : s.substring(s.indexOf(':') + 1);
						ctx.setCommand(command);
						ctx.setCommandParam(params);
						InitiatorCommandHandlerFactory commandHandlerFactory = commands.get(command);
						
						if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, ctx, buf);
						} else {
//							System.err.println("Unhandled Command : "+ command +" : "+ params);
							logger.error("Unhandled Command : "+ command +" : "+ params);
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, ctx, buf);
							} else {
								throw new UnhandledCommandException(command, params);
							}
						}
					} else {
						if (getLine() == 1) {
							skipUntilEOL().skipEOLs();
							continue;
						}
						readUntilEOL(sb);
						throw new ParseException("Found unexpected characters at line " + getLine() + " : " + sb);
					}
				}
				skipEOLs();
			} catch (ParserException ex) {
				readUntilEOL(sb).skipEOLs();
				if (ex instanceof CommandErrorException || ex instanceof CompleteException) {
					commandHandler = null;
				} else {
					if (commandHandler != null) {
						commandHandler = new UnhandledCommandHandler(this,  commandHandler.getCommand(), commandHandler.getParams());
					} else if (ex instanceof UnhandledCommandException) {
						UnhandledCommandException ex1 = (UnhandledCommandException) ex;
						commandHandler = new UnhandledCommandHandler(this,  ex1.getCommand(), ex1.getParams());
					} else {
						commandHandler = null;
					}
				}
			}
		}
	}
	
}
