package id.co.telkom.parser.entity.cm.nokia;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class NokiaCMParser20 extends AbstractParser{

	public NokiaCMParser20(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		FileInputStream fstream = new FileInputStream(file);
	    BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	    String stringLine;
	    @SuppressWarnings("unused")
		String qName, meas, listToGet;
//		String valueListToGet="";
	    while ((stringLine = br.readLine()) != null)
	    {	
	    	qName=stringLine.trim();
	    	if(qName.startsWith("<log>")){
			}else
			if(qName.startsWith("<managedObject")){
				//ready insert
				if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
					loader.onReadyModel(map, ctx);
					map = new LinkedHashMap<String, Object>();
				}
				//tableName
				ctx.setTableName(getAttribute(qName,"class"));
				ctx.setVersion(getAttribute(qName,"version"));
				ctx.setMo_id(getAttribute(qName,"distName"));
				if(ctx.mo_id!=null && ctx.mo_id.indexOf("/")>0)
					for (String s:ctx.mo_id.split("/")){
						if(s.startsWith("BSC")||s.startsWith("RNC")||s.startsWith("MRBTS")){
							ctx.setNe_id(s);
							break;
						}
					}
				PutModel(ctx.t_name, null, null);
					
			}else
			if(qName.startsWith("<list>") && getAttribute(qName,"name")!=null){
				listToGet=getAttribute(qName,"name");
			}
			//here
			else
			if(qName.startsWith("<p"))
				meas=getAttribute(qName, "name");
	    }
	    br.close();
	    fstream.close();
	    loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		// TODO Auto-generated method stub
		
	}
	
	private static String GetMeas(String ln){
		String tag=ln.substring(ln.indexOf("<")+1, ln.indexOf(">"));
		return ln.replace("<"+tag+">", "").replace("</"+tag+">", "").trim();
	}
	
	private static String getAttribute(String ln, String att){
		String tagAtt=ln.substring(ln.indexOf("<")+1, ln.indexOf(">"));
		System.out.println(tagAtt);
		for(String s:tagAtt.split("\\s+")){
			if(s.startsWith(att) && s.contains("="))
				return s.split("=")[1].trim();
		}
		return null;
	}
	
	public static void main(String[] args){
		System.out.println(GetMeas("<abc>dasdasd</abc>"));
		System.out.println(getAttribute("<abc class=qwer >dasdasd<abc>","class"));
	}
}
