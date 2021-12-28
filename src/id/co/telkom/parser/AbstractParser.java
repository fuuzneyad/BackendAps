package id.co.telkom.parser;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public abstract class AbstractParser{
	protected ParserPropReader cynapseProp;
	protected AbstractInitiator parserInit;
	protected Map<String, Map<String,String>> tableModel = new LinkedHashMap<String, Map<String,String>>();
	//local buffer
	public Object localBuffer = new Object();
	
	public AbstractParser(ParserPropReader cynapseProp, AbstractInitiator parserInit){
		this.cynapseProp=cynapseProp;
		this.parserInit=parserInit;
	}
	
	protected abstract void ProcessFile(final File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception ;
	
	public abstract void LoadBuffer(final LoaderHandlerManager loader, final Context ctx) throws Exception;
	
	protected abstract void CreateSchemaFromMap();
	
	public ParserPropReader getCynapseProp() {
		return cynapseProp;
	}
	
	public synchronized void PutModel(String t_name, String param, String value){
		Map<String, String> temp = this.tableModel.get(t_name);
		if (temp==null)
			temp=new LinkedHashMap<String, String>();
		
		String s = temp.get(param);
		temp.put(param, s==null? value : isDouble(s)? s:isDouble(value)? value:"-");
		
		this.tableModel.put(t_name, temp);
	}
	
	private boolean isDouble(String s){
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
