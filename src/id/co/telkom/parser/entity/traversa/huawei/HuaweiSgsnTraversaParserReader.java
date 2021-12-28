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
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserDSPS1APLNKCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserDSPUSRPDPNUMCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTGBPAGINGCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTIUPAGINGCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTLOCALNRICommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTMMEIDCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTNSECommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTRNCCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTS1PAGINGCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTTAILAICommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTTALSTCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserLSTVRFBRDIPBINDCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.huawei.sgsncommand.ParserTSTDNSCommandHandlerFactory;


public class HuaweiSgsnTraversaParserReader extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private static final Logger logger = Logger.getLogger(HuaweiSgsnTraversaParserReader.class);
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private AbstractInitiator cynapseInit;
	
	public HuaweiSgsnTraversaParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		super(reader);
		this.ctx=ctx;
		this.listener=listener;
		this.cynapseInit=cynapseInit;
		
		addCommand("*", new UnhandledHLRCommandHandlerFactory());
		addCommand(new ParserLSTNSECommandHandlerFactory());
		addCommand(new ParserLSTGBPAGINGCommandHandlerFactory());
		addCommand(new ParserTSTDNSCommandHandlerFactory());
		addCommand(new ParserLSTIUPAGINGCommandHandlerFactory());
		addCommand(new ParserDSPUSRPDPNUMCommandHandlerFactory());
		addCommand(new ParserLSTS1PAGINGCommandHandlerFactory());
		addCommand(new ParserDSPS1APLNKCommandHandlerFactory());
		addCommand(new ParserLSTTAILAICommandHandlerFactory());
		
		addCommand(new ParserLSTLOCALNRICommandHandlerFactory());
		addCommand(new ParserLSTVRFBRDIPBINDCommandHandlerFactory());
		addCommand(new ParserLSTTALSTCommandHandlerFactory());
		addCommand(new ParserLSTRNCCommandHandlerFactory());
		addCommand(new ParserLSTMMEIDCommandHandlerFactory());
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
					if (isEqual('%')) {
						skipUntilAlphabet();
						readUntilEOL(sb);
						
						String c = sb.toString()
						.replace("TST ", "TST_")
						.replace("DSP ", "DSP_")
						.replace("LST ", "LST_")
						.replace("%", "")
						;
						String command = c.split(":")[0];
						String params= c.contains(":")? c.split(":")[1]:c;							
						
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
					} else if(isEqual('+')){
						readUntilEOL(sb);
						if(sb.toString().contains("MENAME")){
							String ne = sb.toString().split("MENAME:")[1];
							ne = ne.split("\\*")[0].trim();
							ctx.setNe_id(ne);
						}
						String dt = sb.toString().split("\\*/ ")[1].trim();
						dt = dt.split(" ")[0].trim()+" 00:00:00";
						ctx.setDatetimeid(dt);
					}else {
						readUntilEOL(sb);//tambahan
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
