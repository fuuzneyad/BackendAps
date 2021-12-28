package id.co.telkom.parser.entity.traversa.siemens;

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
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPLTUCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPMECommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSIGDPCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSIGLINKCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSIGLSETCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSIGPOINTCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPCLGPACommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPENHSCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPENSCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPGTCRULCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPGTRULCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPGTTRLCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPLNKREMCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.ParserDISPSPRNSCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.UnhandledCommandHandler;


public class SiemensTraversaSTPParserReader extends CommandParser  {
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(SiemensTraversaSTPParserReader.class);
	private String NE;
	boolean isDISPSPENHS;
	private AbstractInitiator cynapseInit;
	private DataListener listener;
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	@SuppressWarnings("unused")
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public SiemensTraversaSTPParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		super(reader);
		this.ctx=ctx;
		this.cynapseInit=cynapseInit;
		this.listener=listener;
		
		addCommand(new ParserDISPSIGPOINTCommandHandlerFactory());
		addCommand(new ParserDISPSPGTRULCommandHandlerFactory());
		addCommand(new ParserDISPLTUCommandHandlerFactory());
		addCommand(new ParserDISPMECommandHandlerFactory());
		addCommand(new ParserDISPSIGDPCommandHandlerFactory());
		addCommand(new ParserDISPSIGLINKCommandHandlerFactory());
		addCommand(new ParserDISPSIGLSETCommandHandlerFactory());
		addCommand(new ParserDISPSPCLGPACommandHandlerFactory());
		addCommand(new ParserDISPSPENHSCommandHandlerFactory());
		addCommand(new ParserDISPSPENSCommandHandlerFactory());
		addCommand(new ParserDISPSPGTCRULCommandHandlerFactory());
		addCommand(new ParserDISPSPGTTRLCommandHandlerFactory());
		addCommand(new ParserDISPSPLNKREMCommandHandlerFactory());
		addCommand(new ParserDISPSPRNSCommandHandlerFactory());
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
					if (isEqual('D')) {
						readUntilEOL(sb);
						trimRight(sb);
											
						String s = sb.toString();
						if (s.charAt(s.length() - 1) != ';') {

							if (s.contains("ISPSPENHS"))
								isDISPSPENHS=true;
							else
								throw new ParseException("Found unexpected characters at line " + getLine() + " : " + sb);
							
						} else {
							s = s.substring(1, s.length() - 1);
							isDISPSPENHS=false;
						}
						
						
						String command;
						String params;
						
						if (isDISPSPENHS)
						{
							command="DISPSPENHS";
							params=null;
						} else{
							int i = s.indexOf(':');
							String theCommand="D"+s;
							command = i == -1 ? theCommand : theCommand.substring(0, i+1);
							command = command.toUpperCase();
							params = i == -1 ? null : theCommand.substring(theCommand.indexOf(':')+1);
						}

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
						readUntilEOL(sb);
						NE = sb.toString().trim();
						if(NE.contains("/") && (NE.contains("AM") || NE.contains("PM")))
							ctx.setNe_id(NE.substring(0, NE.indexOf("/")));else		
						throw new ParseException("Found unexpected characters at line " + getLine() + " : " + sb);
					}
				}
				skipEOLs();
			} catch (ParserException ex) {
				readUntilEOL(sb).skipEOLs();//				System.out.println(sb);

				if (ex instanceof CommandErrorException || ex instanceof CompleteException) {
					commandHandler = null;
				} else {
					if (commandHandler != null) {
						commandHandler = new UnhandledCommandHandler(this, commandHandler.getCommand(), commandHandler.getParams());
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
