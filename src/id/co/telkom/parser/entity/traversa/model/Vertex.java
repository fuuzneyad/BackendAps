package id.co.telkom.parser.entity.traversa.model;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
	private List<Integer> OWN_SP_DEC = new ArrayList<Integer>();
	private List<String>OWN_GT = new ArrayList<String>();
	private List<String>OWN_MSRN = new ArrayList<String>();
	private List<String>IP = new ArrayList<String>();
	private String NE_NAME;
	private String NE_ID;
	private String VENDOR;
	private Msrns msrn;
	
	public String getVENDOR() {
		return VENDOR==null?"NONE":VENDOR;
	}
	public void setVENDOR(String vENDOR) {
		VENDOR = vENDOR;
	}
	public List<String> getIP() {
		return IP;
	}
	public void setIP(List<String> iP) {
		IP = iP;
	}
	public List<Integer> getOWN_SP_DEC() {
		return OWN_SP_DEC;
	}
	public List<String> getOWN_MSRN() {
		return OWN_MSRN;
	}
	public String getNE_NAME() {
		return NE_NAME;
	}
	public void setNE_NAME(String nE_NAME) {
		NE_NAME = nE_NAME;
	}
	public String getNE_ID() {
		return NE_ID;
	}
	public void setNE_ID(String nE_ID) {
		NE_ID = nE_ID;
	}
	public List<String> getOWN_GT() {
		return OWN_GT;
	}
	public void setOWN_SP_DEC(int oPC_DEC) {
		boolean exist=false;
		for(int i:OWN_SP_DEC){
			if(i==oPC_DEC){
				exist=true;
				break;
			}
		}
		if(!exist)
			OWN_SP_DEC.add(oPC_DEC);
	}
	public void setOWN_MSRN(String oWN_MSRN) {
		boolean exist=false;
		for(String i:OWN_MSRN){
			if(i.equals(oWN_MSRN)){
				exist=true;
				break;
			}
		}
		if(!exist)
			OWN_MSRN.add(oWN_MSRN);
	}
	public void setIP(String iP) {
		boolean exist=false;
		for(String i:IP){
			if(i.equals(iP)){
				exist=true;
				break;
			}
		}
		if(!exist)
			IP.add(iP);
	}
	public void setOWN_GT(String oWN_GT) {
		boolean exist=false;
		for(String i:OWN_GT){
			if(i.equals(oWN_GT)){
				exist=true;
				break;
			}
		}
		if(!exist)
			OWN_GT.add(oWN_GT);
	}
	public Msrns getMsrn() {
		return msrn;
	}
	public void setMsrn(Msrns msrn) {
		this.msrn = msrn;
	}
	@Override
	public String toString() {
		return "Vertex [NE_NAME="+ NE_NAME+", OWN_SP_DEC=" + OWN_SP_DEC + ", OWN_GT=" + OWN_GT
				+ ", OWN_MSRN=" + OWN_MSRN + ", IP=" + IP + ","
				+ " NE_ID=" + NE_ID + ", VENDOR=" + VENDOR + "]";
	}
	
	
}
