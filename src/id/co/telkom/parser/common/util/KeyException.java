package id.co.telkom.parser.common.util;

import java.io.IOException;

public class KeyException extends IOException{
	private static final long serialVersionUID = -7133175568964996024L;

	public KeyException(){
		super();
	}
	
	public KeyException(String message, Throwable cause){
		super(message, cause);
	}
	
	public KeyException(String message){
		super(message);
	}
	
	public KeyException(Throwable cause){
		super(cause);
	}
}
