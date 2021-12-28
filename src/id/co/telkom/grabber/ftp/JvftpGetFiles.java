package id.co.telkom.grabber.ftp;
import cz.dhl.io.*;
import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.FTPPropReader;
import cz.dhl.ftp.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import java.io.IOException;

public class JvftpGetFiles {
	
	private DBFileListWriter dbWriter;
	private FTPPropReader ftpProp;
	public Ftp ftpClient;
	public JvftpGetFiles(DBFileListWriter dbWriter, FTPPropReader ftpProp){
		this.dbWriter=dbWriter;
		this.ftpProp=ftpProp;
	};
	
	public JvftpGetFiles( FTPPropReader ftpProp){
		this.ftpProp=ftpProp;
	};
	
	public boolean GetFiles(
			String host, 
			String username, 
			String password, 
			int port,
			String remotePath, 
			String localPath, 
			String regexPattern, 
			String datePattern){
        boolean ret =false;
		Ftp cl = new Ftp();
        try { 
        	cl.connect(host,port);
            cl.login(username,password);
            
         CoFile file = new FtpFile(remotePath,cl);
       	 if (file.isDirectory())
	         {
		       	   CoFile fls[] = file.listCoFiles();
		       	   
			        if (fls != null) {
			            for (int n = 0; n < fls.length; n++){
			            	boolean matchRegexDate = /*= isRegexValid(datePattern) ? fls[n].getName().matches(datePattern) :*/ fls[n].getName().contains(datePattern);
			            	boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(fls[n].getName(), ftpProp.getMODUL_NAME());
			            	if (fls[n].isFile() && fls[n].getName().matches(regexPattern) && matchRegexDate ){
			            		if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
						           	 CoFile to = new LocalFile(localPath,fls[n].getName());
						       		 System.out.println("Copying file "+fls[n].getName()+" to "+localPath+"...");
						       		 ret  = CoLoad.copy(to,fls[n]);
			            		}else if(isAlreadyDownloaded)
			            			System.out.println("File "+fls[n].getName()+" Already Downloaded..");
				           	 }
			            }
			       } else 
			    	   ret = false;
			 		         		        
	        }else
	        	ret = false;
       	 
       	return  ret; 
       	 
        } catch (IOException e) {
            System.err.println(e);
            return false;  
        } finally {
            cl.disconnect();
        }
	}
	
	public boolean GetFilesFiltered(String host, String username, String password, int port,
			String remotePath, String localPath, String regexPattern, String datePattern){
        boolean ret =false;
		Ftp cl = new Ftp();
        try { 
        	cl.connect(host,port);
            cl.login(username,password);
            
         CoFile file = new FtpFile(remotePath,cl);
       	 if (file.isDirectory())
	         {
		       	   CoFile fls[] = file.listCoFiles();
		       	   
			        if (fls != null) {
			            for (int n = 0; n < fls.length; n++){
			            	boolean matchRegexDate = isRegexValid(datePattern) ? fls[n].getName().matches(datePattern) : fls[n].getName().contains(datePattern);
			            	boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(fls[n].getName(), ftpProp.getMODUL_NAME());
			            	if (fls[n].isFile() && fls[n].getName().matches(regexPattern) && matchRegexDate ){
			            		if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
						           	 CoFile to = new LocalFile(localPath,fls[n].getName());
						       		 System.out.println("Copying file "+fls[n].getName()+" to "+localPath+"...");
						       		 ret  = CoLoad.copy(to,fls[n]);
			            		}else if(isAlreadyDownloaded)
			            			System.out.println("File "+fls[n].getName()+" Already Downloaded..");
				           	 }
			            }
			       } else 
			    	   ret = false;
			 		         		        
	        }else
	        	ret = false;
       	 
       	return  ret; 
       	 
        } catch (IOException e) {
            System.err.println(e);
            return false;  
        } finally {
            cl.disconnect();
        }
	}
	
	public boolean GetFilesExtentionCheck(String host, String username, String password, int port,
			String remotePath, String localPath, String regexPattern, String datePattern, long tmpStmp, String extension){
        boolean ret =false;
		Ftp cl = new Ftp();
        try { 
        	cl.connect(host,port);
            cl.login(username,password);
            
         CoFile file = new FtpFile(remotePath,cl);
       	 if (file.isDirectory())
	         {
		       	   CoFile fls[] = file.listCoFiles();
		       	   
			        if (fls != null) {
			            for (int n = 0; n < fls.length; n++){
			            	boolean matchRegexDate = isRegexValid(datePattern) ? fls[n].getName().matches(datePattern) : fls[n].getName().contains(datePattern);
//			            	SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss"); 
			            	boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(fls[n].getName()+extension, ftpProp.getMODUL_NAME());
			            	if (fls[n].isFile() && fls[n].getName().matches(regexPattern) && matchRegexDate ){
			            		if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
						           	 CoFile to = new LocalFile(localPath,fls[n].getName());
						       		 System.out.println("Copying file "+fls[n].getName()+" to "+localPath+"...");
						       		 ret  = CoLoad.copy(to,fls[n]);
			            		}else if(isAlreadyDownloaded)
			            			System.out.println("File "+fls[n].getName()+" Already Downloaded..");
				           	 }
			            }
			       } else 
			    	   ret = false;
			 		         		        
	        }else
	        	ret = false;
       	 
       	return  ret; 
       	 
        } catch (IOException e) {
            System.err.println(e);
            return false;  
        } finally {
            cl.disconnect();
        }
	}
	
	public boolean GetSomeHuaweiXml(String host, String username, String password, int port,
			String remotePath, String localPath, String pattern,String datePattern, String modul, 
			boolean isCheckAlreadyProcessed, String regexPrefixNe){
        boolean ret =false;

        Ftp cl = new Ftp();
        try {
        	cl.connect(host,port);
            cl.login(username,password);
            
         CoFile file = new FtpFile(remotePath,cl);
       	 if (file.isDirectory())
	         {	       		 
		       	   CoFile fls[] = file.listCoFiles();
		       	   
			        if (fls != null) {
			            for (int n = 0; n < fls.length; n++){
				           	 if (fls[n].isFile() && fls[n].getName().matches(pattern)  && fls[n].getName().matches(datePattern)){
				           		boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(fls[n].getName().replace(".gz", ""), ftpProp.getMODUL_NAME());
				           		if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
				           			 CoFile to = new LocalFile(localPath,fls[n].getName());
						       		 System.out.println("Copying file "+fls[n].getName()+"...");
						       		 ret  = CoLoad.copy(to,fls[n]);
				           		}else if(isAlreadyDownloaded)
			            			System.out.println("File "+fls[n].getName()+" Already Downloaded..");
				           	 }else
				           		 if(fls[n].isDirectory() && !fls[n].getName().matches(pattern) && fls[n].getName().matches(regexPrefixNe)){
				           			CoFile fileDalem = new FtpFile(remotePath+"/"+fls[n].getName(),cl);
				           			CoFile flsDalem[] = fileDalem.listCoFiles();
				           			for (int x = 0; x < flsDalem.length; x++){
					           			if (flsDalem[x].isFile() && flsDalem[x].getName().matches(pattern.replace("($NE)", fls[n].getName())) && flsDalem[x].getName().matches(datePattern) ){
					           				boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(flsDalem[x].getName().replace(".gz", ""), ftpProp.getMODUL_NAME());
					           				if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
//						            			if(ftpProp.isFTP_CHECK_ALREADY_DWL())
//						            				dbWriter.writeFileDownload(flsDalem[x].getName(), ftpProp.getMODUL_NAME(), null);
					           					CoFile to = new LocalFile(localPath,flsDalem[x].getName());
					           					System.out.println("Copying file "+flsDalem[x].getName()+"...");
					           					ret  = CoLoad.copy(to,flsDalem[x]);
					           				}else if(isAlreadyDownloaded)
						            			System.out.println("File "+flsDalem[x].getName()+" Already Downloaded..");
							           	 }
				           			}
				           	}
			            }
			       } else 
			    	   ret = false;
			 		         		        
	        }else
	        	ret = false;
       	 
       	return  ret; 
       	 
        } catch (IOException e) {
            System.out.println(e);
            return false;  
        } finally { /* disconnect from server 
        	  * this must be always run */
            cl.disconnect();
        }
	}
	public boolean GetNodeBXml(String host, String username, String password, int port,
			String remotePath, String localPath, String pattern,String datePattern, String modul, 
			boolean isCheckAlreadyProcessed, String regexPrefixNe){
        boolean ret =false;

        Ftp cl = new Ftp();
        try {
        	cl.connect(host,port);
            cl.login(username,password);
            
         CoFile file = new FtpFile(remotePath,cl);
       	 if (file.isDirectory())
	         {
		       	   CoFile fls1[] = file.listCoFiles();
			        if (fls1 != null) {
			            for (int n = 0; n < fls1.length; n++){
			            	if(fls1[n].isDirectory()){
			            		long lastMod = fls1[n].lastModified();
			            		Timestamp date = new Timestamp(lastMod);
			            		if(convertDate(datePattern)<lastMod){
			            			System.out.println("tanggal "+date);
			            			String addedFileName1=fls1[n].getName();
				            		CoFile fls2[] = fls1[n].listCoFiles();
				            		for(int m=0;m<fls2.length;m++){
				            			lastMod = fls2[m].lastModified();
				            			if(fls2[m]!=null && fls2[m].isDirectory() && convertDate(datePattern)<lastMod){
				            				String addedFileName2=fls2[m].getName();
				            				for(CoFile f:fls2[m].listCoFiles()){
				            					String lclFn= addedFileName1+"_"+addedFileName2+"_"+f.getName();
				            					lastMod = f.lastModified();
				            					if(f.getName().matches(pattern) && f!=null && f.isFile() && convertDate(datePattern)<lastMod){
				            						boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(lclFn, ftpProp.getMODUL_NAME());
				            						if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
					            						System.out.println("Copying file "+lclFn+"...");
					            						CoFile to = new LocalFile(localPath,lclFn);
					            						ret = CoLoad.copy(to,f);
				            						}else
				            							System.out.println("File "+lclFn+" Already Downloaded..");
				            					}
				            				}
				            			}
				            		}
			            		}
			            		
			            	}
			            }
			       } else 
			    	   ret = false;
			 		         		        
	        }else
	        	ret = false;
       	 
       	return  ret; 
       	 
        } catch (IOException e) {
            System.out.println(e);
            return false;  
        } finally { /* disconnect from server 
        	  * this must be always run */
            cl.disconnect();
        }
	}
	public boolean GetSomeHuaweiXmlThreaded(String host, String username, String password, int port,
			String remotePath, String localPath, String pattern,String datePattern, String modul, boolean isCheckAlreadyProcessed){
        boolean ret =false;

        Ftp cl = new Ftp();
        try { 
        	cl.connect(host,port);
            cl.login(username,password);
            
         CoFile file = new FtpFile(remotePath,cl);
       	 if (file.isDirectory())
	         {	       		 
		       	   CoFile fls[] = file.listCoFiles();
		       	   
			        if (fls != null) {
			            for (int n = 0; n < fls.length; n++){
				           	 if (fls[n].isFile() && fls[n].getName().matches(pattern)  && fls[n].getName().matches(datePattern)){
						           	 CoFile to = new LocalFile(localPath,fls[n].getName());
						       		 System.out.println("Copying file "+fls[n].getName()+"...");
						       		 ret  = CoLoad.copy(to,fls[n]);					       		 
				           	 }else
				           		 if(fls[n].isDirectory() && !fls[n].getName().matches(pattern)){
				           			CoFile fileDalem = new FtpFile(remotePath+"/"+fls[n].getName(),cl);
				           			CoFile flsDalem[] = fileDalem.listCoFiles();
				           			for (int x = 0; x < flsDalem.length; x++){
				           				//threaded here
					           			if (flsDalem[x].isFile() 
					           					&& flsDalem[x].getName().matches(pattern.replace("($NE)", fls[n].getName())) 
					           					&& flsDalem[x].getName().matches(datePattern) ){
						           					CoFile to = new LocalFile(localPath,flsDalem[x].getName());
						           					System.out.println("Copying file "+flsDalem[x].getName()+"...");
						           					ret  = CoLoad.copy(to,flsDalem[x]);
							           	 }
				           			}
				           	}
			            }
			       } else 
			    	   ret = false;
			 		         		        
	        }else
	        	ret = false;
       	 
       	return  ret; 
       	 
        } catch (IOException e) {
            System.out.println(e);
            return false;  
        } finally { /* disconnect from server 
        	  * this must be always run */
            cl.disconnect();
        }
	}
	
	public boolean NestedGetFiles(String host, String username, String password, int port,
			String remotePath, String localPath, String pattern,String datePattern, String modul, boolean isCheckAlreadyProcessed){
		
        try { 
        	if(ftpClient==null){
	        	ftpClient = new Ftp();
	        	ftpClient.connect(host,port);
	        	ftpClient.login(username,password);
        	}
        	
        	CoFile file = new FtpFile(remotePath,ftpClient);
            if (file.isDirectory())
	         {
            	//list file..
            	CoFile fls[] = file.listCoFiles();
            	for(CoFile fileInner:fls){
            		if((fileInner!=null && fileInner.isFile()&& fileInner.getName().matches(pattern)&& fileInner.getName().matches(datePattern)) 
            		){
                		boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(fileInner.getName().replace(".gz", ""), ftpProp.getMODUL_NAME());
                		if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
	                		CoFile to = new LocalFile(localPath, fileInner.getName());
	            			CoLoad.copy(to,fileInner);
                		}else if(isAlreadyDownloaded)
	            			System.out.println("File "+fileInner.getName()+" Already Downloaded..");
            		}else 
            			if(fileInner!=null && fileInner.isDirectory()){
            				NestedGetFiles( host,  
            								   username,  
            								   password,  
            								   port,
            								   fileInner.getAbsolutePath(),  
            								   localPath, 
            								   pattern, 
            								   datePattern,  
            								   modul,  
            								   isCheckAlreadyProcessed );
            		}
            	}
	         }
        } catch (IOException e) {
            System.out.println(e);
            return false;  
        } finally { 
//        	if(ftpClient!=null)
//        		ftpClient.disconnect();
        }
		return false;
	}
	
	public ArrayList<String> GetListDirEriscsson3GXml(String host, String username, String password, int port, String remotePath, ArrayList<String> IgnoredPattern, String subRemoteDir, long time){
		ArrayList<String> path = new ArrayList<String>();
		
		subRemoteDir=subRemoteDir==null||subRemoteDir.trim().equals("")?"(.*)":subRemoteDir;
		
        try { 
        	if(ftpClient==null){
	        	ftpClient = new Ftp();
	        	ftpClient.connect(host,port);
	        	ftpClient.login(username,password);
        	}
        	
        	CoFile file = new FtpFile(remotePath, ftpClient);
            if (file.isDirectory())
	         {
            	CoFile fls[] = file.listCoFiles();
            	for(CoFile fileInner1:fls){
            		if(fileInner1.isDirectory() && !fileInner1.getName().contains("ONRM_ROOT_MO_R")&& fileInner1.getName().matches(subRemoteDir)){
            			CoFile fls2[] = fileInner1.listCoFiles();
                    	for(CoFile fileInner2:fls2){
                    		long lastMod = fileInner2.lastModified();
                    		if(fileInner2.isDirectory() && time<lastMod){
                    			path.add(fileInner2.getAbsolutePath());
                    		}//else
                    			//System.out.println("tuwiir "+fileInner2.getAbsolutePath()+"==>"+new Timestamp(fileInner2.lastModified()));
                    	}
            		}
            	}
	         }else
	        	 System.err.println(remotePath+" is Not Directory!!");
        } catch (IOException e) {
            System.out.println(e);
            return null;  
        } finally { 
        	if(ftpClient!=null)
        		ftpClient.disconnect();
        }
		return path;
	}
	
	public ArrayList<String> GetListDirHuawei(String host, String username, String password, int port, String remotePath, ArrayList<String> IgnoredPattern, String subRemoteDir, long time){
		ArrayList<String> path = new ArrayList<String>();
		
		subRemoteDir=subRemoteDir==null||subRemoteDir.trim().equals("")?"(.*)":subRemoteDir;
		
        try { 
        	if(ftpClient==null){
	        	ftpClient = new Ftp();
	        	ftpClient.connect(host,port);
	        	ftpClient.login(username,password);
        	}
        	
        	CoFile file = new FtpFile(remotePath, ftpClient);
            if (file.isDirectory())
	         {
            	CoFile fls[] = file.listCoFiles();
            	for(CoFile fileInner1:fls){
            		if(fileInner1.isDirectory() && fileInner1.getName().matches(subRemoteDir)){
            			path.add(fileInner1.getAbsolutePath());
//            			CoFile fls2[] = fileInner1.listCoFiles();
//                    	for(CoFile fileInner2:fls2){
//                    		long lastMod = fileInner2.lastModified();
//                    		if(fileInner2.isDirectory() && time<lastMod){
//                    			path.add(fileInner2.getAbsolutePath());
//                    		}
//                    	}
            		}
            	}
	         }else
	        	 System.err.println(remotePath+" is Not Directory!!");
        } catch (IOException e) {
            System.out.println(e);
            return null;  
        } finally { 
        	if(ftpClient!=null)
        		ftpClient.disconnect();
        }
		return path;
	}
	
	private boolean isRegexValid(String rgx){
		try{
			Pattern.compile(rgx);
			return true;
		} catch (PatternSyntaxException e){
			return false;
		}
	}
	
	
	private static long convertDate(String val){
		try {
			SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyyMMdd");
			Date lFromDate1 = datetimeFormatter1.parse(val);
			//return  new Timestamp(lFromDate1.getTime());
			return lFromDate1.getTime();
		}catch(Exception e){
			return new Date().getTime();
		}
	}
}
