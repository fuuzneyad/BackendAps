package id.co.telkom.parser.entity.dashboard.customercomplain;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class CwcXmlParser10 extends AbstractParser{

	public CwcXmlParser10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		DefaultHandler handler = new DefaultHandler() {
			private String qName=null;
			int count =0;
			private boolean isStartElement;
			public synchronized void startElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException{
				this.qName=qName;
				isStartElement=true;
			}
			public synchronized void endElement(String uri, String localName,
					String qName) throws SAXException {
				isStartElement=false;
			}
			public synchronized void characters(char ch[], int start, int length) throws SAXException {
				String read =(new String(ch, start, length)).trim();
				if(qName.equals("Incident_Number") && isStartElement){
					count++;
					System.out.println(count+"."+read);
				}else
				if(qName.equals("Notes") && isStartElement){
//					count++;
//					System.out.println(count+"."+read);
				}
			}
		};
		InputStream inputStream= new FileInputStream(file);
		Reader reader = new InputStreamReader (new UnicodeWrapper(inputStream));
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		saxParser.parse(is, handler);
		reader.close();
		inputStream.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
	}

}
