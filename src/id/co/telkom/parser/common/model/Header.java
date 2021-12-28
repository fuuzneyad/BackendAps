/**
 * 
 */
package id.co.telkom.parser.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Header {
	private final String name;
	public int length;
	public Header(String name) {
		this(name.trim(), name.length());
	}
	public Header(String name, int length) {
		super();
		this.name = name.trim();
		this.length = length;
	}
	@Override
	public String toString() {
		return "Header [name=" + name + ", length=" + length + "]";
	}
	public static Header[] computeHeaders(String s) {
		List<Header> list = new ArrayList<Header>();
		final int sLen = s.length();
		int offset = 0;
		for (;;) {
			int i = s.indexOf(' ', offset);
			if (i != -1) {
				String name = s.substring(offset, i);
				for (; i < sLen && s.charAt(i) == ' '; i++);
				list.add(new Header(name, i - offset));
				offset = i;
				if (i >= sLen)
					break;
			} else {
				list.add(new Header(s.substring(offset), sLen - offset));
				break;
			}
		}
		return list.toArray(new Header[list.size()]);
	}
	public static void fillMap(String s, Header[] headers, Map<String, String> table) {
		int offset = 0;
		int sLen = s.length();
		int lastIndex = headers.length - 1;
		for (int i = 0; i <= lastIndex; i++) {
			Header header = headers[i];
			if (sLen > offset) {
				if (i == lastIndex) {
					table.put(header.name, s.substring(offset).trim());
				} else {
					table.put(header.name, s.substring(offset, Math.min(offset + header.length, sLen)).trim());
				}
//			} else {
//				table.put(header.name, null);
			}
			offset += header.length;
		}
	}
	
	public String getName(){
		return name;
	}
	
	public int getLength(){
		return length;
	}

	public void setLength(int length){
		this.length =  length;
	}
}