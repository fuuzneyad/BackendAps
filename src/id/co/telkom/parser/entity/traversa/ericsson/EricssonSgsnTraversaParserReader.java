package id.co.telkom.parser.entity.traversa.ericsson;

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
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.UnhandledCommandHandler;
import id.co.telkom.parser.entity.traversa.ericsson.sgsncommand.ParserGSHLISTRACommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.sgsncommand.ParserGSHLISTServiceAddressCommandHandlerFactory;


public class EricssonSgsnTraversaParserReader extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private static final Logger logger = Logger.getLogger(EricssonSgsnTraversaParserReader.class);
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private AbstractInitiator cynapseInit;
	
	public EricssonSgsnTraversaParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		super(reader);
		this.ctx=ctx;
		this.listener=listener;
		this.cynapseInit=cynapseInit;
		
		addCommand("*", new UnhandledSgsnCommandHandlerFactory());
		addCommand(new ParserGSHLISTRACommandHandlerFactory());
		addCommand(new ParserGSHLISTServiceAddressCommandHandlerFactory());
		
	}
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
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
					if (isEqual('=')) {
						readUntilEOL(sb);
						
						String[] splited = sb.toString().trim().split("\\s+");
						for(String s:splited){
							if(s.contains("SG")){
								ctx.setNe_id(s);
								break;
							}
						}
						String c = sb.toString().contains("#")?sb.toString().split("#")[1]:sb.toString();
							   c =  c.toUpperCase().replace("GSH ", "GSH_").trim();
						String command = c.split(" ")[0];
						String params= c.contains(" ")? c.split(" ")[1]:null;							
						
						
						ctx.setCommandParam(params);
						System.out.println("command "+command+" param "+params);
						
	/*handle block*/					
						CommandHandlerFactory commandHandlerFactory = commands.get(command);
												
						if (commandHandlerFactory != null) {
							commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
						} else {
							logger.error("Unhandled Command : {"+command+"} : {"+params+"}");
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
							} else {
								throw new UnhandledCommandException(command, params);
							}
						}
						
	/*handle block*/
					} else {
						readUntilEOL(sb);
						throw new ParserException("Found unexpected characters at line " + getLine() + " : " + sb);
						
					}
				}
				skipEOLs();
			} catch (ParserException ex) {
				readUntilEOL(sb).skipEOLs();

				if (ex instanceof CommandErrorException || ex instanceof CompleteException) {
					commandHandler = null;
				} else {
					if (commandHandler != null) {
						commandHandler = new UnhandledCommandHandler(this, commandHandler.getCommand(), commandHandler.getParams());
					} else if (ex instanceof UnhandledCommandException) {
						UnhandledCommandException ex1 = (UnhandledCommandException) ex;
						commandHandler = new UnhandledSgsnCommandHandler(this, ex1.getCommand(), ex1.getParams());
					} else {
						commandHandler = null;
					}
				}
			}
		}
	}
	
}
