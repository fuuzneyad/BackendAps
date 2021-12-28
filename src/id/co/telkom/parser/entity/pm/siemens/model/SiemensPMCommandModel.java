package id.co.telkom.parser.entity.pm.siemens.model;

public class SiemensPMCommandModel {
	private String type,table_name,comment, counter_id;
	private int idxCounter;
	public String getCounter_id() {
		return counter_id;
	}
	public void setCounter_id(String counter_id) {
		this.counter_id = counter_id;
	}
	public int getIdxCounter() {
		return idxCounter;
	}
	public void setIdxCounter(int idxCounter) {
		this.idxCounter = idxCounter;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String toString() {
		return "SiemensPMCommandModel [type=" + type + ", table_name="
				+ table_name + ", comment=" + comment + ", counter_id="
				+ counter_id + ", idxCounter=" + idxCounter + "]";
	}
}
