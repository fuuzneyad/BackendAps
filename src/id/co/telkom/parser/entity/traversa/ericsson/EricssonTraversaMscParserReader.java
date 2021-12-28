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
import id.co.telkom.parser.common.charparser.ParseException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.IoexpCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserANBSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserANESPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserANRSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7GCPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7GSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7LDPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7LPPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7LTPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7NCPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7RSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserC7SPPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserDBTSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserEXROPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserEXSCPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserIHALPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserIHCOPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserIHRDPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserIHSTPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserM3RSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGAAPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGADPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGBSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGCAPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGCEPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGCVPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGEPPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGIDPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGISPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGLAPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGMAPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGMGPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGNDPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGNIPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGNMPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGOCPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGORPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGPTPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGRIPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGRLPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGRNPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGRRPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGSSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserMGSVPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserNRGGPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserNRGWPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserPCORPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserPNBSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserSAAEPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.ParserSTRSPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.UnhandledCommandHandler;


public class EricssonTraversaMscParserReader extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(EricssonTraversaMscParserReader.class);
	private AbstractInitiator cynapseInit;
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	@SuppressWarnings("unused")
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public EricssonTraversaMscParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		super(reader);
		this.listener=listener;
		this.ctx=ctx;
		this.cynapseInit=cynapseInit;
		
		addCommand(new IoexpCommandHandlerFactory());
		addCommand(new ParserC7GCPCommandHandlerFactory());
		addCommand(new ParserEXROPCommandHandlerFactory());
		addCommand(new ParserNRGWPCommandHandlerFactory());
		addCommand(new ParserC7SPPCommandHandlerFactory());
		addCommand(new ParserMGCAPCommandHandlerFactory());
		addCommand(new ParserC7GSPCommandHandlerFactory());
		addCommand(new ParserANBSPCommandHandlerFactory());
		addCommand(new ParserDBTSPCommandHandlerFactory());
		addCommand(new ParserMGAAPCommandHandlerFactory());
		addCommand(new ParserMGCEPCommandHandlerFactory());
		addCommand(new ParserMGRRPCommandHandlerFactory());
		addCommand(new ParserMGISPCommandHandlerFactory());
		addCommand(new ParserMGBSPCommandHandlerFactory());
		addCommand(new ParserMGMAPCommandHandlerFactory());
		addCommand(new ParserMGRIPCommandHandlerFactory());
		addCommand(new ParserPCORPCommandHandlerFactory());
		addCommand(new ParserIHALPCommandHandlerFactory());
		addCommand(new ParserANRSPCommandHandlerFactory());
		addCommand(new ParserMGEPPCommandHandlerFactory());
		addCommand(new ParserMGIDPCommandHandlerFactory());
		addCommand(new ParserMGADPCommandHandlerFactory());
		addCommand(new ParserANESPCommandHandlerFactory());
		addCommand(new ParserSAAEPCommandHandlerFactory());
		addCommand(new ParserC7NCPCommandHandlerFactory());
		addCommand(new ParserIHCOPCommandHandlerFactory());
		addCommand(new ParserC7LTPCommandHandlerFactory());
		addCommand(new ParserC7RSPCommandHandlerFactory());
		addCommand(new ParserIHRDPCommandHandlerFactory());
		addCommand(new ParserIHSTPCommandHandlerFactory());
		addCommand(new ParserMGLAPCommandHandlerFactory());
		addCommand(new ParserMGMGPCommandHandlerFactory());
		addCommand(new ParserMGNDPCommandHandlerFactory());
		addCommand(new ParserMGNMPCommandHandlerFactory());
		addCommand(new ParserMGRLPCommandHandlerFactory());
		addCommand(new ParserMGRNPCommandHandlerFactory());
		addCommand(new ParserNRGGPCommandHandlerFactory());
		addCommand(new ParserPNBSPCommandHandlerFactory());
		addCommand(new ParserSTRSPCommandHandlerFactory());
		addCommand(new ParserEXSCPCommandHandlerFactory());
		addCommand(new ParserC7LDPCommandHandlerFactory());
		//...
		addCommand(new ParserC7LPPCommandHandlerFactory());
		addCommand(new ParserMGPTPCommandHandlerFactory());
		addCommand(new ParserMGORPCommandHandlerFactory());
		addCommand(new ParserMGNIPCommandHandlerFactory());
		addCommand(new ParserMGOCPCommandHandlerFactory());
		addCommand(new ParserMGSVPCommandHandlerFactory());
		addCommand(new ParserMGCVPCommandHandlerFactory());
		addCommand(new ParserM3RSPCommandHandlerFactory());
		addCommand(new ParserMGSSPCommandHandlerFactory());
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
							if(commandHandler==null)
								commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx, cynapseInit);
						} else {
							System.err.println("Unhandled Command : "+ command +" : "+ params);
							logger.error("Unhandled Command : "+ command +" : "+ params);
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, listener, ctx);
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
