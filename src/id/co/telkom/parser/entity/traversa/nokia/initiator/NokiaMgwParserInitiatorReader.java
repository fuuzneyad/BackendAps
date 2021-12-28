package id.co.telkom.parser.entity.traversa.nokia.initiator;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.IgnoreException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.UnhandledCommandHandler;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.nokia.msscommand.InitiatorZNRICommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.msscommand.InitiatorZWVICommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.msscommand.UnhandledCommandHandlerInitFactory;

public class NokiaMgwParserInitiatorReader extends CommandParser{
	private static final Logger logger = Logger.getLogger(NokiaMgwParserInitiatorReader.class);
	private Map<String, InitiatorCommandHandlerFactory> commands = new HashMap<String, InitiatorCommandHandlerFactory>();
	private GlobalBuffer buf;
	private final Context ctx;
	
	private void addCommand(InitiatorCommandHandlerFactory commandHandlerFactory){
		commands.put("Z"+commandHandlerFactory.getCommand(), commandHandlerFactory);
		commands.put("ZZ"+commandHandlerFactory.getCommand(), commandHandlerFactory);
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, InitiatorCommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public NokiaMgwParserInitiatorReader(Reader reader, Context ctx, GlobalBuffer buf) {
		super(reader);
		this.ctx=ctx;
		this.buf=buf;
		addCommand("*",  new UnhandledCommandHandlerInitFactory());
		addCommand(new InitiatorZNRICommandHandlerFactory());
		addCommand(new InitiatorZWVICommandHandlerFactory());
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
						String command = null;
						String subCommand = null;
						skipUntilAlphabet();
						readWhileAlphaNumeric(sb);
						if(sb.length() > 0){
							command = sb.toString().toUpperCase();
							if(!isEOL()){
								read().readWhileAlphaNumeric(sb);
								if(sb.length() > 0){
									subCommand = sb.toString().toUpperCase();
								}
							}
						}else{
							throw new IgnoreException();
						}
						readUntilEOL(sb);
						trimRight(sb);
						String params = (subCommand!=null ? subCommand+sb.toString() : sb.toString()).toUpperCase();//yyn
						InitiatorCommandHandlerFactory commandHandlerFactory = commands.get(command+"_"+subCommand);//command+param
						
						if(commandHandlerFactory==null){
							commandHandlerFactory = commands.get(command);
						}

						if (commandHandlerFactory != null) {
							ctx.setCommand(command);
							ctx.setCommandParam(params);
							commandHandler = commandHandlerFactory.create(this, command, params,ctx, buf);
						} else {
							logger.error("Unhandled Command : "+ command +"  "+ params);
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this, command, params,ctx, buf);
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
						logger.error("Found unexpected characters at line " + getLine() + " : " + sb);
					}
				}
				skipEOLs();
			} catch (ParserException ex) {
				readUntilEOL(sb).skipEOLs();
				if (ex instanceof CommandErrorException || ex instanceof CompleteException) {
					commandHandler = null;
				} else if (ex instanceof IgnoreException) {
					logger.error("ignore char "+sb);
				} else {
					System.err.println("Parse error: " + ex.getMessage());
					if (commandHandler != null) {
						commandHandler = new UnhandledCommandHandler(this,  commandHandler.getCommand(), commandHandler.getParams());
					} else if (ex instanceof UnhandledCommandException) {
						UnhandledCommandException ex1 = (UnhandledCommandException) ex;
						commandHandler = new UnhandledCommandHandler(this, ex1.getCommand(), ex1.getParams());
						logger.error("command :"+ex1.getCommand()+"param :"+ex1.getParams()+" error Message:"+ex.getMessage());
					} else {
						commandHandler = null;
					}
				}
			}
		}
	}
}
