package id.co.telkom.grabber.ftp;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.FTPPropReader;

public class SftpGetFiles {
	
	private FTPPropReader ftpProp;
	private DBFileListWriter dbWriter;
	private Session session;
	private Channel channel;
	
	public SftpGetFiles( FTPPropReader ftpProp){
		this.ftpProp=ftpProp;
		init();
	}
	
	public SftpGetFiles(DBFileListWriter dbWriter, FTPPropReader ftpProp){
		this.dbWriter=dbWriter;
		this.ftpProp=ftpProp;
		init();
	}
	
	private void init(){
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(ftpProp.getFTP_USERNAME(),
					ftpProp.getFTP_HOST(),
					Integer.parseInt(ftpProp.getFTP_PORT()));
			
			session.setPassword(ftpProp.getFTP_PASSWD());
			
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications",//kerberos bypass 
            "publickey,keyboard-interactive,password");
			session.setConfig(config);
			
			session.connect();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}

	}
	
	public List<String> ListFiles(String remoteDir, String fileToGet){
		List<String> ret = new ArrayList<String>();
		try {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(remoteDir);
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls(fileToGet);
			for(ChannelSftp.LsEntry entry : list) {
				ret.add(entry.getFilename());
			}
		} catch (SftpException e) {
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (JSchException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}
	
	public void GetFiles(String remoteDir, String fileToGet){
		try {
			if(channel==null || !channel.isConnected())
				channelConnect();
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(remoteDir);
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls(fileToGet);
			for(ChannelSftp.LsEntry entry : list) {
				boolean isAlreadyDownloaded = ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(entry.getFilename().replace(".gz", ""), ftpProp.getMODUL_NAME());
				if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
					System.out.println("downloading "+entry.getFilename());
					channelSftp.get(entry.getFilename(), ftpProp.getFTP_LOCAL_DIR()+"/"+ entry.getFilename());
				}else
					System.out.println("File "+entry.getFilename()+" Already Downloaded..");
			}
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void GetFiles(String remoteDir, String fileToGet, String regexFilePattern){
		try {
			if(channel==null || !channel.isConnected())
				channelConnect();
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(remoteDir);
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls(fileToGet);
				for(ChannelSftp.LsEntry entry : list) {
					if(entry.getFilename().matches(regexFilePattern)){
					boolean isAlreadyDownloaded = ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(entry.getFilename().replace(".gz", ""), ftpProp.getMODUL_NAME());
					if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
						System.out.println("downloading "+entry.getFilename());
						channelSftp.get(entry.getFilename(), ftpProp.getFTP_LOCAL_DIR()+"/"+ entry.getFilename());
					}else
						System.out.println("File "+entry.getFilename()+" Already Downloaded..");
				}
			}
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
	}
	
	public void GetFilesCDRHaud(String remoteDir,String fileToGet, String regexFilePattern){
		try {
			if(channel==null || !channel.isConnected())
				channelConnect();
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(remoteDir+"/"+fileToGet);
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.csv*");
				for(ChannelSftp.LsEntry entry : list) {
					if(entry.getFilename().matches(regexFilePattern)){
					boolean isAlreadyDownloaded = ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(entry.getFilename().replace(".gz", ""), ftpProp.getMODUL_NAME());
					if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
						System.out.println("downloading "+entry.getFilename());
						channelSftp.get(entry.getFilename(), ftpProp.getFTP_LOCAL_DIR()+"/"+ entry.getFilename());
					}else
						System.out.println("File "+entry.getFilename()+" Already Downloaded..");
				}
			}
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
	}
	
	public void GetFilesEri2G(String remoteDir, String fileToGet, String regexFilePattern){
		try {
			if(channel==null || !channel.isConnected())
				channelConnect();
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(remoteDir);
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = channelSftp.ls(fileToGet);
				for(ChannelSftp.LsEntry entry : list) {
					if(entry.getFilename().matches(regexFilePattern)){
					boolean isAlreadyDownloaded = ftpProp.isFTP_CHECK_ALREADY_DWL() && dbWriter.isFileAlreadyDownloaded(entry.getFilename().replace(".gz", "")+".unb", ftpProp.getMODUL_NAME());
					if(!ftpProp.isFTP_CHECK_ALREADY_DWL() || !isAlreadyDownloaded){
						System.out.println("downloading "+entry.getFilename());
						channelSftp.get(entry.getFilename(), ftpProp.getFTP_LOCAL_DIR()+"/"+ entry.getFilename());
					}else
						System.out.println("File "+entry.getFilename()+" Already Downloaded..");
				}
			}
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
	}
	public void disConnect(){
		session.disconnect();
	}
	public void channelConnect() throws JSchException{
		channel = session.openChannel("sftp");
		channel.connect();
	}
}
