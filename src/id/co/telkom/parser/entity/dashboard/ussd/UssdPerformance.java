package id.co.telkom.parser.entity.dashboard.ussd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class UssdPerformance   {
	private File file;
	private LoaderHandlerManager loader;
	private	Context ctx; 
	@SuppressWarnings("unused")
	private ParserPropReader cynapseProp;
	@SuppressWarnings("unused")
	private AbstractInitiator cynapseInit;
	private UssdParser10 parser;
	
	public UssdPerformance (final ParserPropReader cynapseProp,
			final AbstractInitiator cynapseInit, File file,
			final LoaderHandlerManager loader, 
			final Context ctx, 
			final UssdParser10 parser) {
		this.parser=parser;
		this.cynapseInit=cynapseInit;
		this.cynapseProp=cynapseProp;
		this.ctx=ctx;
		this.loader=loader;
		this.file=file;
	}


	public void ProcessFile()  throws Exception {
		ctx.setTableName("PERFORMANCE");
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, String> header = new LinkedHashMap<String, String>();
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new DataInputStream(fstream)));
		
		String strLine;
		int line=0;
		while ((strLine = br.readLine()) != null) {
			line++;
			String spt[] = strLine.split("\\s+");
			if(line==1){//header
				for(int i=0;i<spt.length;i++){
					header.put("H"+i, spt[i]);
				}
			}else
			if(line!=2){//content'
				for(int i=0;i<spt.length;i++){
						map.put(header.get("H"+i).toUpperCase(), spt[i]);
						parser.PutModel(ctx.t_name, header.get("H"+i).toUpperCase(), spt[i]);
				}
			}
//			if(!map.isEmpty())
			loader.onReadyModel(map, ctx);
			map = new LinkedHashMap<String, Object>();
		}
		
		br.close();
		fstream.close();
	}
	
	protected static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}

}
