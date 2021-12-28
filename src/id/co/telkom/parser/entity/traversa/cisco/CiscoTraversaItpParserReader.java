package id.co.telkom.parser.entity.traversa.cisco;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.cisco.command.ParserCs7AsCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.cisco.command.ParserCs7GttConfigCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.cisco.command.ParserCs7PointCodeCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.cisco.command.UnhandledCommandHandlerFactory;

public class CiscoTraversaItpParserReader extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(CiscoTraversaItpParserReader.class);
	private AbstractInitiator cynapseInit;
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public CiscoTraversaItpParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		super(reader);
		this.listener=listener;
		this.ctx=ctx;
		this.cynapseInit=cynapseInit;
		
		addCommand("",new UnhandledCommandHandlerFactory());
		addCommand(new ParserCs7PointCodeCommandHandlerFactory());
		addCommand(new ParserCs7GttConfigCommandHandlerFactory());
		addCommand(new ParserCs7AsCommandHandlerFactory());
	}
	
	public StringBuffer GenerateSchema(){
		StringBuffer sb = new StringBuffer();

		Map<CommandHandlerFactory,String> uniq = new LinkedHashMap<CommandHandlerFactory,String>();
		for(Map.Entry<String,CommandHandlerFactory> mp:commands.entrySet()){
			uniq.put(mp.getValue(), null);
		}
		for(Map.Entry<CommandHandlerFactory,String> u:uniq.entrySet()){
			sb.append(u.getKey().getTableSchema());
		}
		return sb;
	}
	
	public void parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		read();
		CommandHandler commandHandler = null;
		while (!isEOF()) {
			if(isEqual('>')){
				read();
				readUntilEOL(sb).skipEOL();
				String command = sb.toString().replace("-", "_").replace(" ", "_").replace("show_", "");
				   command = command.toUpperCase().trim();
				String params = null;
				listener.onBeginCommand(ctx, command, params, getLine());
				ctx.setCommand(command);
				ctx.setCommandParam(params);
				CommandHandlerFactory commandHandlerFactory = commands.get(command);
				
				if (commandHandlerFactory != null) {
					commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx);
					if(commandHandler==null)
						commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
				}else {
					System.err.println("Unhandled Command : "+ command +" : "+ params);
					logger.error("Unhandled Command : "+ command +" : "+ params);
					commandHandlerFactory= new UnhandledCommandHandlerFactory(command);
					commandHandler = commandHandlerFactory.create(this, command, params, listener, ctx);
				}
				
				if(commandHandler!=null){
					commandHandler.handle(ctx);
					if (commandHandler.isDone()) {
						listener.onEndCommand(ctx, commandHandler.toString(),null,getLine());
						commandHandler = null;
					}
				}
			}else
				read(sb);
		}
	}
	
}
