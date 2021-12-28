package id.co.telkom.parser.entity.dashboard.oss;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.ParseException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.dashboard.oss.command.DfCommandHandlerFactory;
import id.co.telkom.parser.entity.dashboard.oss.command.IpscanCommandHandlerFactory;
import id.co.telkom.parser.entity.dashboard.oss.command.PerformanceCommandHandlerFactory;
import id.co.telkom.parser.entity.dashboard.oss.command.PrstatCommandHandlerFactory;
import id.co.telkom.parser.entity.dashboard.oss.command.ServiceCommandHandlerFactory;
import id.co.telkom.parser.entity.dashboard.oss.command.UnhandledCommandHandler;
import id.co.telkom.parser.entity.dashboard.oss.command.UptimeCommandHandlerFactory;
import id.co.telkom.parser.entity.dashboard.oss.command.VmstatCommandHandlerFactory;



public class OssDashboardParserReader extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(OssDashboardParserReader.class);
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	@SuppressWarnings("unused")
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public OssDashboardParserReader(Reader reader, DataListener listener, Context ctx) {
		super(reader);
		this.listener=listener;
		this.ctx=ctx;
		addCommand(new DfCommandHandlerFactory());
		addCommand(new VmstatCommandHandlerFactory());
		addCommand(new PrstatCommandHandlerFactory());
		addCommand(new ServiceCommandHandlerFactory());
		addCommand(new UptimeCommandHandlerFactory());
		addCommand(new IpscanCommandHandlerFactory());
		addCommand(new PerformanceCommandHandlerFactory());
	}
	
	public StringBuffer GenerateSchema(){
		StringBuffer sb = new StringBuffer();

		Map<CommandHandlerFactory,String> uniq = new LinkedHashMap<CommandHandlerFactory,String>();
		for(Map.Entry<String,CommandHandlerFactory> mp:commands.entrySet()){
			uniq.put(mp.getValue(), "");
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
			try {
				notEOL : {
					if (commandHandler != null) {
						commandHandler.handle(ctx);
						if (commandHandler.isDone()) {
							listener.onEndCommand(ctx, commandHandler.toString(),null,getLine());
							commandHandler = null;
						}
						break notEOL;
					}
					if (isEqual('#')) {
						read();
						readUntilEOL(sb);
						trimRight(sb);
						String s = sb.toString().replace("./", "");
						if(!s.startsWith("executing")&& !s.equals("")){
							int i = s.indexOf(':');
							String command = i == -1 ? s : s.substring(0, i);
							command = command.toUpperCase().trim();
							String params = i == -1 ? null : s.substring(s.indexOf(':') + 1);
							if(command.trim().indexOf(" ")>-1){
								params=command.substring(command.indexOf(" ")+1).trim();
								command=command.substring(0,command.indexOf(" ")+1).trim();
							}
							listener.onBeginCommand(ctx, command, params, getLine());
							ctx.setCommand(command);
							ctx.setCommandParam(params);
							CommandHandlerFactory commandHandlerFactory = commands.get(command);
							
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx);
							} else {
								System.err.println("Unhandled Command : "+ command +" : "+ params);
								logger.error("Unhandled Command : "+ command +" : "+ params);
								commandHandlerFactory = commands.get("*");
								if (commandHandlerFactory != null) {
									commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx);
								} else {
									throw new UnhandledCommandException(command, params);
								}
							}
						}
					} else {
						if (getLine() == 1) {
							skipUntilEOL().skipEOLs();
							continue;
						}
						readUntilEOL(sb);
						logger.error("ParseException: Found unexpected characters at line " + getLine() + " : " + sb);
						throw new ParseException("Found unexpected characters at line " + getLine() + " : " + sb);
					}
				}
				skipEOLs();
			} catch (ParserException ex) {
				readUntilEOL(sb).skipEOLs();
				if (ex instanceof CommandErrorException || ex instanceof CompleteException) {
					commandHandler = null;
				} else {
					listener.onParseError(getLine(), ctx, getColumn(), "Parse error: " + ex.getMessage(), ex);
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
