package id.co.telkom.parser.common.charparser;

import java.io.IOException;

import id.co.telkom.parser.common.model.Context;


public interface CommandHandler {
	public abstract boolean isDone();
	public String getCommand();
	public String getParams();
	public abstract void handle(Context ctx) throws IOException;
}
