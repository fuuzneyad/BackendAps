package id.co.telkom.parser.entity.pm.ericsson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERApplicationSpecific;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERT61String;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERVisibleString;
//import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.util.encoders.Hex;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class EricssonAsn1Bouncy11 extends AbstractParser{
	private Map<String, String> header  = new LinkedHashMap<String, String>();
	private Map<String, Object> map  = new LinkedHashMap<String, Object>();
	private static final Logger logger = Logger.getLogger(EricssonAsn1Bouncy11.class);
	private boolean isImplicit;
	private int hCounter,cCounter,berSeq,tagNo,tempBerSeq=0;
	private boolean isCounter=false;
	private String t_name,ne;
	
	public EricssonAsn1Bouncy11(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(final File file, final LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		String fileName=file.getName();
		String ne = fileName.substring(fileName.indexOf("_") + 1);
			ne=ne.indexOf(":") > 0? ne.substring(0, ne.indexOf(":")):ne;
			ne=ne.indexOf("_") > 0? ne.substring(0, ne.indexOf("_")):ne;
			ne=ne.indexOf(".") > 0? ne.substring(0, ne.indexOf(".")):ne;
		ctx.setNe_id(ne.trim());
		FileInputStream localFileInputStream   = new FileInputStream(file);
		ASN1InputStream localASN1InputStream = new ASN1InputStream(localFileInputStream);
		ASN1Primitive localASN1Primitive = null;
		 while ((localASN1Primitive = localASN1InputStream.readObject()) != null){
//		      System.out.println(ASN1Dump.dumpAsString(localASN1Primitive,true));
			 if (localASN1Primitive instanceof ASN1Primitive){
				 ReadAsn(localASN1Primitive,loader,ctx);
			 }else if (localASN1Primitive instanceof ASN1Encodable){
				 ReadAsn(((ASN1Encodable)localASN1Primitive).toASN1Primitive(),loader,ctx);
			 }else{
				 logger.error("Not ASN.1 Encodable");
			 }
		 }
		localASN1InputStream.close();
		loader.onEndFile();
	}
	
	@SuppressWarnings("rawtypes")
	private void ReadAsn(ASN1Primitive paramASN1Primitive, final LoaderHandlerManager loader,
			Context ctx){
		//sequence
		Object localObject1;
		Object localObject2;
	    Object localObject3;
		if (paramASN1Primitive instanceof ASN1Sequence)
	    {
			localObject1 = ((ASN1Sequence)paramASN1Primitive).getObjects();
			if(paramASN1Primitive instanceof BERSequence){
				berSeq++;
				tagNo=-1;
			}else if(paramASN1Primitive instanceof DERSequence){
				//derSeq++;
			}else{
				logger.info("Sequense");
			}
			while (((Enumeration)localObject1).hasMoreElements()){
				localObject3 = ((Enumeration)localObject1).nextElement();
				if ((localObject3 == null) || (localObject3.equals(DERNull.INSTANCE)))
					logger.info("ASN1Sequence null");
		        else
				if (localObject3 instanceof ASN1Primitive)
					ReadAsn((ASN1Primitive)localObject3,loader,ctx);
				else
					ReadAsn(((ASN1Encodable)localObject3).toASN1Primitive(),loader,ctx);
		    }
			if(paramASN1Primitive instanceof BERSequence){
				berSeq--;
				hCounter=0;
				tagNo=-1;
				//TODO: check test:
				cCounter=0;
	    		hCounter=0;
			}else if(paramASN1Primitive instanceof DERSequence){
				//derSeq--;
			}
	    }
		else if (paramASN1Primitive instanceof ASN1TaggedObject){
			localObject2 = (ASN1TaggedObject)paramASN1Primitive;
			tagNo=((ASN1TaggedObject)localObject2).getTagNo();
			if ((((ASN1TaggedObject)localObject2).isExplicit()))
				isImplicit=false;
			else
				isImplicit=true;
			if (!((ASN1TaggedObject)localObject2).isEmpty()){
				ReadAsn(((ASN1TaggedObject)localObject2).getObject(),loader,ctx);
		    }else{
		    	System.out.println("ASN1TaggedObject Empty!");
		    	logger.info("ASN1TaggedObject Empty!");
		    }
		}
		else if (paramASN1Primitive instanceof ASN1Set){
				localObject1 = (ASN1OctetString)paramASN1Primitive;
				if (paramASN1Primitive instanceof BERSet){
					logger.info("BERSet");
				}else{
					logger.info("DERSet");
				}
				while (((Enumeration)localObject1).hasMoreElements())
		        {
					localObject3 = ((Enumeration)localObject1).nextElement();
					if (localObject3 != null){
						if(localObject3 instanceof ASN1Primitive)
							ReadAsn((ASN1Primitive)localObject3,loader,ctx);
						else
							ReadAsn(((ASN1Encodable)localObject3).toASN1Primitive(),loader,ctx);
			        }else{
			        	  logger.info("ASN1Set null");
			        }
		        }
		}
		else if (paramASN1Primitive instanceof ASN1OctetString){
			localObject1 = ((ASN1OctetString)paramASN1Primitive);
			if (paramASN1Primitive instanceof BEROctetString){
			   System.out.println("BER Constructed Octet String" + "[" + ((ASN1OctetString)localObject1).getOctets().length + "] ");
			}else if (paramASN1Primitive instanceof DEROctetString)
		      {
		    	  localObject1 = (ASN1OctetString)paramASN1Primitive;
		    	  //here we go
		    	  if(berSeq==2 && tagNo==1){//ne_id,OK
		    		  cCounter=0;
		    		  hCounter=0;
//		    		  ne=dumpBinaryDataAsString(((ASN1OctetString)localObject1).getOctets());
		    	  }else
		    	  if(berSeq==2 && tagNo==4){//datetime,OK
		    		  ctx.setDatetimeid(convertDate(dumpBinaryDataAsString( ((ASN1OctetString)localObject1).getOctets()).replace("Z", "")));
//			    	  logger.info("datetime=>"+dumpBinaryDataAsString( ((ASN1OctetString)localObject1).getOctets()));
			      }
		    	  else	  
				  if(berSeq==4 && tagNo==0){//datetime_inner,OK
//				    	 logger.info("datetime_inner=>"+dumpBinaryDataAsString( ((ASN1OctetString)localObject1).getOctets()));
				  }
		    	  else	  
		    	  if(berSeq==4 && tagNo==1){//granularity,OK
		    		  try{
			    		  Integer gran = Integer.parseInt(HexToStringLong(((ASN1OctetString)localObject1).getOctets()));
			    		  gran=gran>60?gran/60:gran;
			    		  ctx.setGranularity(gran);
		    		  }catch(NumberFormatException e){}
		    	  }
			      else
				  if((berSeq==6||berSeq==5) && tagNo==0 &&!isCounter){//mo_table
					  cCounter=0;
					  String neTbl=dumpBinaryDataAsString(((ASN1OctetString)localObject1).getOctets());
					  t_name=neTbl.contains(".")?neTbl.split("\\.")[0]:neTbl;
					  String mo = neTbl.split("\\.").length>1?neTbl.split("\\.")[1]:neTbl;
					  		 mo=mo.equals("")||mo.equals("-")||mo.equals(".")?ne:mo;
					  ctx.setTableName(t_name);
					  ctx.setMo_id(mo); 
					  tempBerSeq=berSeq;
					  isCounter=true;
//					  if(berSeq==5){
//						  System.out.println("MMMMMM>>"+neTbl+">"+t_name);
//					  }
				  }
//				  else
				  if(((berSeq==tempBerSeq+1) && isCounter) && isImplicit/* && (tagNo==0||tagNo==2)*/){//counter
					     cCounter++;
					  	 String ctr =header.get("H"+cCounter);
					  		 ctr=ctr==null?"X":ctr;
						  	 String val = HexToStringLong(((ASN1OctetString)localObject1).getOctets());
							 PutModel(t_name, ctr, val);
							 map.put(ctr, val);
				  }
				  //TODO:Check
//				  else if(berSeq==tempBerSeq && isCounter && tagNo==0 && isImplicit){//counter2
//					  	 System.out.println("ssss"+HexToStringLong(((ASN1OctetString)localObject1).getOctets()));
//					  	 cCounter++;;
//					  	 String ctr =header.get("H"+cCounter);
//					  	 if(ctr!=null){
//						  	 String val = HexToStringLong(((ASN1OctetString)localObject1).getOctets());
//							 PutModel(t_name, ctr, val);
//							 map.put(ctr, val);
//					  	 }
//				  } 
				  else if((berSeq==tempBerSeq) && tagNo==2 ){//ready,OK
					  	isCounter=false;
				    	hCounter=0;
				    	cCounter=0;
			    	if(!cynapseProp.isGENERATE_SCHEMA_MODE())
			    		loader.onReadyModel(map, ctx);
					map=new LinkedHashMap<String, Object>();
				  }
		      }else{
				System.out.println("DER Octet String" + "[" + ((ASN1OctetString)localObject1).getOctets().length + "] ");
			}
		}
		else if (paramASN1Primitive instanceof DERPrintableString){
			if(hCounter==0)
				header=new LinkedHashMap<String, String>();
			hCounter++;
			header.put("H"+hCounter, ((DERPrintableString)paramASN1Primitive).getString());
			tempBerSeq=berSeq;
		}
		else if (paramASN1Primitive instanceof ASN1ObjectIdentifier){
		    logger.info( "ObjectIdentifier(" + ((ASN1ObjectIdentifier)paramASN1Primitive).getId() + ")" );
		}
		else if (paramASN1Primitive instanceof ASN1Boolean){
		     logger.info( "Boolean(" + ((ASN1Boolean)paramASN1Primitive).isTrue() + ")" );
		}
		else if (paramASN1Primitive instanceof ASN1Integer){
			 logger.info( "Integer(" + ((ASN1Integer)paramASN1Primitive).getValue() + ")" );
		}
		else if (paramASN1Primitive instanceof DERBitString){
			localObject1 = (ASN1OctetString)paramASN1Primitive;
			logger.info(  "DER Bit String" + "[" + ((ASN1OctetString)localObject1).getOctets().length + "] ");
			logger.info(dumpBinaryDataAsString( ((ASN1OctetString)localObject1).getOctets()));
		}
		else if (paramASN1Primitive instanceof DERIA5String){
			logger.info( "IA5String(" + ((DERIA5String)paramASN1Primitive).getString() + ") " );
		}
		else if (paramASN1Primitive instanceof DERUTF8String){
			logger.info( "UTF8String(" + ((DERUTF8String)paramASN1Primitive).getString() + ") " );
		}
		else if (paramASN1Primitive instanceof DERVisibleString){
			logger.info( "VisibleString(" + ((DERVisibleString)paramASN1Primitive).getString() + ") " );
		}
		else if (paramASN1Primitive instanceof DERBMPString){
			logger.info( "BMPString(" + ((DERBMPString)paramASN1Primitive).getString() + ") " );
		}
		else if (paramASN1Primitive instanceof DERT61String){
			logger.info( "T61String(" + ((DERT61String)paramASN1Primitive).getString() + ") " );
		}
		else if (paramASN1Primitive instanceof DERUTCTime){
			logger.info( "UTCTime(" + ((DERUTCTime)paramASN1Primitive).getTime() + ") " );
		}
		else if (paramASN1Primitive instanceof DERGeneralizedTime){
			logger.info( "GeneralizedTime(" + ((DERGeneralizedTime)paramASN1Primitive).getTime() + ") " );
		}
		else if (paramASN1Primitive instanceof BERApplicationSpecific){
			System.out.println("BERApplicationSpecific");
		}
		else if (paramASN1Primitive instanceof DERApplicationSpecific){
			System.out.println("DERApplicationSpecific");
		}
		
	}
	
	private static String dumpBinaryDataAsString(byte[] paramArrayOfByte){
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramArrayOfByte.length; i += 32)
		      if (paramArrayOfByte.length - i > 32)
		          localStringBuffer.append(calculateAscString(paramArrayOfByte, i, 32));
		      else
		          localStringBuffer.append(calculateAscString(paramArrayOfByte, i, paramArrayOfByte.length - i));
		return localStringBuffer.toString();
	}
	
	private static String calculateAscString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	  {
	    StringBuffer localStringBuffer = new StringBuffer();
	    for (int i = paramInt1; i != paramInt1 + paramInt2; ++i)
	    {
	      if ((paramArrayOfByte[i] < 32) || (paramArrayOfByte[i] > 126))
	        continue;
	      localStringBuffer.append((char)paramArrayOfByte[i]);
	    }
	    return localStringBuffer.toString();
	 }
	
	 private static String HexToStringLong(byte[] paramArrayOfByte){
		 //ok
		 StringBuffer localStringBuffer = new StringBuffer();
			for (int i = 0; i < paramArrayOfByte.length; i += 32)
			      if (paramArrayOfByte.length - i > 32){
			    	  localStringBuffer.append(new String(Hex.encode(paramArrayOfByte, i, 32)));
			      }else{
			    	  localStringBuffer.append(new String(Hex.encode(paramArrayOfByte, i, paramArrayOfByte.length - i)));
			      }
			try{
				return Long.toString(Long.parseLong(localStringBuffer.toString(), 16));
			} catch(NumberFormatException e){
				return null;
			}
		}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"Ericsson2GSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(400) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n"; 
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
				}
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=MyIsam;\n");
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
					
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	private static String convertDate(String val) {
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
