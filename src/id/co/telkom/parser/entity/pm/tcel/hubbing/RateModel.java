package id.co.telkom.parser.entity.pm.tcel.hubbing;

public class RateModel {
	private String countryName;
	private String countryCode;
	private String operatorName;
	private Double rate;
	private String idparametercountry;
	
	
	public String getIdparametercountry() {
		return idparametercountry;
	}
	public void setIdparametercountry(String idparametercountry) {
		this.idparametercountry = idparametercountry;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	@Override
	public String toString() {
		return "RateModel [countryName=" + countryName + ", countryCode="
				+ countryCode + ", operatorName=" + operatorName + ", rate="
				+ rate + "]";
	}
	
}
