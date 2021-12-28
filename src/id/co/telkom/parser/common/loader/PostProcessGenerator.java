package id.co.telkom.parser.common.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DBMetadata;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.util.JsonParser;

public class PostProcessGenerator {
	
	
	public static String GenerateCsvMapMysql(Context context, Map<String,DBMetadata> hdr, Map<String,Object> map){
		//http://www.tech-recipes.com/rx/2345/import_csv_file_directly_into_mysql/
		//LOAD DATA LOCAL INFILE 'R1_H1275068427-2013102310.loa' INTO TABLE R1_H1275068427
		//fields terminated by ',' enclosed by '\"' lines terminated by '\n' (field1, field2, field3);
		map.put("ENTRY_DATE",context.date);
		map.put("DATETIME_ID",context.dateTimeid);
		map.put("GRANULARITY",context.granularity);
		map.put("CNT",context.granularity);
		map.put("SOURCE_ID",context.source);
		map.put("NE_ID",context.ne_id);
		map.put("MO_ID",context.mo_id);
		map.put("VERSION",context.version);
		
		String r="";
		for(Map.Entry<String,DBMetadata> oCols : hdr.entrySet()){
			Object val=map.get(oCols.getValue().getCOLUMN_NAME());
			if(val!=null)
				r+="\""+val.toString().replace("\\","\\\\").replace("\"","\\\"")+"\",";
			else
				r+="\""+"\\N"+"\",";
		}
		if(r.length()>1)
			r=r.substring(0,r.length()-1);
		return r;
	}
	
	public static String GenerateJson(Map<String, Object> map, Context context, OutputMethodPropReader om,JsonParser jp){
		Map<String,String> headers =new LinkedHashMap<String, String>();
		headers.put("path",om.getRPC_PATH().replaceAll("\\$source",context.source).replaceAll("\\$datetime",context.dateTimeid.substring(0, 13).replaceAll(":", "-")+"-00-00"));
    
		Map<String, Object> json=new LinkedHashMap<String, Object>();
		json.put("TABLE_NAME",context.t_name);
		json.put("ENTRY_DATE",String.valueOf(context.date));
		json.put("DATETIME_ID",context.dateTimeid);
		json.put("SOURCE",context.source);
		json.put("VERSION", context.version);
		json.put("NE_ID",context.ne_id);
		json.put("MO_ID",context.mo_id);
		map.put("GRANULARITY",String.valueOf(context.granularity));
		json.put("DATA", map);
		String row = jp.getJsonFormat(json);
		row = row.substring(1, row.length()-1);
		return row;
	}
	
	public static String GenerateOraHeader(Map<String,DBMetadata> hdr, String t_name){
		String r="LOAD DATA\nINFILE *\nAPPEND\nINTO TABLE "+t_name+"\nFIELDS TERMINATED BY '|' \nTRAILING NULLCOLS\n(\n";
		for(Map.Entry<String,DBMetadata> oCols : hdr.entrySet()){
			if(oCols.getValue().getTYPE_NAME().equals("DATE")||oCols.getValue().getTYPE_NAME().contains("TIME"))
				r+=oCols.getValue().getCOLUMN_NAME()+" date \"yyyy-mm-dd hh24:mi:ss\",\n";
			else
				r+=oCols.getValue().getCOLUMN_NAME()+",\n";
		}
		if(r.length()>2)
			r=r.substring(0,r.length()-2);
		r+="\n)\nBEGINDATA\n";
		return r;
	}
	
	public static String GenerateMysqlHeaderDelimiter(Map<String,DBMetadata> hdr){
		String r="";
		for(Map.Entry<String,DBMetadata> oCols : hdr.entrySet()){
			r+="\""+oCols.getValue().getCOLUMN_NAME()+"\""+",";
		}
		if(r.length()>1)
			r=r.substring(0,r.length()-1);
		return r;
	}
	
	
	public static  String GenerateOraDelimited(Context context, Map<String,DBMetadata> hdr, Map<String,Object> map){
		map.put("ENTRY_DATE",context.date);
		map.put("START_TIME",context.dateTimeid);
		map.put("DATETIME_ID",context.dateTimeid);
		map.put("GRANULARITY",context.granularity);
		map.put("GRANULARITY_PERIOD",context.granularity);
		map.put("CNT",context.granularity);
		map.put("SOURCE_ID",context.source);
		map.put("NE_ID",context.ne_id);
		map.put("SUB_NE",context.sub_ne_id);
		map.put("MO_ID",context.mo_id);
		map.put("NE",context.ne_id);
		map.put("SUB_NE",context.sub_ne_id);
		map.put("MO",context.mo_id);
		map.put("VERSION",context.version);
		
		String r="";
		for(Map.Entry<String,DBMetadata> oCols : hdr.entrySet()){
			Object val=map.get(oCols.getValue().getCOLUMN_NAME());
			r=val!=null?r+val.toString()+"|":r+"|";
		}
//		if(r.length()>1)
//			r=r.substring(0,r.length()-1);
		return r;
	}
	
	public static String convertDate(String val, String format) {
		if(val==null)
			return "";
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat myFormat = new SimpleDateFormat(format);
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return "";}
		
	}
}
