package id.co.telkom.parser.entity.cli.peinet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.CommandErrorException;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.CommandParser;
import id.co.telkom.parser.common.charparser.CompleteException;
import id.co.telkom.parser.common.charparser.IgnoreException;
import id.co.telkom.parser.common.charparser.ParseException;
import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.common.charparser.UnhandledCommandException;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.entity.cli.peinet.commands.UnhandledCommandHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;



@SuppressWarnings({ "unused", "deprecation" })
public class CliPEInetParserReaderV10 extends CommandParser  {
	private final DataListener listener;
	private final Context ctx;
	private Map<String, CommandHandlerFactory> commands = new HashMap<String, CommandHandlerFactory>();
	private static final Logger logger = Logger.getLogger(CliPEInetParserReaderV10.class);
	private Map<String, StandardMeasurementModel> modelMap;
	
	private void addCommand(CommandHandlerFactory commandHandlerFactory){
		commands.put(commandHandlerFactory.getCommand(), commandHandlerFactory);
	}
	
	private void addCommand(String key, CommandHandlerFactory commandHandlerFactory){
		commands.put(key, commandHandlerFactory);
	}
	
	public CliPEInetParserReaderV10(Reader reader, DataListener listener, Context ctx, Map<String, StandardMeasurementModel> modelMap) {
		super(reader);
		this.listener=listener;
		this.ctx=ctx;
		this.modelMap=modelMap;
	}
	
	public StringBuffer GenerateSchema(){
		StringBuffer sb = new StringBuffer();

		Map<CommandHandlerFactory,String> uniq = new LinkedHashMap<CommandHandlerFactory,String>();
		for(Map.Entry<String,CommandHandlerFactory> mp:commands.entrySet()){
			uniq.put(mp.getValue(), "");
		}
		for(Map.Entry<CommandHandlerFactory,String> u:uniq.entrySet()){
			sb.append(u.getKey().getTableSchema());
		}
		return sb;
	}
	
	public void parse() throws IOException{
		ctx.setTableName("alarmdia");
		listener.onBeginTable(getLine(), ctx);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		read();
		CommandHandler commandHandler = null;
		String cust="", ip="";
		String txt =  TanggalSekarang()+"\nAlarm for ";
		while (!isEOF()) {
			readUntilEOL(sb).skipEOL();
			String s = sb.toString().trim();
			if(s.contains("!PING TO")){
				cust = s.split("!PING TO")[1].trim();
				logger.info(cust);
				readUntil('#', sb).readUntilEOL(sb).skipEOL();
				ip=sb.toString();
			}else if(s.startsWith("Success rate is")){
				logger.info(s);
				String successrate = s.split("Success rate is")[1].trim().split(" ")[0];
				map.put("cust", cust);
				map.put("ip", ip);
				map.put("succ_rate",successrate);
				listener.onReadyData(ctx, map, getLine());
				map = new LinkedHashMap<String, Object>();
//				if(toInt(successrate)<=60){
//					txt+="\nDIA "+cust+" ip "+ip+" success rate:"+successrate+"%";
//				}
			}
				
		}
	}
	
	 private static String TanggalSekarang() {
	    	Calendar cal = Calendar.getInstance();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    	return sdf.format(cal.getTime());
	 }
}
