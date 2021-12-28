package id.co.telkom.parser.entity.traversa.siemens.initiator;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.ParseException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.siemens.command.InitiatorDispsigpointCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.siemens.command.UnhandledCommandHandler;


public class SiemensSTPParserInitiatorReader extends CommandParser  {
	private final Context ctx;
	private Map<String, InitiatorCommandHandlerFactory> commands = new HashMap<String, InitiatorCommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(SiemensSTPParserInitiatorReader.class);
	private GlobalBuffer buf;
	private String NE;
	boolean isDISPSPENHS;
	
	private void addCommand(InitiatorCommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	@SuppressWarnings("unused")
	private void addCommand(String key, InitiatorCommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public SiemensSTPParserInitiatorReader(Reader reader, Context ctx, GlobalBuffer buf) {
		super(reader);
		this.ctx=ctx;
		this.buf=buf;
		
		addCommand(new InitiatorDispsigpointCommandHandlerFactory());
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
					if (isEqual('D')) {//if (isEqual('<'))
						readUntilEOL(sb);
						trimRight(sb);
											
						String s = sb.toString();
						if (s.charAt(s.length() - 1) != ';') {

								if (s.contains("ISPSPENHS"))
								isDISPSPENHS=true;
									else
								throw new ParseException("Found unexpected characters at line " + getLine() + " : " + sb);
							
						} else 
						{
							s = s.substring(1, s.length() - 1);
							isDISPSPENHS=false;
						}
						
						
						String command;
						String params;
						
						if (isDISPSPENHS)
						{
						command="DISPSPENHS";
						params=null;
						}
						else{
						int i = s.indexOf(':');
						String theCommand="D"+s;
						command = i == -1 ? theCommand : theCommand.substring(0, i+1);
						command = command.toUpperCase();
						params = i == -1 ? null : theCommand.substring(theCommand.indexOf(':')+1);
						}

	/*handle block*/					
						InitiatorCommandHandlerFactory commandHandlerFactory = commands.get(command);

						if (commandHandlerFactory != null) {
							commandHandler = commandHandlerFactory.create(this,command, params, ctx, buf);
						} else {
//							System.out.println("Unhandled Command : {"+command+"} : {"+params+"}");
							logger.error("Unhandled Command : {"+command+"} : {"+params+"}");
							commandHandlerFactory = commands.get("*");
							if (commandHandlerFactory != null) {
								commandHandler = commandHandlerFactory.create(this,command, params, ctx, buf);
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
						//System.out.println(getLine() + " " + sb);
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
//						System.out.println("commandHandler != null");
					} else if (ex instanceof UnhandledCommandException) {
						UnhandledCommandException ex1 = (UnhandledCommandException) ex;
						commandHandler = new UnhandledCommandHandler(this,  ex1.getCommand(), ex1.getParams());
//						throw new IOException(ex);
					} else {
						commandHandler = null;
					}
				}

			}
		}
	}
	
}
