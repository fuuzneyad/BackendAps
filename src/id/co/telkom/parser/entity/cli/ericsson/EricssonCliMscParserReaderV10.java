package id.co.telkom.parser.entity.cli.ericsson;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.IgnoreException;
import id.co.telkom.parser.common.charparser.ParseException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.DbtspCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.DirCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.EreppCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.IoexpCommandHandler;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.IoexpCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgaapCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgbspCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgcapCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgcepCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgmapCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgmgpCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgptpCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgripCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.MgsvpCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.NrgwpCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.PlldpCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.RaeppCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.SqrepCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand.UnhandledCommandHandler;


@SuppressWarnings("unused")
public class EricssonCliMscParserReaderV10 extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(EricssonCliMscParserReaderV10.class);
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public EricssonCliMscParserReaderV10(Reader reader, DataListener listener, Context ctx) {
		super(reader);
		this.listener=listener;
		this.ctx=ctx;
		
		addCommand(new IoexpCommandHandlerFactory());
		addCommand(new PlldpCommandHandlerFactory());
		addCommand(new MgsvpCommandHandlerFactory());
		addCommand(new MgbspCommandHandlerFactory());
		addCommand(new MgripCommandHandlerFactory());
		addCommand(new MgmapCommandHandlerFactory());
		addCommand(new NrgwpCommandHandlerFactory());
		addCommand(new MgcepCommandHandlerFactory());
		addCommand(new MgaapCommandHandlerFactory());
		addCommand(new MgptpCommandHandlerFactory());
		addCommand(new MgcapCommandHandlerFactory());
		addCommand(new EreppCommandHandlerFactory());
		addCommand(new DirCommandHandlerFactory());
		addCommand(new MgmgpCommandHandlerFactory());
		addCommand(new SqrepCommandHandlerFactory());
		
//		addCommand(new RaeppCommandHandlerFactory());
//		addCommand(new DbtspCommandHandlerFactory());
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
