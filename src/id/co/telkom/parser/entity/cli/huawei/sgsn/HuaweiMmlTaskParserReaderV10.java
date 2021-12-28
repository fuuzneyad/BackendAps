package id.co.telkom.parser.entity.cli.huawei.sgsn;

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
import id.co.telkom.parser.entity.cli.huawei.sgsn.nocdcommand.DspCommandHandlerFactory;
import id.co.telkom.parser.entity.cli.huawei.sgsn.nocdcommand.UnhandledCommandHandler;


@SuppressWarnings("unused")
public class HuaweiMmlTaskParserReaderV10 extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(HuaweiMmlTaskParserReaderV10.class);
	private String command=null;
	private String params=null;
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	
	public HuaweiMmlTaskParserReaderV10(Reader reader, DataListener listener, Context ctx) {
		super(reader);
			this.listener=listener;
			this.ctx=ctx;
		addCommand(new DspCommandHandlerFactory());
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
	
	public void parse() throws IOException {
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
					if (isEqual('[')) {
						readUntilEOL(sb);
						
						if(sb.toString().trim().equals("[Mml Command]")){
							skipEOLs().readUntilEOL(sb);
							command=sb.toString().replace("\t","");
							if(command.contains(" ")){
								params=command.split(" ")[1];
								command=command.split(" ")[0];
								ctx.setCommandParam(params);
							}
							
						}else
						if(sb.toString().trim().equals("[Ne Name]")){
							skipEOLs().readUntilEOL(sb);
							String ne=(sb.toString().replace("\t", ""));
							ctx.setNe_id(ne);
							ctx.setMo_id(ne);
						}else
						if(sb.toString().trim().equals("[Mml Command Report]")){
							/*handle block*/
							logger.info("command "+command+" param "+params+" ne:"+ctx.ne_id);
							CommandHandlerFactory commandHandlerFactory = commands.get(command);
													
							if (commandHandlerFactory != null) {
								listener.onBeginCommand(ctx, command, params, getLine());
								commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx);
							} else {
								System.out.println("Unhandled Command : "+ command +":"+ params);
								logger.error("Unhandled Command : "+ command +":"+ params);
								commandHandlerFactory = commands.get("*");
								if (commandHandlerFactory != null) {
									commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx);
								} else {
									throw new UnhandledCommandException(command, params);
								}
							}
							/*handle block*/
						}
						

					} else {
						readUntilEOL(sb);//tambahan
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
