package id.co.telkom.parser.entity.cm.ericsson;

import java.io.File;
//import java.io.IOException;
//import java.util.LinkedHashMap;
//import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class EricssonCM3GParser10 extends AbstractParser{

	public EricssonCM3GParser10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		String filename=file.getName();
		String type=filename.contains("RJK") ? "RJK" : filename.contains("RBD") ? "RBD" : filename.contains("RNC") ? "RNC" : "."; 
		String ne=filename.contains(type) ? filename.substring(filename.indexOf(type), filename.indexOf(".")):null;
		//tsel
		ne=file.getName().substring(0, file.getName().indexOf("."));
//		String ne=null;
		String date_time= file.getName().contains("_201")? file.getName().substring(file.getName().indexOf("201"), file.getName().indexOf("201")+8):null;
		ctx.setFileName(file.getName());
		ctx.setDatetimeid(date_time);
		ctx.setNe_id(ne);
		
//		SAXParserFactory factory = SAXParserFactory.newInstance();
//		SAXParser saxParser = factory.newSAXParser();
//		DefaultHandler handler = new DefaultHandler() {
//			private boolean isRef, isUtran, isIubLinkRef, isUtranRelation, isGsmrelation=false;
//			private String qName,tableName,tableNameFix, tmp;
//			private Attributes attributes=null;
//			private Map<String, Object> map = new LinkedHashMap<String, Object>();
//			private Map<String, Object> map2 = new LinkedHashMap<String, Object>();
//			@Override
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes)
//					throws SAXException {
//				super.startElement(uri, localName, qName, attributes);
//				
//				this.qName=qName.trim();
//				this.attributes=attributes;
//				
//				if(this.qName.equals("xn:MeContext") ){
//					String me=getAttribute("id");
//					if(me!=null && !me.equals("") && me.length()>2){
//						ctx.setMe_id(me);
//					}
//				}else
//				if(this.qName.contains("xn:VsDataContainer")){
////					System.out.println("-------------------[container_id-->"+getAttribute("id")+"--------------]");
//					ctx.setContainer_id(getAttribute("id"));
//					if(cynapseProp.isGENERATE_SCHEMA_MODE())
//						map.put("CONTAINER_ID", ctx.container_id);
//				}else 
//					if(this.qName.startsWith("un:")){//mo_id, mo_name
//						String mo_id=getAttribute("id");
//						if(mo_id!=null){
//							ctx.setMo_id(getAttribute("id"));
//						}
//						ctx.setMo_name(this.qName.replace("un:", ""));
//						
//						if(this.qName.equals("un:UtranCell"))
//							isUtran=true;else
//						if(this.qName.equals("un:IubLink"))
//							isIubLinkRef=true;else
//						if(this.qName.equals("un:UtranRelation")){
//							isUtranRelation=true;
//						}
//					}
//					else 
//					if(this.qName.startsWith("gn:GsmRelation")){//gsmrelation moid
//						String mo_id=getAttribute("id");
//						if(mo_id!=null){
//							ctx.setMo_id(getAttribute("id"));
//						}
//						ctx.setMo_name(this.qName.replace("gn:", ""));
//						isGsmrelation=true;
//					}
//			}
//
//			@Override
//			public void endElement(String uri, String localName,
//					String qName) throws SAXException {
//				
//				if (qName.equals("un:attributes") || qName.equals("un:UtranRelation") ){
//					if(!cynapseProp.isGENERATE_SCHEMA_MODE() && map!=null && !map.isEmpty()){
//						if(qName.equals("un:UtranRelation")){//utk vsDataUtranRelation
//							map.put("CONTAINER_ID", ctx.container_id);
//							map.put("ME", ctx.me_id);
//							map.put("MO", ctx.mo_id);
//							map.put("MO_NAME", ctx.mo_name);
//						}
//						
//						loader.onReadyModel(map, ctx);
//						map = new LinkedHashMap<String, Object>();
//					}
//					if(isUtran)
//						isUtran=false;
//					if(isIubLinkRef)
//						isIubLinkRef=false;
//					if(isUtranRelation)
//						isUtranRelation=false;
//				}else if(qName.equals("gn:attributes")){
//					if(!cynapseProp.isGENERATE_SCHEMA_MODE() && map!=null && !map.isEmpty()){
//						if(qName.equals("un:UtranRelation")){
//							map.put("CONTAINER_ID", ctx.container_id);
//							map.put("ME", ctx.me_id);
//							map.put("MO", ctx.mo_id);
//							map.put("MO_NAME", ctx.mo_name);
//						}
//						
//						loader.onReadyModel(map, ctx);
//						map = new LinkedHashMap<String, Object>();
//					}
//					if(isGsmrelation)
//						isGsmrelation=false;
//				}else
//				if (qName.equals("es:vsDataMeContext") && map!=null && !cynapseProp.isGENERATE_SCHEMA_MODE()){
//					ctx.setMo_name("vsDataMeContext");
//					map.put("CONTAINER_ID", ctx.container_id);
//					map.put("ME", ctx.me_id);
//					map.put("MO", ctx.mo_id);
//					map.put("MO_NAME", ctx.mo_name);
//					loader.onReadyModel(map, ctx);
//					map = new LinkedHashMap<String, Object>();
//				}
//				
//				super.endElement(uri, localName, qName);
//			}
//
//			@Override
//			public void characters(char[] ch, int start, int length)
//					throws SAXException {
//				super.characters(ch, start, length);
//				
//				String read =(new String(ch, start, length)).trim();
//				if(this.qName.equals("xn:vsDataType") && !read.equals("")){
//					tableName=read.toUpperCase();
//							
//					//ready model ???
//					if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
//							map.put("CONTAINER_ID", ctx.container_id);
//							map.put("ME", ctx.me_id);
//							map.put("MO", ctx.mo_id);
//							map.put("MO_NAME", ctx.mo_name);
//							
//							if(!map.isEmpty()){
//								loader.onReadyModel(map, ctx);
//								map = new LinkedHashMap<String, Object>();
//							}
//					}
//						
//					
//				}
//				else if((this.qName.equals("xn:locationName")||
//							this.qName.equals("xn:userDefinedState") ||
//							this.qName.equals("xn:vendorName") ||
//							this.qName.equals("xn:userLabel") ||
//							this.qName.equals("xn:managedElementType") ||
//							this.qName.equals("xn:swVersion") ||
//							this.qName.equals("xn:managedBy")
//						) &&!read.equals("")){
//						String table = "MANAGEDELEMENT";
//						if (!cynapseProp.isGENERATE_SCHEMA_MODE()){
//							ctx.setTableName(table);
//							ctx.setMo_name("IubLink");
//							map.put("CONTAINER_ID", ctx.container_id);
//							map.put("ME", ctx.me_id);
//							map.put("MO", ctx.mo_id);
//							map.put("MO_NAME", ctx.mo_name);
//							map.put(this.qName.replace("xn:", "").toUpperCase(), read);
//							if(this.qName.equals("xn:managedBy")){
//								loader.onReadyModel(map, ctx);
//								map = new LinkedHashMap<String, Object>();
//							}
//						}
//						else{
//							PutModel(table,this.qName.replace("xn:", "").toUpperCase(), read);
//						}
//				}
//				else if((this.qName.equals("xn:userDefinedNetworkType")
//						)&&!read.equals("")){//subnetwork
//					String table = "SUBNETWORK";
//					if (!cynapseProp.isGENERATE_SCHEMA_MODE()){
//						ctx.setTableName(table);
//						ctx.setMo_name("IubLink");
//						map.put("CONTAINER_ID", ctx.container_id);
//						map.put(this.qName.replace("xn:", "").toUpperCase(), read);
//						map.put("USERLABEL", ctx.ne_id);
//						
//							loader.onReadyModel(map, ctx);
//							map = new LinkedHashMap<String, Object>();
//					}
//					else{
//						PutModel(table,this.qName.replace("xn:", "").toUpperCase(), read);
//					}
//				}else if(this.qName.equals("un:nodeBFunctionIubLink")&&!read.equals("")){
//					String t_iub="IUBLINK";
//					if (!cynapseProp.isGENERATE_SCHEMA_MODE()){
//						ctx.setTableName(t_iub);
//						ctx.setMo_name("IubLink");
//						map.put("CONTAINER_ID", ctx.container_id);
//						map.put("ME", ctx.me_id);
//						map.put("MO", ctx.mo_id);
//						map.put("MO_NAME", ctx.mo_name);
//						map.put("IUBLINKNODEBFUNCTION", read);
////						loader.onReadyModel(map, ctx);
////						map = new LinkedHashMap<String, Object>();
//						map.put(this.qName.replace("un:", "").toUpperCase(), read);
//					}else{
//						PutModel(t_iub, this.qName.replace("un:", "").toUpperCase(), read);
//					}
//				}
//				else if(this.qName.startsWith("un:") && isUtran && !read.equals("")){
////					System.out.println("gotcha "+this.qName+"="+read);
//					String t="UTRANCELL";
//					if (!cynapseProp.isGENERATE_SCHEMA_MODE()){
//						ctx.setTableName(t);
//						ctx.setMo_name("UtranCell");
//						map.put("CONTAINER_ID", ctx.container_id);
//						map.put("ME", ctx.me_id);
//						map.put("MO", ctx.mo_id);
//						map.put("MO_NAME", ctx.mo_name);
//						map.put(this.qName.replace("un:", "").toUpperCase(), read);
//					}else{
//						PutModel(t, this.qName.replace("un:", "").toUpperCase(), read);
//					}
//				}
//				else if(this.qName.startsWith("un:") && isUtranRelation && !read.equals("")){
////					System.out.println("gotcha "+this.qName+"="+read);
//					String t="UTRANRELATION";
//					if (!cynapseProp.isGENERATE_SCHEMA_MODE()){
//						ctx.setTableName(t);
//						ctx.setMo_name("UtranRelation");
//						map.put("ME", ctx.me_id);
//						map.put("MO", ctx.mo_id);
//						map.put("MO_NAME", ctx.mo_name);
//						if(!read.equals("")){
//							map.put(this.qName.replace("un:", "").toUpperCase(), read);
//							loader.onReadyModel(map, ctx);
//							map = new LinkedHashMap<String, Object>();
//						}
//					}else{
//						if(!read.equals(""))
//							PutModel(t, this.qName.replace("un:", "").toUpperCase(), read);
//					}
//				}
//				else if(this.qName.startsWith("un:") && isIubLinkRef && !read.equals("")){
//					String t="IUBLINK_REF";
//					if (!cynapseProp.isGENERATE_SCHEMA_MODE()){
//						ctx.setTableName(t);
//						ctx.setMo_name("UtranRelation");
//						map.put("CONTAINER_ID", ctx.container_id);
//						map.put("ME", ctx.me_id);
//						map.put("MO", ctx.mo_id);
//						map.put("MO_NAME", ctx.mo_name);
////						loader.onReadyModel(map, ctx);
////						map = new LinkedHashMap<String, Object>();
//						map.put("PARAM", this.qName.replace("un:", ""));
//						map.put("VALUE", read);
//					}else{
//						PutModel(t, "PARAM", this.qName);
//						PutModel(t, "VALUE", read);
//					}
//				}
//				else if(this.qName.startsWith("gn:") && isGsmrelation && !read.equals("")){
//					String t="GSMRELATION";
//					if (!cynapseProp.isGENERATE_SCHEMA_MODE()){
//						ctx.setTableName(t);
//						map.put("ME", ctx.me_id);
//						map.put("MO", ctx.mo_id);
//						map.put("MO_NAME", ctx.mo_name);
//						if(!read.equals("")){
//							map.put(this.qName.replace("gn:", "").toUpperCase(), read);
//							loader.onReadyModel(map, ctx);
//							map = new LinkedHashMap<String, Object>();
//						}
//					}else{
//						if(!read.equals(""))
//							PutModel(t, this.qName.replace("gn:", "").toUpperCase(), read);
//					}
//				}
//				else//data
//					if(this.qName.startsWith("es:")){//
//							if(!read.equals("")){
//								String param=this.qName.replace("es:", "").toUpperCase();
//								//pendekin
//								if(param.length()>30)
//									param=param.substring(0,30);
//								
//								if(param.equals(tmp)){//
//									isRef=true;//
//								}
//								else
//									isRef=false;
//								
//								tmp=param;//
//								
//								if(tableName.length()>26)
//									tableName=tableName.substring(0,26);
//								if(isRef)
//									tableNameFix=tableName+"_REF";else
//										tableNameFix=tableName;
//								
//								
//									ctx.setTableName(tableNameFix);
//									
////									System.out.println(param+"--->>"+read);
//									
//									if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
//											if(isRef){// ready model
//												if(!map2.isEmpty()){
//													map2.put("CONTAINER_ID", ctx.container_id);
//													map2.put("ME", ctx.me_id);
//													map2.put("MO", ctx.mo_id);
//													map2.put("MO_NAME", ctx.mo_name);
//													map2.put("PARAM", param);
//													map2.put("VALUE", read);
//													loader.onReadyModel(map2, ctx);
//													map2 = new LinkedHashMap<String, Object>();
//												}
//												
//											}else{
//												map.put(param, read);
//												map2.put(param, read);
//											}
//										}else{
//											PutModel(tableNameFix, param, read);
////											if(ctx.t_name.equalsIgnoreCase("VSDATAUTRANCELL"))
////												System.out.println(param+" "+read);
//										}
//							}
//					}
//				
//					
//			}
//			
//			protected String getAttribute(String attToGet){
//				for (int i=0;i<attributes.getLength();i++){
//					if(attToGet.equalsIgnoreCase(attributes.getQName(i)))
//						return attributes.getValue(i);
//				}
//				return null;
//			}
//			
//		};
//		
//		saxParser.parse(fileloc, handler);
//		//last
		if(!cynapseProp.isGENERATE_SCHEMA_MODE())
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
