package id.co.telkom.parser.entity.traversa.model;

import java.util.List;

public class EricssonMsrn implements Msrns{

	private String R, USAGE,LAI, CELLCON, AIDX;
	private List<String> MSRN;
	
	@Override
	public List<String> getMsrns() {
		return MSRN;
	}

	public String getR() {
		return R;
	}

	public void setR(String r) {
		R = r;
	}

	public String getUSAGE() {
		return USAGE;
	}

	public void setUSAGE(String uSAGE) {
		USAGE = uSAGE;
	}

	public String getLAI() {
		return LAI;
	}

	public void setLAI(String lAI) {
		LAI = lAI;
	}

	public String getCELLCON() {
		return CELLCON;
	}

	public void setCELLCON(String cELLCON) {
		CELLCON = cELLCON;
	}

	public String getAIDX() {
		return AIDX;
	}

	public void setAIDX(String aIDX) {
		AIDX = aIDX;
	}
	
}
