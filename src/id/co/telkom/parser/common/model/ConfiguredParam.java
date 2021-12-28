/**
 * 
 */
package id.co.telkom.parser.common.model;


public class ConfiguredParam{

	private final int dbLength;
	private final String name;

	public ConfiguredParam(String name, int dbLength) {
		this.name = name;
		this.dbLength = dbLength;
	}

	public int getDbLength() {
		return dbLength;
	}

	public String getName() {
		return name;
	}
}