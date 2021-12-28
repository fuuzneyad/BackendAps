package id.co.telkom.parser.entity.traversa.huawei;

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
import id.co.telkom.parser.entity.traversa.huawei.command.ParserLocalinfoCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.command.ParserSccpDSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.command.ParserSccpGTCommandHandlerFactory;


public class HuaweiHlrTraversaParserReader extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private static final Logger logger = Logger.getLogger(HuaweiHlrTraversaParserReader.class);
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private AbstractInitiator cynapseInit;
	
	public HuaweiHlrTraversaParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		super(reader);
		this.ctx=ctx;
		this.listener=listener;
		this.cynapseInit=cynapseInit;
		
		addCommand("*", new UnhandledHLRCommandHandlerFactory());
		addCommand(new ParserLocalinfoCommandHandlerFactory());
		addCommand(new ParserSccpGTCommandHandlerFactory());
		addCommand(new ParserSccpDSPCommandHandlerFactory());
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
					if (isEqual('S')||isEqual('L')||isEqual('O')) {
						readUntilEOL(sb);
						String command;
						String params;							
						
						String s = sb.toString().trim();
						if (s.startsWith("Local Office Information"))
						{
							command="LOCALINFO";
							params=null;
						}else
						if (s.equals("OPC Grouping"))
						{
							command="OPCGROUP";
							params=null;
						}else
						if (s.equals("SCCP GT table"))
						{
							command="SCCPGTTABLE";
							params=null;
						}else
						if (s.equals("SCCP DSP table"))
						{
							command="SCCPDSPTABLE";
							params=null;
						}else							
						if (s.equals("SCCP multi load share table"))
						{
							command="SCCPLOADSHARE";
							params=null;
						}else						
							throw new ParserException("Found unexpected characters at line " + getLine() + " : " + sb);
						
						
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
						if (getLine() == 1) {
							skipUntilEOL().skipEOLs();
							continue;
						}
						readUntilEOL(sb);//tambahan
						if(sb.toString().startsWith("NE Name:")){
							skipEOLs().readUntilEOL(sb);
							ctx.setNe_id(sb.toString().replace("-", "").trim());
						}
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
						commandHandler = new UnhandledCommandHandler(this, ex1.getCommand(), ex1.getParams());
					} else {
						commandHandler = null;
					}
				}
			}
		}
	}
	
}
