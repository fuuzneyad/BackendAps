/**
 * 
 */
package id.co.telkom.parser.common.model;

import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.charparser.Predicate;



public class ConfiguredHeader extends Header {

	public final int dbLength;
	public final boolean optional;
	public final boolean rightPad;
	public final Predicate multilinePredicate;
	public final boolean copied;
	public final boolean isSplitted;

	public ConfiguredHeader(String name) {
		super(name);
		this.rightPad = false;
		this.optional = false;
		this.copied = false;
		this.dbLength= this.length;
		this.multilinePredicate = null;
		this.isSplitted=false;
	}

	public ConfiguredHeader(String name, Boolean copied, int dbLength) {
		super(name);
		this.rightPad = false;
		this.optional = false;
		this.copied = copied;		
		this.dbLength= dbLength;
		this.multilinePredicate = null;
		this.isSplitted=false;
	}
	
	public ConfiguredHeader(String name, boolean rightPad, boolean optional, Predicate multiLinePredicate) {
		super(name);
		this.rightPad = rightPad;
		this.optional = optional;
		this.multilinePredicate = multiLinePredicate;
		this.copied = false;
		this.dbLength= this.length;
		this.isSplitted=false;
	}
	
	public ConfiguredHeader(String name, boolean rightPad, boolean optional, Predicate multiLinePredicate,int dbLength) {
		super(name);
		this.rightPad = rightPad;
		this.optional = optional;
		this.multilinePredicate = multiLinePredicate;
		this.copied = false;
		this.dbLength= dbLength;
		this.isSplitted=false;
	}
	
	public ConfiguredHeader(String name, Boolean copied, int length, int dbLength) {
		super(name,length);
		this.rightPad = false;
		this.optional = false;
		this.copied = copied;		
		this.dbLength= dbLength;
		this.multilinePredicate = null;
		this.isSplitted=false;
	}

	public ConfiguredHeader(String name, int length) {
		super(name, length);
		this.rightPad = false;
		this.optional = false;
		this.copied = false;
		this.dbLength= this.length;
		this.multilinePredicate = null;
		this.isSplitted=false;
	}

	public ConfiguredHeader(String name, int length, boolean isSplitted) {
		super(name, length);
		this.rightPad = false;
		this.optional = false;
		this.copied = false;
		this.dbLength= this.length;
		this.multilinePredicate = null;
		this.isSplitted=isSplitted;
	}
	public ConfiguredHeader(String name, int dbLength, int length) {
		super(name, length);
		this.rightPad = false;
		this.optional = false;
		this.copied = false;		
		this.dbLength= dbLength;
		this.multilinePredicate = null;
		this.isSplitted=false;
	}
	
	public ConfiguredHeader(String name, int dbLength, int length, boolean copied) {
		super(name, length);
		this.rightPad = false;
		this.optional = false;
		this.copied = copied;		
		this.dbLength= dbLength;
		this.multilinePredicate = null;
		this.isSplitted=false;
	}
	
	public ConfiguredHeader(String name, Boolean copied) {
		super(name);
		this.rightPad = false;
		this.optional = false;
		this.copied = copied;		
		this.dbLength= this.length;
		this.multilinePredicate = null;
		this.isSplitted=false;
	}
	
	public ConfiguredHeader(String name, Boolean copied, boolean isSplitted) {
		super(name);
		this.rightPad = false;
		this.optional = false;
		this.copied = copied;		
		this.dbLength= this.length;
		this.multilinePredicate = null;
		this.isSplitted=isSplitted;
	}
	
	public int getDbLength() {
		return dbLength;
	}
	public boolean isMultiLine() {
		return this.multilinePredicate != null;
	}
	public boolean isNewEntry(Parser reader) {
		return multilinePredicate == null || !multilinePredicate.apply(reader);
	}
}