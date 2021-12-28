package id.co.telkom.parser.entity.traversa;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.model.Vertex;
import id.co.telkom.parser.entity.traversa.propreader.TraversaInitialMappingPropReader;

public class TraversaXmlInitiatorReader {

	public static void writeXML(GlobalBuffer buf, TraversaInitialMappingPropReader traversaProp){
		try{
			System.out.println("Write xml config to "+traversaProp.getINITIAL_CONFIG_FILE());
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("TraversaInitialData");
			doc.appendChild(rootElement);
			for(Map.Entry<String,Vertex> lcl : buf.getNeOfVertex().entrySet()){
				Element vertex = doc.createElement("NetworkElement");
				rootElement.appendChild(vertex);
				Attr attr = doc.createAttribute("id");
				attr.setValue(lcl.getKey());
				vertex.setAttributeNode(attr);
				
				Element neName =doc.createElement("NEName");
				neName.appendChild(doc.createTextNode(lcl.getValue().getNE_NAME().toString()));
				vertex.appendChild(neName);
				
				Element VENDOR =doc.createElement("Vendor");
				VENDOR.appendChild(doc.createTextNode(lcl.getValue().getVENDOR().toString()));
				vertex.appendChild(VENDOR);
				
				Element OWN_SP_DEC =doc.createElement("OwnSP");
				OWN_SP_DEC.appendChild(doc.createTextNode(lcl.getValue().getOWN_SP_DEC().toString()));
				vertex.appendChild(OWN_SP_DEC);
				
				Element OWN_GT =doc.createElement("OwnGT");
				OWN_GT.appendChild(doc.createTextNode(lcl.getValue().getOWN_GT().toString()));
				vertex.appendChild(OWN_GT);
				
				Element OWN_MSRN =doc.createElement("OwnMSRN");
				OWN_MSRN.appendChild(doc.createTextNode(lcl.getValue().getOWN_MSRN().toString()));
				vertex.appendChild(OWN_MSRN);
				
				Element IP =doc.createElement("IP");
				IP.appendChild(doc.createTextNode(lcl.getValue().getIP().toString()));
				vertex.appendChild(IP);
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
//			StreamResult result =  new StreamResult(System.out);
			StreamResult result = new StreamResult(new File(traversaProp.getINITIAL_CONFIG_FILE()));
			transformer.transform(source, result);
		}catch(ParserConfigurationException e){
			e.printStackTrace();
		}catch(TransformerException e){
			e.printStackTrace();
		}
	}
	
	public static void readXML(final GlobalBuffer buf, TraversaInitialMappingPropReader traversaProp){
		System.out.println("Reading xml config file "+traversaProp.getINITIAL_CONFIG_FILE());
		File f = new File(traversaProp.getINITIAL_CONFIG_FILE());
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		try{
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser saxParser = factory.newSAXParser();
			
				DefaultHandler handler = new DefaultHandler() {
					private String qName;
					private Attributes attributes;
					private boolean isStartElement;
					private String neName;
					@Override
					public void endElement(String uri, String localName,
							String qName)
							throws SAXException {
						super.endElement(uri, localName, qName);
						isStartElement=false;
					}

					@Override
					public void startElement(String uri, String localName,
							String qName, Attributes attributes) throws SAXException {
						super.startElement(uri, localName, qName, attributes);
						isStartElement=true;
						this.qName=qName;
						this.attributes=attributes;
					}
					@Override
					public void characters(char[] ch, int start, int length)
							throws SAXException {
						super.characters(ch, start, length);
						String read =(new String(ch, start, length)).trim();
						if(this.qName.equals("NetworkElement") && isStartElement){
							neName=getAttribute(attributes, "id");
							buf.setNEToVertex(neName);
//							System.out.println(neName);
						}else if(this.qName.equals("Vendor") && isStartElement){
							buf.setVendorToVertex(neName, read);
						}else if(this.qName.equals("OwnSP") && isStartElement){
							read=read.replace("[", "").replace("]", "");
							for(String s:read.split(",")){
								s=s.trim();
								if(s!=null && !s.equals("") && toInt(s)!=0)
									buf.setSPToVertex(neName, toInt(s));
							}
						}else if(this.qName.equals("OwnGT") && isStartElement){
							read=read.replace("[", "").replace("]", "");
							for(String s:read.split(",")){
								s=s.trim();
								if(s!=null && !s.equals(""))
									buf.setGTToVertex(neName, s);
							}
						}else if(this.qName.equals("OwnMSRN") && isStartElement){
							read=read.replace("[", "").replace("]", "");
							for(String s:read.split(",")){
								s=s.trim();
								if(s!=null && !s.equals(""))
									buf.setMSRNToVertex(neName, s);
							}
						}else if(this.qName.equals("IP") && isStartElement){
							read=read.replace("[", "").replace("]", "");
							for(String s:read.split(",")){
								s=s.trim();
								if(s!=null && !s.equals(""))
									buf.setIPToVertex(neName, s);
							}
						}
						
					}
					private String getAttribute(Attributes attributes,String attToGet){
						for (int i=0;i<attributes.getLength();i++){
							if(attToGet.equalsIgnoreCase(attributes.getQName(i)))
								return attributes.getValue(i);
						}
						return null;
					}
					private Integer toInt(String s){
						try {
							return Integer.parseInt(s);
						}
						catch(NumberFormatException e){
							return 0;
						}
					}
				};
			InputStream inputStream= new FileInputStream(f);
			Reader reader = new InputStreamReader(inputStream,"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			reader.close();
			inputStream.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
