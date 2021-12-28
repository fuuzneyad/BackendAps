package id.co.telkom.parser.common.charparser;


public class UnhandledCommandException extends ParseException {
	private static final long serialVersionUID = -2336938725696506483L;
	private String command;
	private String params;

	public UnhandledCommandException(String command, String params) {
		super(createMessage(command, params));
		this.command = command;
		this.params = params;
	}

	private static String createMessage(String command, String params) {
		if (params != null)
			return "Unhandled command: " + command + " " + params;
		else
			return "Unhandled command: " + command;
	}

	public String getCommand() {
		return command;
	}

	public String getParams() {
		return params;
	}

}
