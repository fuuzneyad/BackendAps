package id.co.telkom.parser.entity.pm.tcel.hubbing;

import java.util.Map;

import id.co.telkom.parser.common.model.StandardMeasurementModel;

public class VotlvcdrModels {
	private Map<String, StandardMeasurementModel> modelMap;
	private Map<String, RateModel> rateMap;
	
	public Map<String, StandardMeasurementModel> getModelMap() {
		return modelMap;
	}
	public void setModelMap(Map<String, StandardMeasurementModel> modelMap) {
		this.modelMap = modelMap;
	}
	public Map<String, RateModel> getRateMap() {
		return rateMap;
	}
	public void setRateMap(Map<String, RateModel> rateMap) {
		this.rateMap = rateMap;
	}
}
