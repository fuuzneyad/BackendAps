package id.co.telkom.parser.common.charparser;

public abstract class AbstractCommandHandler implements CommandHandler {
	private final String command;
	private final String params;
	private boolean done;
	
	public AbstractCommandHandler(String command, String params) {
		super();
		this.command = command;
		this.params = params;
	}
	
	public String toString() {
		if (params != null)
			return command + " " + params;
		return command;
	}
	public boolean isDone() {
		return done;
	}
	protected void done() {
		this.done = true;
	}
	@Override
	public String getCommand() {
		return command;
	}
	@Override
	public String getParams() {
		return params;
	}
}
