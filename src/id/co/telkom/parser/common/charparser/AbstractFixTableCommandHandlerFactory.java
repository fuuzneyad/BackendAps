package id.co.telkom.parser.common.charparser;


public abstract class AbstractFixTableCommandHandlerFactory extends AbstractTableCommandHandlerFactory {

	private final String skipped;

	
	public AbstractFixTableCommandHandlerFactory(String command) {
		this(command, 1, null);
	}

	public AbstractFixTableCommandHandlerFactory(String command, int skipLines) {
		this(command, skipLines, null);
	}


	public AbstractFixTableCommandHandlerFactory(String command, int skipLines, String skipped) {
		super(command, skipLines);
		this.skipped = skipped;
	}

	public String getSkipped() {
		return skipped;
	}

}
