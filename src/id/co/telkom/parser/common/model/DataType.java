/**
 * 
 */
package id.co.telkom.parser.common.model;

public class DataType {
	public static final int STRUCT = 1;
	public static final int ARRAY = 2;
	public final int type;
	public final int count;
	public final String name;
	public int countDown;
	public DataType(int type, int count, String name) {
		super();
		this.type = type;
		this.count = countDown = count;
		this.name = name;
	}
}