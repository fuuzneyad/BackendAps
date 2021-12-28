package id.co.telkom.executor;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import id.co.telkom.grabber.ftp.SftpGetFiles;
import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.FTPPropReader;

public class GetFilesSftpExecutor
{
  private Properties prop = new Properties();
  private SftpGetFiles ftp;
  private FTPPropReader ftpProp;
  private static ApplicationContext context;
  
  public static void main(String[] args)
  {
	  
//	  String regex ="^(cdrs-main).*$";
//	  String [] test = {"cdrs-main-210903-180000-185959-p-001-r-0084458.csv", "cdrs-dlr-210903-180000-185959-p-001-r-0084458.csv"};
//	  for(String s:test) {
//		  if(s.matches(regex)) {
//			  System.out.println(s+" OK berhasil");
//		  }else
//			  System.out.println(s+" NOK");
//	  }
	  
    if (args.length < 1)
    {
      System.out.println("Usage with [config file]..");
      System.exit(0);
    }
    GetFilesSftpExecutor getFile = new GetFilesSftpExecutor(args[0]);
    
    String externalParam = null;
    if (args.length >= 2) {
      externalParam = args[1];
    }
    externalParam = externalParam == null ? "*" : externalParam;
    
    getFile.GetFiles(externalParam);
  }
  
  public GetFilesSftpExecutor(String config)
  {
    String fileName = "01_config/CynapseFTP.cfg";
    File configFile = new File(fileName);
    if ((!configFile.exists()) || (!configFile.isFile()))
    {
      fileName = config;
      configFile = new File(fileName);
      if ((!configFile.exists()) || (!configFile.isFile()))
      {
        System.err.println("Configuration file Not Found");
        System.exit(1);
      }
    }
    try
    {
      this.prop.load(new FileInputStream(fileName));
    }
    catch (IOException e)
    {
      System.out.println("No " + fileName + " file?\n");System.exit(0);
    }
  }
  
  private void GetFiles(String externalParam)
  {
    for (int source = 1; source <= 10; source++)
    {
      this.ftpProp = new FTPPropReader(this.prop, source);
      if ((this.ftpProp.isValid()) && (this.ftpProp.isGET_FTP()))
      {
        System.out.println("Getting SFTP for " + this.ftpProp.getMODUL_NAME());
        String regexPrefixNe = this.ftpProp.getFTP_SUBREMOTE_DIR();
        
        regexPrefixNe = (regexPrefixNe == null) || (regexPrefixNe.trim().equals("")) ? "(.*)" : regexPrefixNe;
        
        Properties propOutput = new Properties();
        try
        {
          propOutput.load(new FileInputStream(this.ftpProp.getOUTPUT_CONFIG()));
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        String appContext = this.ftpProp.getPARSER_CONFIG();
        System.setProperty("JDBC_OUTPUTMETHOD", this.ftpProp.getOUTPUT_CONFIG());
        context = new FileSystemXmlApplicationContext(appContext);
        DataSource ds = (DataSource)context.getBean("dataSource");
        DBFileListWriter dbWriter = new DBFileListWriter(ds);
        this.ftp = new SftpGetFiles(dbWriter, this.ftpProp);
        System.out.println("getting file in dir "+ftpProp.getFTP_REMOTE_DIR());
//        this.ftp.GetFiles(ftpProp.getFTP_REMOTE_DIR(), "*"+externalParam+"*", ftpProp.getFTP_FILEPATTERN());
        this.ftp.GetFilesCDRHaud(ftpProp.getFTP_REMOTE_DIR(), externalParam, ftpProp.getFTP_FILEPATTERN());
        this.ftp.disConnect();
      }else
    	  continue;
    }
  }
  
}
