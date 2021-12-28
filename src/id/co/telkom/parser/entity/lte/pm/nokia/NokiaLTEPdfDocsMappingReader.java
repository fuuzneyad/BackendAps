package id.co.telkom.parser.entity.lte.pm.nokia;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class NokiaLTEPdfDocsMappingReader extends AbstractParser{
	//this parser used to generate Mapping model from Nokia LTE Pdf Documentation
	
	public NokiaLTEPdfDocsMappingReader(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
			throw new IOException("Parsing mode Not Supported!!");
		}
		
		PDDocument pd;
		int lineNum=0;
		pd=PDDocument.load(file);;
		PDFTextStripper stripper = new PDFTextStripper();
			
		InputStream is = new ByteArrayInputStream(stripper.getText(pd).getBytes());//
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String stringLine;
		String counter_id=null;
    	String counter_name=null;
    	String meas_name=null;
    	
		while ((stringLine = br.readLine()) != null) {
			lineNum++;
        	if(lineNum==6){
        		meas_name=(stringLine);
        	}
        	if(stringLine.startsWith("Counter ID:")){
        		counter_id=stringLine.replace("Counter ID:", "").trim().split(" ")[0];
        	}else
        	if(stringLine.contains("NetAct name:")){
        		counter_name=stringLine.split("NetAct name:")[1];
        		counter_name=counter_name.trim().toUpperCase();
        		PutModel(meas_name, counter_id, counter_name);
        	}
		}
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		//TODO: Write mapping to file.. ([counter_id][counter_name][meas_name])
		String location=cynapseProp.getFILE_SCHEMA_LOC()+"NokiaLTERMapping.cfg";
		System.out.println("Generating Mapping to "+location+"..");
		try {
			FileWriter out = new FileWriter(location);
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				String name = entry.getKey();
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					out.write("["+entry2.getKey()+"]["+entry2.getValue()+"]["+name+"]\n");
					out.flush();
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
