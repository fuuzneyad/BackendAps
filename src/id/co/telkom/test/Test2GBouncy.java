package id.co.telkom.test;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Enumeration;
//
//import org.bouncycastle.asn1.*;
//import org.apache.commons.io.FileUtils;
//import org.bouncycastle.asn1.ASN1OctetString;
//import org.bouncycastle.asn1.ASN1Sequence;
//import org.bouncycastle.asn1.ASN1Set;
//import org.bouncycastle.asn1.ASN1StreamParser;
//import org.bouncycastle.asn1.BERApplicationSpecific;
//import org.bouncycastle.asn1.BERConstructedOctetString;
//import org.bouncycastle.asn1.BERSequence;
//import org.bouncycastle.asn1.BERSequenceParser;
//import org.bouncycastle.asn1.BERSet;
//import org.bouncycastle.asn1.BERTaggedObject;
//import org.bouncycastle.asn1.DERApplicationSpecific;
//import org.bouncycastle.asn1.DERBMPString;
//import org.bouncycastle.asn1.DERBitString;
//import org.bouncycastle.asn1.DERBoolean;
//import org.bouncycastle.asn1.DEREncodable;
//import org.bouncycastle.asn1.DEREnumerated;
//import org.bouncycastle.asn1.DERGeneralizedTime;
//import org.bouncycastle.asn1.DERIA5String;
//import org.bouncycastle.asn1.DERInteger;
//import org.bouncycastle.asn1.DERNull;
//import org.bouncycastle.asn1.DERObject;
//import org.bouncycastle.asn1.DERObjectIdentifier;
//import org.bouncycastle.asn1.DEROctetString;
//import org.bouncycastle.asn1.DERPrintableString;
//import org.bouncycastle.asn1.DERSequence;
//import org.bouncycastle.asn1.DERSet;
//import org.bouncycastle.asn1.DERT61String;
//import org.bouncycastle.asn1.DERTaggedObject;
//import org.bouncycastle.asn1.DERUTCTime;
//import org.bouncycastle.asn1.DERUTF8String;
//import org.bouncycastle.asn1.DERUnknownTag;
//import org.bouncycastle.asn1.DERVisibleString;
//import org.bouncycastle.asn1.util.ASN1Dump;
//import org.bouncycastle.util.encoders.Hex;
//
public class Test2GBouncy {
//
//	public static void main(String[] args) {
//		System.out.println("Hi, We are testing an asn.1!");
//		Test2GBouncy test = new Test2GBouncy();
//	}
//	
//	private void ProcessFile2(String fileloc) {
//		try{
//			File file = new File(fileloc);
//			ASN1InputStream bIn = new ASN1InputStream(FileUtils.readFileToByteArray(file));
//			Object obj = bIn.readObject();
//			DERApplicationSpecific app = (DERApplicationSpecific) obj;
//			ASN1Sequence seq = (ASN1Sequence) app.getObject(BERTaggedObject.SEQUENCE);
//			@SuppressWarnings("rawtypes")
//			Enumeration secEnum = seq.getObjects();
//			while (secEnum.hasMoreElements()) {
//			    Object seqObj = secEnum.nextElement();
//			    System.out.println(seqObj.toString());
//			}
//		}catch (IOException o){
//			o.printStackTrace();
//		}
//	}
//
//	private void ProcessFile(String fileloc){
//		File file = new File(fileloc);
//           ASN1StreamParser aIn;
//			try {
//				aIn = new ASN1StreamParser(FileUtils.readFileToByteArray(file));
//				Object o = aIn.readObject();
//				BERSequenceParser ber = (BERSequenceParser) o;
//			    System.out.println(ASN1Dump.dumpAsString(ber,true));
////				this.Read(o);
//				
////					String location="02_raw/Bouncy.txt";
////					FileWriter out = new FileWriter(location);
////					out.write(ASN1Dump.dumpAsString(ber,true));
////					out.flush();
////					out.close();
//
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//	}
//	private void Read(Object paramObject){
//		 if (paramObject instanceof DERObject){
//			 ReadDer((DERObject)paramObject);
//		 }else if (paramObject instanceof DEREncodable){
//			 ReadDer(((DEREncodable)paramObject).getDERObject());
//		 }else if(paramObject instanceof  ASN1Sequence){
//			 System.out.println("sssss");
//		 }else {
//			 System.out.println("dddddd");
//		 }
//	}
//	
//	@SuppressWarnings("rawtypes")
//	private void ReadDer(DERObject paramDERObject){
//		String str="";
//		//sequence
//		Object localObject1;
//		Object localObject2;
//	    Object localObject3;
//		if (paramDERObject instanceof ASN1Sequence)
//	    {
//			localObject1 = ((ASN1Sequence)paramDERObject).getObjects();
//			System.out.println("ASN1Sequence");
//			if(paramDERObject instanceof BERSequence){
//				System.out.println("BERSequence");
//			}else if(paramDERObject instanceof DERSequence){
//				System.out.println("DERSequence");
//			}else
//				System.out.println("Sequense");
//			while (true){
//				if (!(((Enumeration)localObject1).hasMoreElements()))
//			          return;
//				localObject3 = ((Enumeration)localObject1).nextElement();
//				if ((localObject3 == null) || (localObject3.equals(new DERNull())))
//		        {
//					System.out.println("nnull");
//		        }
//				if (localObject3 instanceof DERObject)
//					ReadDer((DERObject)localObject3);
//				else
//					ReadDer(((DEREncodable)localObject3).getDERObject());
//		    }
//	    }
//	    if (paramDERObject instanceof DERTaggedObject){
//			System.out.println("DERTaggedObject");
//			localObject2 = (DERTaggedObject)paramDERObject;
//			if (!((DERTaggedObject)localObject2).isEmpty()){
//				ReadDer(((DERTaggedObject)localObject2).getObject());
//		    }else{
//		    	System.out.println("DERTaggedObject nnull");
//		    }
//		}else{
//			if (paramDERObject instanceof BERSet){
//				System.out.println("BERSet");
//				localObject1 = ((ASN1Set)paramDERObject).getObjects();
//				while (true)
//		        {
//					if (!(((Enumeration)localObject1).hasMoreElements()))
//			            return;
//					localObject3 = ((Enumeration)localObject1).nextElement();
//					if (localObject3 != null){
//						if(localObject3 instanceof DERObject)
//							ReadDer((DERObject)localObject3);
//						else
//							ReadDer(((DEREncodable)localObject3).getDERObject());
//			        }else{
//			        	  System.out.println("nnull");
//			        }
//		        }
//		    }
//			if (paramDERObject instanceof DERSet){
//				localObject1 = ((ASN1Set)paramDERObject).getObjects();
//				while (true)
//		        {
//		          if (!(((Enumeration)localObject1).hasMoreElements()))
//		            return;
//		          localObject3 = ((Enumeration)localObject1).nextElement();
//		          if (localObject3 != null){
//						if(localObject3 instanceof DERObject)
//							ReadDer((DERObject)localObject3);
//						else
//							ReadDer(((DEREncodable)localObject3).getDERObject());
//			        }else{
//			        	  System.out.println("nnull");
//			        }
//		        }
//		    }
//			if (paramDERObject instanceof DERObjectIdentifier)
//		      {
//		        System.out.println( "ObjectIdentifier(" + ((DERObjectIdentifier)paramDERObject).getId() + ")" + str);
//		      }
//		      else if (paramDERObject instanceof DERBoolean)
//		      {
//		    	  System.out.println( "Boolean(" + ((DERBoolean)paramDERObject).isTrue() + ")" + str);
//		      }
//		      else if (paramDERObject instanceof DERInteger)
//		      {
//		    	  System.out.println( "Integer(" + ((DERInteger)paramDERObject).getValue() + ")" + str);
//		      }
//		      else if (paramDERObject instanceof BERConstructedOctetString)
//		      {
//		        localObject1 = (ASN1OctetString)paramDERObject;
//		        System.out.println(  "BER Constructed Octet String" + "[" + ((ASN1OctetString)localObject1).getOctets().length + "] ");
//		        System.out.println(dumpBinaryDataAsString( ((ASN1OctetString)localObject1).getOctets()));
//		      }
//		      else if (paramDERObject instanceof DEROctetString)
//		      {
//		    	  localObject1 = (ASN1OctetString)paramDERObject;
//		    	  System.out.println(  "DER Octet String" + "[" + ((ASN1OctetString)localObject1).getOctets().length + "] ");
//		    	  System.out.println(dumpBinaryDataAsString( ((ASN1OctetString)localObject1).getOctets()));
//		      }
//		      else if (paramDERObject instanceof DERBitString)
//		      {
//		    	  localObject1 = (ASN1OctetString)paramDERObject;
//		    	  System.out.println(  "DER Bit String" + "[" + ((ASN1OctetString)localObject1).getOctets().length + "] ");
//		    	  System.out.println(dumpBinaryDataAsString( ((ASN1OctetString)localObject1).getOctets()));
//
//		      }
//		      else if (paramDERObject instanceof DERIA5String)
//		      {
//		    	  System.out.println( "IA5String(" + ((DERIA5String)paramDERObject).getString() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERUTF8String)
//		      {
//		    	  System.out.println( "UTF8String(" + ((DERUTF8String)paramDERObject).getString() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERPrintableString)
//		      {
//		    	  System.out.println( "PrintableString(" + ((DERPrintableString)paramDERObject).getString() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERVisibleString)
//		      {
//		        System.out.println( "VisibleString(" + ((DERVisibleString)paramDERObject).getString() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERBMPString)
//		      {
//		        System.out.println( "BMPString(" + ((DERBMPString)paramDERObject).getString() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERT61String)
//		      {
//		        System.out.println( "T61String(" + ((DERT61String)paramDERObject).getString() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERUTCTime)
//		      {
//		        System.out.println( "UTCTime(" + ((DERUTCTime)paramDERObject).getTime() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERGeneralizedTime)
//		      {
//		        System.out.println( "GeneralizedTime(" + ((DERGeneralizedTime)paramDERObject).getTime() + ") " + str);
//		      }
//		      else if (paramDERObject instanceof DERUnknownTag)
//		      {
//		        System.out.println( "Unknown " + Integer.toString(((DERUnknownTag)paramDERObject).getTag(), 16) + " " + new String(Hex.encode(((DERUnknownTag)paramDERObject).getData())) + str);
//		      }
//		      else if (paramDERObject instanceof BERApplicationSpecific)
//		      {
//		      }
//		      else if (paramDERObject instanceof DERApplicationSpecific)
//		      {
//		      }
//		      else if (paramDERObject instanceof DEREnumerated)
//		      {
//		        localObject1 = (DEREnumerated)paramDERObject;
//		        System.out.println( "DER Enumerated(" + ((DEREnumerated)localObject1).getValue() + ")" + str);
//		      }
//		}
//		
//	}
//	
//	private static String dumpBinaryDataAsString(byte[] paramArrayOfByte){
//		StringBuffer localStringBuffer = new StringBuffer();
//		for (int i = 0; i < paramArrayOfByte.length; i += 32)
//		      if (paramArrayOfByte.length - i > 32)
//		      {
//		        localStringBuffer.append(new String(Hex.encode(paramArrayOfByte, i, 32)));
//		        localStringBuffer.append(calculateAscString(paramArrayOfByte, i, 32));
//		      }
//		      else
//		      {
//		        localStringBuffer.append(new String(Hex.encode(paramArrayOfByte, i, paramArrayOfByte.length - i)));
//		        localStringBuffer.append(">");
//		        localStringBuffer.append(calculateAscString(paramArrayOfByte, i, paramArrayOfByte.length - i));
//		      }
//		    return localStringBuffer.toString();
//	}
//	 private static String calculateAscString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
//	  {
//	    StringBuffer localStringBuffer = new StringBuffer();
//	    for (int i = paramInt1; i != paramInt1 + paramInt2; ++i)
//	    {
//	      if ((paramArrayOfByte[i] < 32) || (paramArrayOfByte[i] > 126))
//	        continue;
//	      localStringBuffer.append((char)paramArrayOfByte[i]);
//	    }
//	    return localStringBuffer.toString();
//	  }
}
