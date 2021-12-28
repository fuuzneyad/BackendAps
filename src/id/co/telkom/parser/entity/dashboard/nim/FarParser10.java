package id.co.telkom.parser.entity.dashboard.nim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.CharParserExtender;

public class FarParser10 extends AbstractParser {
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, String> buffer = new LinkedHashMap<String, String>();
	private Map<String, String> csvHeader = new LinkedHashMap<String, String>();
	
	public FarParser10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		final CharParserExtender parser = new CharParserExtender(reader){};
		String[] spt = file.getName().split("_");
		String dt = "0000-00-00"; 
		for(String s:spt){
			dt = convertDate(s.split("\\.")[0]);
			if(dt!=null)
				break;
		}
		ctx.setDatetimeid(dt);
		
		if(file.getName().contains("Sites_Report"))
			ParseSite(parser, ctx,loader);
		else
		if(file.getName().contains("Category_Report"))
			ParseCategory(parser,ctx,loader);
		else
		if(file.getName().contains("Asset_Key"))
			ParseAssetKey(parser,ctx,loader); 
		else
		if(file.getName().endsWith(".csv"))
			ParseCsv(file, ctx, loader);
		
		reader.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+cynapseProp.getSOURCE_ID()+"_FarSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE  IF NOT EXISTS "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT '',\n");
				sb.append("\t`DATETIME_ID` DATETIME DEFAULT '0000-00-00 00:00:00',\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					String typeData = "VARCHAR("+(entry2.getValue().length()+100)+"),\n"; 
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
				}
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=InnoDB;\n");
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
					
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void ParseCsv(File f, Context ctx, LoaderHandlerManager loader) throws IOException{
		final String T_NAME="FAR_CSV";
		ctx.setTableName(T_NAME);
		FileReader fr = new FileReader(f);
		CSVReader reader = new CSVReader(fr, '|');
		String [] splitted;
		int line =0;
		while ((splitted = reader.readNext()) != null) {
			line++;
			if(line==1){
				for(int i=0;i<splitted.length;i++){
					csvHeader.put("h"+i, splitted[i].toUpperCase());
					PutModel(T_NAME, splitted[i].toUpperCase(), "");
				}
			}else{
				for(int i=0;i<splitted.length;i++)
					map.put(csvHeader.get("h"+i), splitted[i]);
				
				if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
					loader.onReadyModel(map, ctx);
					map = new LinkedHashMap<String, Object>();
				}
			}
		}
		reader.close();
		fr.close();
	}
	
	private void ParseSite(CharParserExtender parser, Context ctx, LoaderHandlerManager loader) throws IOException{
		ctx.setTableName("SITE_REPORT");
		StringBuilder sb = new StringBuilder();
		boolean start=false;
		parser.read();
		while (!parser.isEOF()) {
			parser.readUntilEOL(sb);
			if(sb.toString().contains("SITE ID")&&sb.toString().contains("SITE NAME")){
				//TODO: can make auto detect length here..
				start=true;
			}
			if(start){
				parser.skipLines(1).skipEOLs();
				while(!parser.isEOL()){
					ConfiguredHeader[] header = FarHeader.siteReportHeader;
					final int lastIdx = header.length-1;
					for (int i = 0;i <= lastIdx; i++) {
						ConfiguredHeader configuredHeader = header[i];
						if(lastIdx==i)
							parser.readUntilEOL(sb);
						else{
							parser.read(sb, configuredHeader.getLength());
						}
						
						String s = sb.toString().trim();
						if(i==0 && isNumber(s)){
							if(!map.isEmpty()){
								if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
									loader.onReadyModel(map, ctx);
									map = new LinkedHashMap<String, Object>();
								}else 
								for(Map.Entry<String, Object> mp:map.entrySet()){
									PutModel(ctx.t_name, mp.getKey(), mp.getValue()==null?"":mp.getValue().toString());
								}
							}
						}
						
						Object data = map.get(configuredHeader.getName());
						s = data!=null ? (data.toString())+" "+s : s;
						map.put(configuredHeader.getName(), s);
					}

					parser.skipEOL();
				}
				start=false;
			}
			
			parser.skipEOL();
		}
		if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
			loader.onReadyModel(map, ctx);
			map = new LinkedHashMap<String, Object>();
		}
	}
	
	private void ParseCategory(CharParserExtender parser,Context ctx, LoaderHandlerManager loader) throws IOException{
		ctx.setTableName("CATEGORY_REPORT");
		StringBuilder sb = new StringBuilder();
		boolean start=false;
		parser.read();
		while (!parser.isEOF()) {
			parser.readUntilEOL(sb);
			if(sb.toString().contains(" Asset-Description ")&&sb.toString().contains("Asset Number ")){
				start=true;
			}
			if(start){
				parser.skipLines(1).skipEOLs();
				while(parser.isNumber()){
					Parse(parser, FarHeader.categoryReportHeader);
					if(!cynapseProp.isGENERATE_SCHEMA_MODE())
						loader.onReadyModel(map, ctx);else
					for(Map.Entry<String, Object> mp:map.entrySet()){
						PutModel(ctx.t_name, mp.getKey(), mp.getValue()==null?"":mp.getValue().toString());
					}
					map = new LinkedHashMap<String, Object>();
					parser.skipEOL();
				}
				start=false;
			}
			
			parser.skipEOL();
		}
	}
	
	private void ParseAssetKey(CharParserExtender parser, Context ctx, LoaderHandlerManager loader) throws IOException{
		ctx.setTableName("ASSET_KEY_REPORT");
		StringBuilder sb = new StringBuilder();
		boolean start=false;
		parser.read();
		while (!parser.isEOF()) {
			parser.readUntilEOL(sb);
			if(sb.toString().contains("Cc     Asset-Description ")){
				start=true;
			}
			if(start){
				parser.skipLines(1).skipEOLs();
				while(!parser.isEOF()){
					Parse(parser, FarHeader.assetKeyReportHeader);
					if(!cynapseProp.isGENERATE_SCHEMA_MODE())
						loader.onReadyModel(map, ctx);else
					for(Map.Entry<String, Object> mp:map.entrySet()){
						PutModel(ctx.t_name, mp.getKey(), mp.getValue()==null?"":mp.getValue().toString());
					}
					map = new LinkedHashMap<String, Object>();
					parser.skipEOL();
				}
			}
			
			parser.skipEOL();
		}
	}
	
	private void Parse(CharParserExtender reader, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0;i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
			}
			
			String s = sb.toString().trim();
			map.put(configuredHeader.getName(), s);
			if(configuredHeader.copied){
				if(s.equals(""))
					map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
				else
					buffer.put(configuredHeader.getName(), s);
			}
		}
	}
	
	private static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		if(val.length()=="yyyyMMdd".length())
			format="yyyyMMdd";else
		if(val.length()=="ddMMyy".length())
			format="ddMMyy";else
		if(val.length()=="MMMyy".length())
			format="MMMyy";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return null;}
	}
	
	
	private boolean isNumber(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
}
