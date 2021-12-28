package id.co.telkom.parser.common.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class Context {
	public String dateTimeid, mo_id, ne_id, sub_ne_id, hash_value, source, version, t_name, vendor;
	public int line;
	public String command,commandParam;
	public String fileName;
	public int granularity;
	public Timestamp date, timestampDateTime;
	public boolean isloadWithPrefix=true;
	
	public void setLoadWithPrefix(boolean loadWithPrefix) {
		this.isloadWithPrefix = loadWithPrefix;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public void setCommandParam(String commandParam) {
		this.commandParam = commandParam;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setDatetimeid(String datetimeid) {
		this.dateTimeid = datetimeid;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public void setTimestampDateTime(Timestamp date) {
		this.timestampDateTime = date;
	}
	public void setTableName(String t_name) {
		this.t_name = t_name;
	}
	public void setMo_id(String mo_id) {
		this.mo_id = mo_id;
	}
	public void setNe_id(String ne_id) {
		this.ne_id = ne_id;
	}
	public void setSub_ne_id(String sub_ne_id) {
		this.sub_ne_id = sub_ne_id;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setHashValue(){
		dateTimeid = dateTimeid == null ? "" : dateTimeid;
		ne_id = ne_id == null ? "" : ne_id;
		source = source == null ? "" : source; 
		sub_ne_id = sub_ne_id == null ? "" : sub_ne_id; 
		mo_id = mo_id == null ? "" : mo_id ;
		
		String word=dateTimeid+"|"+ne_id+"|"+source+"|"+sub_ne_id+"|"+mo_id+"|"+granularity;
//		String word=ne_id+"|"+source+"|"+sub_ne_id+"|"+mo_id;
		
		String hashword = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(word.getBytes());
			BigInteger hash = new BigInteger(1, md5.digest());
			hashword = hash.toString(16);
		} catch (NoSuchAlgorithmException e) {}
		
		this.hash_value=hashword;
	}
	
	public String getHashValue(){
		return this.hash_value;
	}
	@Override
	public String toString() {
		return "Context [t_name=" + t_name+",dateTimeid=" + dateTimeid + ", mo_id=" + mo_id
				+ ", ne_id=" + ne_id + ", sub_ne_id=" + sub_ne_id + ", source="
				+ source + ", version=" + version
				+ ", granularity=" + granularity + ", date=" + date + "]";
	}

	
}
