package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.CommandHandlerFactory;

public abstract class AbstractCommandHandlerFactory implements CommandHandlerFactory {
	private final String command;
	private final int skipLines;

	public AbstractCommandHandlerFactory(String command) {
		this(command, 1);
	}

	public AbstractCommandHandlerFactory(String command, int skipLines) {
		this.command = command;
		this.skipLines = skipLines;
	}

	public String getCommand() {
		return command;
	}

	public int getSkipLines() {
		return skipLines;
	}
	
}
