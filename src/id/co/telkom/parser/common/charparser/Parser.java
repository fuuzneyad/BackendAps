package id.co.telkom.parser.common.charparser;

import java.io.IOException;
import java.io.Reader;

public abstract class Parser {
	private int c = -1;
	private int line = 1;
	private int col = 0;
	private int lastReadLine = 0;
	private final Reader reader;
	
	public Parser(Reader reader) {
		this.reader = reader;
	}
	public int getLastReadLine() {
		return lastReadLine;
	}
	public void resetLastReadLine() {
		lastReadLine = line;
	}
	public int getSkippedLines() {
		return lastReadLine == 0 ? 0 : line - lastReadLine;
	}
	public int getChar() {
		return c;
	}
	public int getLine() {
		return line;
	}
	public int getColumn() {
		return col;
	}
	public boolean isEqual(int c) {
		return this.c == c;
	}
	public boolean isEqual(char c) {
		return this.c == c;
	}
	public boolean isAlphabet() {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	public boolean isNumber() {
		return c >= '0' && c <= '9';
	}
	public boolean isAlphaNumeric() {
		return isAlphabet() || isNumber();
	}
	public boolean isEOL() {
		return c == '\r' || c == '\n' || c == -1;
	}
	public boolean isEOF() {
		return c == -1;
	}
	public boolean isWhiteSpace() {
		return c == ' ';
	}
	//append char into sb
	public Parser appendTo(StringBuilder sb) {
		sb.append((char) c);
		return this;
	}
	//append char into sb
	public Parser appendTo(StringBuffer sb) {
		sb.append((char) c);
		return this;
	}
	//read char from file
	public Parser read() throws IOException {
		lastReadLine = line;
		if(c == '\r' || c == '\n')
			line++;
		read0();
		return this;
	}
	//read and buffer by line, by ozan
	public Parser read(StringBuilder sb) throws IOException {
		lastReadLine = line;
		if(c == '\r' || c == '\n'){
			sb.setLength(0);
			line++;
		}else
			sb.append((char) c);
		read0();
		return this;
	}
	
	//read char from file
	private Parser read0() throws IOException {
		c = reader.read();
		col++;
		return this;
	}
	//read chars in one file in certain length
	public Parser read(StringBuilder sb, int length) throws IOException {
		sb.setLength(0);
		for (int i = 0; i < length; i++) {
			if (isEOL())
				break;
			lastReadLine = line;
			sb.append((char) c);
			read0();
		}
		return this;
	}
	public Parser skipEOLs(int maxSkippedEOL) throws IOException {
		for (int i = getSkippedLines(); i < maxSkippedEOL && (c == '\r' || c == '\n'); i++) {
			skipEOL();
		}
		return this;
	}
	public Parser skipEOLs() throws IOException {
		do {
			skipEOL();
		} while (c == '\r' || c == '\n');
		return this;
	}
	//skip line in file
	public Parser skipLines(int count) throws IOException {
		skipEOLs();
		for (int i = 0; i < count; i++) {
			skipUntilEOL().skipEOLs();
		}
		return this;
	}
	//skip chars until condition char is found in the file
	public Parser skipUntil(int condition) throws IOException {
		while (c != condition && c != -1) {
			read0();
		}
		if (c == condition)
			read0();
		return this;
	}
	//reads all chars in one line until condition char is found
	public Parser readUntil(int condition, StringBuilder sb) throws IOException {
		sb.setLength(0);
		while (c != condition && c != '\r' && c != '\n' && c != -1) {
			sb.append((char) c);
			lastReadLine = line;
			read0();
		}
		if (c == condition)
			read0();
		return this;
	}
	public Parser readUntilIgnoreLine(int condition, StringBuilder sb) throws IOException {
		while (c != condition && c != -1) {
			if(c == '\r' || c == '\n'){
				read0();
				sb.append('\n');
				line++;
			}
			sb.append((char) c);
			read0();
		}
		if (c == condition)
			read0();
		return this;
	}
	public Parser readUntilWithoutLastChar(int condition, StringBuilder sb) throws IOException {
		sb.setLength(0);
		while (c != condition && c != '\r' && c != '\n' && c != -1) {
			sb.append((char) c);
			lastReadLine = line;
			read0();
		}
		return this;
	}
	
	public Parser readUntilSkipEoL(int condition, StringBuilder sb) throws IOException {
		sb.setLength(0);
		while (c != condition  && c != -1) {
			if(isEOL())
				break;
//				skipEOL();
			sb.append((char) c);
			lastReadLine = line;
			read0();
		}
		if (c == condition)
			read0();
		return this;
	}
	//skip all chars in one line until end of line
	public Parser skipUntilEOL() throws IOException {
		while (c != '\r' && c != '\n' && c != -1) {
			read0();
		}
		return this;
	}
	
	//read all char in one line in to sb param
	public Parser readUntilEOL(StringBuilder sb) throws IOException {
		sb.setLength(0);
		while (c != '\r' && c != '\n' && c != -1) {
			lastReadLine = line;
			sb.append((char) c);
			read0();
		}
		return this;
	}
	
	
	//skip while condition but only in one line
	public Parser skipWhile(int condition) throws IOException {
		if (condition == '\r' || condition == '\n')
			throw new IllegalArgumentException();
		while (c == condition) {
			read0();
		}
		return this;
	}
	
	public Parser skipUntilAlphabet() throws IOException {
		while (c != '\r' && c != '\n' && c != -1 && !isAlphabet()) {
			read0();
		}
		return this;
	}
	
	public Parser readWhileAlphabet(StringBuilder sb) throws IOException {
		sb.setLength(0);
		while(isAlphabet()){
			sb.append((char) c);
			read0();
		}
		return this;
	}
	public Parser readWhileAlphaNumeric(StringBuilder sb) throws IOException {
		sb.setLength(0);
		while(isAlphaNumeric()){
			sb.append((char) c);
			read0();
		}
		return this;
	}
	
	public Parser readWhileAlphaNumericAnd(StringBuilder sb, char ch) throws IOException {
		sb.setLength(0);
		while(isAlphaNumeric() || c == ch){
			sb.append((char) c);
			read0();
		}
		return this;
	}
	//skip new line character
	public Parser skipEOL() throws IOException {
		if (c == '\r') {
			read0();
			if (c == '\n') {
				read0();
			}
			col = 0;
			line++;
		} else if (c == '\n') {
			read0();
			col = 0;
			line++;
		}
		return this;
	}
	//remove space from right
	public static void trimRight(StringBuilder sb) {
		for (int i = sb.length() - 1; i >= 0 && sb.charAt(i) == ' '; i--) {
			sb.setLength(i);
		}
	}
	
	public static void trimRight(StringBuilder sb, char c) {
		for (int i = sb.length() - 1; i >= 0 && (sb.charAt(i) == ' ' || sb.charAt(i) == c); i--) {
			sb.setLength(i);
		}
	}
	
	public Parser skipSomeChar( int length) throws IOException {
		for (int i = 0; i < length; i++) {
			if (isEOL())
				break;
			lastReadLine = line;
			read0();
		}
		return this;
	}
	
}
