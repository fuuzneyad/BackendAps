package id.co.telkom.parser.entity.traversa.huawei.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserSccpGTCommandHandler extends AbstractCommandHandler implements CommandHandler{
	private ConfiguredHeader[] headers;
	private Parser reader;
	private boolean isStartExecution;
	Map<String, Object> map = new LinkedHashMap<String, Object>();//orig
	private  DataListener listener;
	
	public ParserSccpGTCommandHandler(Parser reader, String command,
			String params, Context ctx, ConfiguredHeader[] headers, DataListener listener) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		ctx.setTableName(getCommand());
		StringBuilder sb = new StringBuilder();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('(')){
			if(reader.isEqual(' ')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().contains("Address nature indicator")){
					resetHeaderLenght(sb.toString(), this.headers);
					isStartExecution = true;
				}else{
					//System.err.println("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('(')){
					done();
					return;
			}
			else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
			}

			reader.skipEOLs();
		}
		
		if(reader.isEqual('(')){
				done();
				return;
		}	
		if(!isStartExecution){
			done();
			return;
		} 
			while(!reader.isEOF() && !reader.isEqual('(') && isStartExecution){	
					listener.onBeginTable(reader.getLine(), ctx);
				while(!reader.isEOL() && !reader.isEqual('(')){
					map = new LinkedHashMap<String, Object>();//org
					parse(map, reader, headers);					
					listener.onReadyData(ctx, map, reader.getLine());
				}
				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				if (reader.isEqual('(')){
					done();
					return;
				}
			}
		
			if (reader.isEqual('(')){
				done();
				return;
			}
		reader.readUntilEOL(sb);
		if(sb.toString().trim().startsWith("(Number")){
			done();
			return;
		}
		
	}
	
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
	
		for (int i = 0; i < header.length; i++) {
			if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header[i].getLength());

			if (i==9)
			map.put(header[i].getName(), convertToDEc(sb.toString().trim()));
			else
			map.put(header[i].getName(), sb.toString().trim());//the org
	
		}

	}
	
	private int convertToDEc(String Str){
		return Integer.parseInt(Str.substring(2).trim(),16);
	}
	
	private void resetHeaderLenght(String hdr, ConfiguredHeader[] header){
		String[] hdrSpt=hdr.split(" \\b");
		for (int i=0; i<hdrSpt.length;i++){
			hdrSpt[i]+=" ";
		}
		header[0].setLength((hdrSpt[0]+hdrSpt[1]+hdrSpt[2]).length()-1);
		header[1].setLength((hdrSpt[3]+hdrSpt[4]).length());
		header[2].setLength((hdrSpt[5]+hdrSpt[6]).length());
		header[3].setLength((hdrSpt[7]+hdrSpt[8]).length());
		header[4].setLength((hdrSpt[9]+hdrSpt[10]).length());
		header[5].setLength((hdrSpt[11]+hdrSpt[12]+hdrSpt[13]).length());
		header[6].setLength((hdrSpt[14]+hdrSpt[15]+hdrSpt[16]).length());
		header[7].setLength((hdrSpt[17]+hdrSpt[18]).length());
		header[8].setLength((hdrSpt[19]+hdrSpt[20]+hdrSpt[21]).length());
		header[9].setLength((hdrSpt[22]).length());
		header[10].setLength((hdrSpt[23]).length());
		header[11].setLength((hdrSpt[24]+hdrSpt[25]+hdrSpt[26]+hdrSpt[27]+hdrSpt[28]).length());
		header[12].setLength((hdrSpt[29]+hdrSpt[30]+hdrSpt[31]).length());
		header[13].setLength((hdrSpt[32]+hdrSpt[23]).length());
	}
	
}
