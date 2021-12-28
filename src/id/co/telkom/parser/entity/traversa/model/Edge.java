package id.co.telkom.parser.entity.traversa.model;

import java.util.List;

public class Edge {
	private String vertex_id;
	private Vertex source;
	private Vertex destination;
	private List<Integer> GTS;
	private List<Integer> MSRNS;
//	private int loadShare;
//	private int priority;
	
	public Vertex getSource() {
		return source;
	}
	public String getVertex_id() {
		return vertex_id;
	}
	public void setVertex_id(String vertex_id) {
		this.vertex_id = vertex_id;
	}
	public void setSource(Vertex source) {
		this.source = source;
	}
	public Vertex getDestination() {
		return destination;
	}
	public void setDestination(Vertex destination) {
		this.destination = destination;
	}
	public List<Integer> getGTS() {
		return GTS;
	}
	public void setGTS(List<Integer> gTS) {
		GTS = gTS;
	}
	public List<Integer> getMSRNS() {
		return MSRNS;
	}
	public void setMSRNS(List<Integer> mSRNS) {
		MSRNS = mSRNS;
	}
}
