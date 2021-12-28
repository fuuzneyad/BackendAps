package id.co.telkom.grabber.ftp;
import cz.dhl.io.*;
import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.FTPPropReader;
import cz.dhl.ftp.*;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import java.io.IOException;

public class JvftpGetFilesHuaweiCM {
	
	private DBFileListWriter dbWriter;
	private FTPPropReader ftpProp;
	public Ftp ftpClient;
	public JvftpGetFilesHuaweiCM(DBFileListWriter dbWriter, FTPPropReader ftpProp){
		this.dbWriter=dbWriter;
		this.ftpProp=ftpProp;
	};
	
	public JvftpGetFilesHuaweiCM( FTPPropReader ftpProp){
		this.ftpProp=ftpProp;
	};
	
	public boolean GetFilesHuaweiCM(String host, String username, String password, int port,
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
			            	boolean isAlreadyDownloaded=ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(fls[n].getName().replace(".zip", ".txt"), ftpProp.getMODUL_NAME());
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
	
	
	private boolean isRegexValid(String rgx){
		try{
			Pattern.compile(rgx);
			return true;
		} catch (PatternSyntaxException e){
			return false;
		}
	}
	
}
