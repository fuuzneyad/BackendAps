package id.co.telkom.parser.entity.cli.nokia;

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
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.UnhandledCommandHandler;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.UnhandledCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.Ze2iCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.Ze3iCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZedoCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZifoCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZjgiCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZmvfCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZmviCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZtpoCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZtutCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.ZusiCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.nokia.nocdcommand.Zw7iCommandHandlerFactory;


@SuppressWarnings("unused")
public class NokiaCliParserReaderV10 extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(NokiaCliParserReaderV10.class);
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put("Z"+commandHandlerFactory.getCommand(), commandHandlerFactory);
		commands.put("ZZ"+commandHandlerFactory.getCommand(), commandHandlerFactory);
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	
	public NokiaCliParserReaderV10(Reader reader, DataListener listener, Context ctx) {
		super(reader);
		this.listener=listener;
		this.ctx=ctx;
		addCommand("*",  new UnhandledCommandHandlerFactory());
		addCommand(new ZedoCommandHandlerFactory());
		addCommand(new Ze2iCommandHandlerFactory());//hang if empty?
		addCommand(new ZtutCommandHandlerFactory());
		addCommand(new ZifoCommandHandlerFactory());
		addCommand(new ZmviCommandHandlerFactory());
		addCommand(new ZmvfCommandHandlerFactory());
		addCommand(new ZjgiCommandHandlerFactory());
		addCommand(new ZusiCommandHandlerFactory());
		addCommand(new ZtpoCommandHandlerFactory());
		addCommand(new Zw7iCommandHandlerFactory());
		addCommand(new Ze3iCommandHandlerFactory());
		//FWO, Z6I
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
							listener.onEndCommand(ctx, commandHandler.getCommand(), commandHandler.getParams(), getLine());
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
							commandHandler = commandHandlerFactory.create(this, command, params,listener, ctx);
						} else {
							System.err.println("Unhandled Command : "+ command +"  "+ params);
							logger.error("Unhandled Command : "+ command +"  "+ params);
							
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this, command, params, listener, ctx);
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
