package id.co.telkom.parser.entity.traversa.nokia;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.IgnoreException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.UnhandledCommandHandler;
import id.co.telkom.parser.entity.traversa.nokia.msscommand.UnhandledCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.sgsncommand.ParserB6OCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.sgsncommand.ParserE61CommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.sgsncommand.ParserEJHCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.sgsncommand.ParserKAICommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.sgsncommand.ParserKAOCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.sgsncommand.ParserWVICommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.nokia.sgsncommand.ParserZEJLCommandHandlerFactory;

public class NokiaTraversaSgsnParserReader extends CommandParser{
	private static final Logger logger = Logger.getLogger(NokiaTraversaSgsnParserReader.class);
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private final Context ctx;
	private final DataListener listener;
	private AbstractInitiator cynapseInit;	
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put("Z"+commandHandlerFactory.getCommand(), commandHandlerFactory);
		commands.put("ZZ"+commandHandlerFactory.getCommand(), commandHandlerFactory);
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public NokiaTraversaSgsnParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		super(reader);
		this.ctx=ctx;
		this.listener=listener;
		this.cynapseInit=cynapseInit;
		
		addCommand("*",  new UnhandledCommandHandlerFactory());
		addCommand(new ParserZEJLCommandHandlerFactory());
		addCommand(new ParserKAICommandHandlerFactory());
		addCommand(new ParserE61CommandHandlerFactory());
		addCommand(new ParserEJHCommandHandlerFactory());
		addCommand(new ParserKAOCommandHandlerFactory());
		addCommand(new ParserWVICommandHandlerFactory());
		addCommand(new ParserB6OCommandHandlerFactory());
		
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
						CommandHandlerFactory commandHandlerFactory = commands.get(command+"_"+subCommand);//command+param
						
						if(commandHandlerFactory==null){
							commandHandlerFactory = commands.get(command);
						}

						if (commandHandlerFactory != null) {
							ctx.setCommand(command);
							ctx.setCommandParam(params);
							commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
							if(commandHandler==null)
								commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
						} else {
							System.err.println("Unhandled Command : "+ command +"  "+ params);
							logger.error("Unhandled Command : "+ command +"  "+ params);
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
								if(commandHandler==null)
									commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
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
