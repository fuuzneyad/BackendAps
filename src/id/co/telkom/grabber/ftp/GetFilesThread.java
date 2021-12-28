package id.co.telkom.grabber.ftp;


import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.FTPPropReader;
import id.co.telkom.parser.common.util.ThreadTracker;

public class GetFilesThread extends Thread{
	private final ThreadTracker threadTracker;
	private final FTPPropReader ftpProp;
	private final String remotePath;
	private final String localPath;
	private final String regexPattern; 
	private final String datePattern;
	DBFileListWriter dbWriter;
	public GetFilesThread(
			ThreadTracker threadTracker, 
			FTPPropReader ftpProp,
			DBFileListWriter dbWriter,
			String remotePath, 
			String localPath, 
			String regexPattern, 
			String datePattern){
		
		this.dbWriter= dbWriter;
		this.threadTracker=threadTracker;
		this.ftpProp=ftpProp;
		this.regexPattern=regexPattern;
		this.datePattern=datePattern;
		this.remotePath=remotePath;
		this.localPath=localPath;
	}
	
	@Override
	public void run() {
		threadTracker.incrementCounter();
		try{
			JvftpGetFiles ftp = new JvftpGetFiles(dbWriter, ftpProp);
			ftp.GetFiles(ftpProp.getFTP_HOST(), 
					ftpProp.getFTP_USERNAME(),
					ftpProp.getFTP_PASSWD(), 
					Integer.parseInt(ftpProp.getFTP_PORT()), 
					remotePath, 
					localPath, 
					regexPattern, 
					datePattern);
		}finally {
			threadTracker.decreaseCounter();
		}
	}
}
