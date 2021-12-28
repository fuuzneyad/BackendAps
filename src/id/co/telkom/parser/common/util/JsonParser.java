package id.co.telkom.parser.common.util;

import java.util.Map;

import com.json.generators.JSONGenerator;
import com.json.generators.JsonGeneratorFactory;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class JsonParser {
	
	private JSONParser parser;
	private JSONGenerator generator;
	private JsonParserFactory factoryParser;
	private JsonGeneratorFactory factoryGenerator;
	
	public JsonParser(){
		
	}
	
	public void initParser(){
		factoryParser=JsonParserFactory.getInstance();
		parser=factoryParser.newJsonParser();
		//parser.initialize(new FileInputStream(new File("d:/Map.xml")));		
		//parser.setValidating(true);
	}
	
	public void initGenerator(){
		factoryGenerator=JsonGeneratorFactory.getInstance();
		generator=factoryGenerator.newJsonGenerator();
	}
	
	public String getJsonFormat(Map<String,Object> line){
		return generator.generateJson(line);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getMapContent(String line){
		return parser.parseJson(line);
	}
	
}
