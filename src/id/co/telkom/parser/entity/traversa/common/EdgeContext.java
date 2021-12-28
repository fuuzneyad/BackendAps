package id.co.telkom.parser.entity.traversa.common;

public class EdgeContext {
	private String t_name;
	private String edge_id, edge_source, edge_dest, edge_type, edge_link_type, edge_additional_param, edge_poolname, edge_destPC, edge_srcPC;
	private int edgeWeight;
	private boolean isPool,isUp;

	public boolean isUp() {
		return isUp;
	}
	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}
	public int getEdgeWeight() {
		return edgeWeight;
	}
	public void setEdgeWeight(int edgeWeight) {
		this.edgeWeight = edgeWeight;
	}
	public String getEdge_destPC() {
		return edge_destPC;
	}
	public void setEdge_destPC(String edge_destPC) {
		this.edge_destPC = edge_destPC;
	}
	public String getEdge_srcPC() {
		return edge_srcPC;
	}
	public void setEdge_srcPC(String edge_srcPC) {
		this.edge_srcPC = edge_srcPC;
	}
	public String getT_name() {
		return t_name;
	}
	public void setTableName(String t_name) {
		this.t_name = t_name;
	}
	public String getEdge_id() {
		return edge_id;
	}
	public void setEdge_id(String edge_id) {
		this.edge_id = edge_id;
	}
	public String getEdge_source() {
		return edge_source;
	}
	public void setEdge_source(String edge_source) {
		this.edge_source = edge_source;
	}
	public String getEdge_dest() {
		return edge_dest;
	}
	public void setEdge_dest(String edge_dest) {
		this.edge_dest = edge_dest;
	}
	public String getEdge_type() {
		return edge_type;
	}
	public void setEdge_type(String edge_type) {
		this.edge_type = edge_type;
	}
	public String getEdge_link_type() {
		return edge_link_type;
	}
	public void setEdge_link_type(String edge_link_type) {
		this.edge_link_type = edge_link_type;
	}
	public String getEdge_additional_param() {
		return edge_additional_param;
	}
	public void setEdge_additional_param(String edge_additional_param) {
		this.edge_additional_param = edge_additional_param;
	}
	public String getEdge_poolname() {
		return edge_poolname;
	}
	public void setEdge_poolname(String edge_poolname) {
		this.edge_poolname = edge_poolname;
	}
	public boolean isPool() {
		return isPool;
	}
	public void setPool(boolean isPool) {
		this.isPool = isPool;
	}
	@Override
	public String toString() {
		return "EdgeContext [t_name=" + t_name + ", edge_id=" + edge_id
				+ ", edge_source=" + edge_source + ", edge_dest=" + edge_dest
				+ ", edge_type=" + edge_type + ", edge_link_type="
				+ edge_link_type + ", edge_additional_param="
				+ edge_additional_param + ", edge_poolname=" + edge_poolname
				+ ", isPool=" + isPool + "]";
	}
}
