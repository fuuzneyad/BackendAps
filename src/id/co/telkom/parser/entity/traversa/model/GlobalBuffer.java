package id.co.telkom.parser.entity.traversa.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalBuffer {
	//this buffer is used for storing initial data ne2vertex 
	private Map<String, Vertex> neOfVertex = new LinkedHashMap<String, Vertex>();
	//this buffers are used for re-formating initial data to other format
	private Map<String,Vertex> ownGtOfVertex = new LinkedHashMap<String, Vertex>();
	private Map<String,Vertex> ownMsrnOfVertex = new LinkedHashMap<String, Vertex>();
	private Map<String,Vertex> ownIPOfVertex = new LinkedHashMap<String, Vertex>();
	private Map<Integer,Vertex> ownSpOfVertex = new LinkedHashMap<Integer, Vertex>();
	//this buffer used by e_msc
	private EMscBuffer eMscBuf = new EMscBuffer();
	//this buffer used by i_itp
	private CItpBuffer iItpBuf = new CItpBuffer();
	//this buffer used by s_stp
	private SStpBuffer sStpBuf = new SStpBuffer();
	
	public SStpBuffer getSstpBuf(){
		return sStpBuf;
	}
	
	public CItpBuffer getIitpBuf(){
		return iItpBuf;
	}
	
	public EMscBuffer geteMscBuf() {
		return eMscBuf;
	}

	public Map<String, Vertex> getNeOfVertex() {
		return neOfVertex;
	}

	public Vertex getVertexFromNE(String ne){
		return neOfVertex.get(ne);
	}
	
	public Map<Integer,Vertex> getOwnSpOfVertex(){
		return ownSpOfVertex;
	}
	
	public Map<String,Vertex> getOwnGTOfVertex(){
		return ownGtOfVertex;
	}
	
	public Map<String,Vertex> getOwnMSRNOfVertex(){
		return ownMsrnOfVertex;
	}
	
	public Map<String,Vertex> getOwnIPOfVertex(){
		return ownIPOfVertex;
	}
	
	public void setSPToVertex(String ne, int sp){
		Vertex v = neOfVertex.get(ne);
			   if(v==null) v = new Vertex();
	    v.setOWN_SP_DEC(sp);
	    neOfVertex.put(ne, v);
	}
	
	public void setGTToVertex(String ne, String gt){
		Vertex v = neOfVertex.get(ne);
			   if(v==null) v = new Vertex();
	    v.setOWN_GT(gt);
	    neOfVertex.put(ne, v);
	}
	
	public void setMSRNToVertex(String ne, String msrn){
		Vertex v = neOfVertex.get(ne);
			   if(v==null) v = new Vertex();
	    v.setOWN_MSRN(msrn);
	    neOfVertex.put(ne, v);
	}
	
	public void setIPToVertex(String ne, String ip){
		Vertex v = neOfVertex.get(ne);
			   if(v==null) v = new Vertex();
	    v.setIP(ip);
	    neOfVertex.put(ne, v);
	}
	public void setMSRNsToVertex(String ne, Msrns msrns){
		Vertex v = neOfVertex.get(ne);
			   if(v==null) v = new Vertex();
	    v.setMsrn(msrns);
	    neOfVertex.put(ne, v);
	}
	
	public void setVendorToVertex(String ne, String vendor){
		Vertex v = neOfVertex.get(ne);
			   if(v==null) v = new Vertex();
	    v.setVENDOR(vendor);
	    neOfVertex.put(ne, v);
	}
	
	public void setNEToVertex(String ne){
		Vertex v = neOfVertex.get(ne);
			   if(v==null) v = new Vertex();
	    v.setNE_ID(ne);
	    v.setNE_NAME(ne);
	    neOfVertex.put(ne, v);
	}
	
	public void empty(){
		ownSpOfVertex.clear();
		ownGtOfVertex.clear();
		ownMsrnOfVertex.clear();
		ownIPOfVertex.clear();
	}
	
	public void transform(){
		//ownSpOfVertex
		for (Map.Entry<String, Vertex> is : neOfVertex.entrySet()){
			for(Integer sp : is.getValue().getOWN_SP_DEC())
				ownSpOfVertex.put(sp, is.getValue());
		}
		//ownGtOfVertex
		for (Map.Entry<String, Vertex> is : neOfVertex.entrySet()){
			for(String gt : is.getValue().getOWN_GT())
				ownGtOfVertex.put(gt, is.getValue());
		}
		//ownMsrnOfVertex
		for (Map.Entry<String, Vertex> is : neOfVertex.entrySet()){
			for(String msrn : is.getValue().getOWN_MSRN())
				ownMsrnOfVertex.put(msrn, is.getValue());
		}
		//ownIPOfVertex
		for (Map.Entry<String, Vertex> is : neOfVertex.entrySet()){
			for(String ip : is.getValue().getIP())
				ownIPOfVertex.put(ip, is.getValue());
		}
	}
	
}
