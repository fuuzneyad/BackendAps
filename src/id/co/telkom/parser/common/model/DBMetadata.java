package id.co.telkom.parser.common.model;

public class DBMetadata {

	String 
		DATABASE_PRODUCT,
		TABLE_CAT, 
		TABLE_SCHEM, 
		TABLE_NAME, 
		COLUMN_NAME, 
		DATA_TYPE, 
		TYPE_NAME, 
		COLUMN_SIZE, 
		BUFFER_LENGTH, 
		DECIMAL_DIGITS, 
		NUM_PREC_RADIX, 
		NULLABLE, 
		REMARKS, 
		COLUMN_DEF, 
		SQL_DATA_TYPE, 
		SQL_DATETIME_SUB, 
		CHAR_OCTET_LENGTH, 
		ORDINAL_POSITION, 
		IS_NULLABLE;

	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}

	public void setCOLUMN_NAME(String cOLUMN_NAME) {
		COLUMN_NAME = cOLUMN_NAME;
	}

	public String getDATABASE_PRODUCT() {
		return DATABASE_PRODUCT;
	}

	public void setDATABASE_PRODUCT(String dATABASE_PRODUCT) {
		DATABASE_PRODUCT = dATABASE_PRODUCT;
	}

	public String getTABLE_CAT() {
		return TABLE_CAT;
	}

	public void setTABLE_CAT(String tABLE_CAT) {
		TABLE_CAT = tABLE_CAT;
	}

	public String getTABLE_SCHEM() {
		return TABLE_SCHEM;
	}

	public void setTABLE_SCHEM(String tABLE_SCHEM) {
		TABLE_SCHEM = tABLE_SCHEM;
	}

	public String getTABLE_NAME() {
		return TABLE_NAME;
	}

	public void setTABLE_NAME(String tABLE_NAME) {
		TABLE_NAME = tABLE_NAME;
	}


	public String getDATA_TYPE() {
		return DATA_TYPE;
	}

	public void setDATA_TYPE(String dATA_TYPE) {
		DATA_TYPE = dATA_TYPE;
	}

	public String getTYPE_NAME() {
		return TYPE_NAME;
	}

	public void setTYPE_NAME(String tYPE_NAME) {
		TYPE_NAME = tYPE_NAME;
	}

	public String getCOLUMN_SIZE() {
		return COLUMN_SIZE;
	}

	public void setCOLUMN_SIZE(String cOLUMN_SIZE) {
		COLUMN_SIZE = cOLUMN_SIZE;
	}

	public String getBUFFER_LENGTH() {
		return BUFFER_LENGTH;
	}

	public void setBUFFER_LENGTH(String bUFFER_LENGTH) {
		BUFFER_LENGTH = bUFFER_LENGTH;
	}

	public String getDECIMAL_DIGITS() {
		return DECIMAL_DIGITS;
	}

	public void setDECIMAL_DIGITS(String dECIMAL_DIGITS) {
		DECIMAL_DIGITS = dECIMAL_DIGITS;
	}

	public String getNUM_PREC_RADIX() {
		return NUM_PREC_RADIX;
	}

	public void setNUM_PREC_RADIX(String nUM_PREC_RADIX) {
		NUM_PREC_RADIX = nUM_PREC_RADIX;
	}

	public String getNULLABLE() {
		return NULLABLE;
	}

	public void setNULLABLE(String nULLABLE) {
		NULLABLE = nULLABLE;
	}

	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

	public String getCOLUMN_DEF() {
		return COLUMN_DEF;
	}

	public void setCOLUMN_DEF(String cOLUMN_DEF) {
		COLUMN_DEF = cOLUMN_DEF;
	}

	public String getSQL_DATA_TYPE() {
		return SQL_DATA_TYPE;
	}

	public void setSQL_DATA_TYPE(String sQL_DATA_TYPE) {
		SQL_DATA_TYPE = sQL_DATA_TYPE;
	}

	public String getSQL_DATETIME_SUB() {
		return SQL_DATETIME_SUB;
	}

	public void setSQL_DATETIME_SUB(String sQL_DATETIME_SUB) {
		SQL_DATETIME_SUB = sQL_DATETIME_SUB;
	}

	public String getCHAR_OCTET_LENGTH() {
		return CHAR_OCTET_LENGTH;
	}

	public void setCHAR_OCTET_LENGTH(String cHAR_OCTET_LENGTH) {
		CHAR_OCTET_LENGTH = cHAR_OCTET_LENGTH;
	}

	public String getORDINAL_POSITION() {
		return ORDINAL_POSITION;
	}

	public void setORDINAL_POSITION(String oRDINAL_POSITION) {
		ORDINAL_POSITION = oRDINAL_POSITION;
	}

	public String getIS_NULLABLE() {
		return IS_NULLABLE;
	}

	public void setIS_NULLABLE(String iS_NULLABLE) {
		IS_NULLABLE = iS_NULLABLE;
	}

//	@Override
//	public String toString() {
//		return "DBMetadata [DATABASE_PRODUCT=" + DATABASE_PRODUCT
//				+ ", TABLE_CAT=" + TABLE_CAT + ", TABLE_SCHEM=" + TABLE_SCHEM
//				+ ", TABLE_NAME=" + TABLE_NAME + ", COLUMN_NAME=" + COLUMN_NAME
//				+ ", DATA_TYPE=" + DATA_TYPE + ", TYPE_NAME=" + TYPE_NAME
//				+ ", COLUMN_SIZE=" + COLUMN_SIZE + ", BUFFER_LENGTH="
//				+ BUFFER_LENGTH + ", DECIMAL_DIGITS=" + DECIMAL_DIGITS
//				+ ", NUM_PREC_RADIX=" + NUM_PREC_RADIX + ", NULLABLE="
//				+ NULLABLE + ", REMARKS=" + REMARKS + ", COLUMN_DEF="
//				+ COLUMN_DEF + ", SQL_DATA_TYPE=" + SQL_DATA_TYPE
//				+ ", SQL_DATETIME_SUB=" + SQL_DATETIME_SUB
//				+ ", CHAR_OCTET_LENGTH=" + CHAR_OCTET_LENGTH
//				+ ", ORDINAL_POSITION=" + ORDINAL_POSITION + ", IS_NULLABLE="
//				+ IS_NULLABLE + "]";
//	}
	
	
}
