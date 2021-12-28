package id.co.telkom.parser.entity.dashboard.mkios.model;

public class MkiosTableMappingMeasurementModel {
	private String tableName, fieldSequence;
	private int xPos,yPos, widht, depth;
	
	public String getTableName() {
		return tableName;
	}
	
	public String getFieldSequence() {
		return fieldSequence;
	}

	public void setFieldSequence(String fieldSequence) {
		this.fieldSequence = fieldSequence;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public int getxPos() {
		return xPos;
	}
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	public int getWidht() {
		return widht;
	}
	public void setWidht(int widht) {
		this.widht = widht;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}
