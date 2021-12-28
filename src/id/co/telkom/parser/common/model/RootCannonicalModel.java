package id.co.telkom.parser.common.model;

import java.sql.Timestamp;
import java.util.Map;

public class RootCannonicalModel extends CommonModel {
	private static final long serialVersionUID = -6016754890725004643L;
	public RootCannonicalModel(String name, Map<String, Object> values) {
		super(name, values);
	}
	public RootCannonicalModel() {
	}
	public void setLine(int line) {
		put("LINE", Integer.valueOf(line));
	}
	
	public void setNE(String ne) {
		put("NE", ne);
	}
	
	public void setDate(Timestamp date) {
		put("ENTRY_DATE", date);
	}

	public int getLine() {
		Integer i = (Integer) get("LINE");
		if (i != null)
			return i.intValue();
		else
			return -1;
	}

	public void setBatchId(Number batchId) {
		put("BATCH_ID", batchId);
	}

	public Number getBatchId() {
		Number i = (Number) get("BATCH_ID");
		if (i != null)
			return i.intValue();
		else
			return -1;
	}
}
