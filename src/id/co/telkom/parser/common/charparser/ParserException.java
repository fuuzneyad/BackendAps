package id.co.telkom.parser.common.charparser;

import java.io.IOException;

public class ParserException extends IOException {
	private static final long serialVersionUID = -4782409749458222768L;

	public ParserException() {
		super();
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserException(String message) {
		super(message);
	}

	public ParserException(Throwable cause) {
		super(cause);
	}

}
