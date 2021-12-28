package id.co.telkom.parser.common.charparser;



public abstract class AbstractTableCommandHandlerFactory implements CommandHandlerFactory {

	private final String command;
	private final int skipLines;

	
	public AbstractTableCommandHandlerFactory(String command) {
		this(command, 1);
	}

	public AbstractTableCommandHandlerFactory(String command, int skipLines) {
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
