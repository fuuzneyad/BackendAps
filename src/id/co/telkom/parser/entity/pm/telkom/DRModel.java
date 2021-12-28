package id.co.telkom.parser.entity.pm.telkom;

public class DRModel {
	private String message_id;
	private String 		supplier_message_id;
	private String 		received_time;
	private String 		processed_time;
	private String 		status;
	private String 		error_code;
	private String 		message_found;
	private String 		is_smsc_dlr;
	private String 		filename;
	boolean isFoundOnCDR = false;
	
	
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isFoundOnCDR() {
		return isFoundOnCDR;
	}

	public void setFoundOnCDR(boolean isFoundOnCDR) {
		this.isFoundOnCDR = isFoundOnCDR;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getSupplier_message_id() {
		return supplier_message_id;
	}

	public void setSupplier_message_id(String supplier_message_id) {
		this.supplier_message_id = supplier_message_id;
	}

	public String getReceived_time() {
		return received_time;
	}

	public void setReceived_time(String received_time) {
		this.received_time = received_time;
	}

	public String getProcessed_time() {
		return processed_time;
	}

	public void setProcessed_time(String processed_time) {
		this.processed_time = processed_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

//	public String getDlr_message() {
//		return dlr_message;
//	}
//
//	public void setDlr_message(String dlr_message) {
//		this.dlr_message = dlr_message;
//	}

	public String getMessage_found() {
		return message_found;
	}

	public void setMessage_found(String message_found) {
		this.message_found = message_found;
	}

	public String getIs_smsc_dlr() {
		return is_smsc_dlr;
	}

	public void setIs_smsc_dlr(String is_smsc_dlr) {
		this.is_smsc_dlr = is_smsc_dlr;
	}

	@Override
	public String toString() {
		return "DRModel [message_id=" + message_id + ", supplier_message_id=" + supplier_message_id + ", received_time="
				+ received_time + ", processed_time=" + processed_time + ", status=" + status + ", error_code="
				+ error_code + ", message_found=" + message_found + ", is_smsc_dlr=" + is_smsc_dlr + "]";
	}
	
	
	
}
