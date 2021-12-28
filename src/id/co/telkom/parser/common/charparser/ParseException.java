package id.co.telkom.parser.common.charparser;


public class ParseException extends ParserException {
	private static final long serialVersionUID = -5726461381250559809L;

	public ParseException() {
		super();
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

}
