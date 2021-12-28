package id.co.telkom.parser.entity.traversa.huawei.initiator;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.UnhandledCommandHandler;
import id.co.telkom.parser.entity.traversa.huawei.UnhandledCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.command.InitiatorLocalinfoCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.command.InitiatorOpcGroupCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class HuaweiHLRParserInitiatorReader extends CommandParser{
	private static final Logger logger = Logger.getLogger(HuaweiHLRParserInitiatorReader.class);
	private Map<String, InitiatorCommandHandlerFactory> commands = new HashMap<String, InitiatorCommandHandlerFactory>();
	private GlobalBuffer buf;
	private final Context ctx;
	
	public HuaweiHLRParserInitiatorReader(Reader reader, Context ctx, GlobalBuffer buf) {
		super(reader);
		this.ctx=ctx;
		this.buf=buf;
		
		addCommand("*", new UnhandledCommandHandlerFactory());
		addCommand(new InitiatorOpcGroupCommandHandlerFactory());//own_sp
		addCommand(new InitiatorLocalinfoCommandHandlerFactory());//own_gt
	}
	
	private void addCommand(InitiatorCommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, InitiatorCommandHandlerFactory commandHandlerFactory){
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
						InitiatorCommandHandlerFactory commandHandlerFactory = commands.get(command);
												
						if (commandHandlerFactory != null) {
							commandHandler = commandHandlerFactory.create(this, command, params, ctx, buf);
						} else {
							logger.error("Unhandled Command : {"+command+"} : {"+params+"}");
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this, command, params, ctx, buf);
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
							buf.setVendorToVertex(ctx.ne_id, ctx.vendor);
							buf.setNEToVertex(ctx.ne_id);
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
