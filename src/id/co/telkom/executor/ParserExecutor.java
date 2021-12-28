package id.co.telkom.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.ParserManager;
import id.co.telkom.parser.ParserThread;
import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DBTableModel;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.tableoutput.MetadataOperation;
import id.co.telkom.parser.common.util.ThreadTracker;


public class ParserExecutor {
	static final String AUTHOR="Fauzan Baskoro";
	static final String VERSION="1.0.1";

	//-----------------------------------------------------------------------------//
	
	
	private ParserPropReader parserProp;
	private static ExecutorService poolRef;
	private int maxThread;
	private Timestamp entry_date;
	private static ApplicationContext context;
	private Properties prop;
	private static final Logger logger = Logger.getLogger(ParserExecutor.class);
	private final Properties l4jPr = new Properties();
	private static final long start = System.currentTimeMillis();
//	private Object tempGlobbalBuffer;
	
	public static void main(String[] args) {
		
		logger.info("Parser "+VERSION+" Started..");
		
		ParserExecutor executor = new ParserExecutor(args);
		
		String externalParam=null;
		if(args.length>=2)
			externalParam=args[1];
		externalParam = externalParam==null ? "(.*)" : "(.*)"+externalParam+"(.*)";
		
		for (int source=1; source<=20; source++){
			executor.Execute(externalParam, source);
		}
		
		long finish =System.currentTimeMillis()-start;
		final long secondInMillis = 1000;
		final long minuteInMillis = secondInMillis * 60;
		final long hourInMillis = minuteInMillis * 60;
		final long dayInMillis = hourInMillis * 24;
		final long elapsedDays = finish / dayInMillis;
			finish = finish % dayInMillis;
		final long elapsedHours = finish / hourInMillis;
			finish = finish % hourInMillis;
		final  long elapsedMinutes = finish / minuteInMillis;
			finish = finish % minuteInMillis;
		final long elapsedSeconds = finish / secondInMillis;
		
		String elapsed = elapsedDays+" Days "+elapsedHours+" Hours " +elapsedMinutes+" Minutes "+elapsedSeconds+" Seconds";
		System.out.println("\nFinish!! elapsed: "+elapsed);
		logger.info("Finish!! elapsed:"+elapsed);
		
	}

	//constructor
	private ParserExecutor(String[] args){
		entry_date = new Timestamp(new Date().getTime());
		
		//default config, this config are used by parser if args[0] not specified
		String configFileLoc = "01_config/Parser.cfg";
	    
	    //check Config are exist
	    File configFile = new File(configFileLoc);
		if(!configFile.exists() || !configFile.isFile()) {
			configFileLoc = "../01_config/Parser.cfg";
			
			if(args.length>0)
				configFileLoc=args[0];
			
			configFile = new File(configFileLoc);
			
			if(!configFile.exists() || !configFile.isFile()){
				System.err.println("Configuration "+configFileLoc+" file Not Found");
				System.exit(1);
			}
		}
		
		try {
		    prop = new Properties();
	    	prop.load(new FileInputStream(configFileLoc)); 	
	    	
	    	//init parserProp
		    this.parserProp =  new ParserPropReader(prop);
		    
		    try {
				l4jPr.load(new FileInputStream(parserProp.getPARSER_LOG4J_CONFIG()));
				} catch (FileNotFoundException e1) {
					System.err.println("log4j Configurator "+parserProp.getPARSER_LOG4J_CONFIG()+" not found");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			PropertyConfigurator.configure(l4jPr);
		    
			this.maxThread = parserProp.isGENERATE_SCHEMA_MODE() ? 1 : parserProp.getMAX_THREAD();

			//check Parser AppContext
		    configFile = new File(parserProp.getPARSER_APP_CONTEXT());
		    if(!configFile.exists() || !configFile.isFile()){
				System.err.println("Configuration "+parserProp.getPARSER_APP_CONTEXT()+" file Not Found");
				logger.error("Configuration "+parserProp.getPARSER_APP_CONTEXT()+" file Not Found");
				System.exit(1);
			}
		    
	    }catch (IOException e) { System.out.println("No "+configFileLoc+" file?\n"); System.exit(0);}
	    
	}
	
	private void Execute(String exParam, int source){
		this.parserProp =  new ParserPropReader(prop,source);
		final ThreadTracker threadTracker = new ThreadTracker();
		
			 if(this.parserProp.isValid()){
				ParserManager parserManager = new ParserManager();
				ParserExecutor.poolRef = Executors.newFixedThreadPool(maxThread);
				
				//Reconfigure log4J
				System.setProperty("SOURCEID", parserProp.getSOURCE_ID());
				PropertyConfigurator.configure(l4jPr);
				
				 //init DataSource
				System.setProperty("JDBC_OUTPUTMETHOD", parserProp.getOUTPUT_CONFIG());
				context = new FileSystemXmlApplicationContext (parserProp.getPARSER_APP_CONTEXT());
				DataSource ds=null;
				DBFileListWriter dbWriter=null;
				
				//Output Method
				OutputMethodPropReader om = new OutputMethodPropReader(parserProp.getOUTPUT_CONFIG());
				
				//init Parser
				Object[] paramConstructor = new Object[]{parserProp, om};
				AbstractInitiator parserInit=null;
				Constructor<?> c =parserManager.getParserInitContructor(parserProp, paramConstructor);
				try {
					parserInit = (AbstractInitiator)c.newInstance(paramConstructor);
				} catch (InstantiationException e) {
					logger.error("InstantiationException :"+e);
					System.out.println(e.getMessage());
				} catch (IllegalAccessException e) {
					logger.error("IllegalAccessException :"+e);
					System.out.println(e.getMessage());
				} catch (IllegalArgumentException e) {
					logger.error("IllegalArgumentException "+e);
					System.out.println(e.getMessage());
				} catch (InvocationTargetException e) {
					logger.error("InvocationTargetException :"+e);
					System.out.println(e.getMessage());
				}

				//init Meta Db Writer..
				if(!parserProp.isGENERATE_SCHEMA_MODE() || parserProp.isREWRITE_METADATA_FL_MODE()){
					ds = (DataSource)context.getBean("dataSource");
				    dbWriter = new DBFileListWriter(ds);
					MetadataOperation to = new MetadataOperation(parserProp, om, ds);
					DBTableModel dbTableModel = to.GetDBTableModel();
					parserInit.SetMappingOutput(dbTableModel);
					parserInit.SetDBFileListWriter(dbWriter);
					
					//TODO: disable read mapping in every initiator and check
					parserInit.setDs(ds);
					parserInit.setMappingModel(parserInit.ReadMappingModel());

					//TODO: check
//					tempGlobbalBuffer=parserInit.getMappingModel();
//					parserInit.setMappingModel(tempGlobbalBuffer);
					
					if(parserProp.isREWRITE_METADATA_FL_MODE() && source==1){
						to.WriteMetaDataToFile(dbTableModel);
						System.exit(1);
					}
				}
				
				//check the dir..
				 File fl = new File(parserProp.getLOCAL_DIR());
				 if (fl.exists() && fl.isDirectory()){
					 // generate Schema Mode
					 ParserThread thread=null;
					 if(parserProp.isGENERATE_SCHEMA_MODE()){
						 System.out.println("Generating Schema..");
						 logger.info("Generating Schema..");
						 thread = new ParserThread(poolRef, parserManager, parserProp, parserInit, fl, entry_date, null, om, threadTracker);
						 poolRef.execute(thread);
					 } else {
					 //parse Mode
						 File[] listFile =fl.listFiles();
						 for(File file:listFile){
							 String rgx=parserProp.getFILE_PATTERN().trim().replace("($DATE)", "");
							 if(file.getName().matches(rgx) && file.getName().matches(exParam)){
								 
								 boolean isAlreadyProcessed=parserProp.isCHECK_ALREADY_PROC() && dbWriter.isFileAlreadyProcessed(file.getName(), parserProp.getSOURCE_ID()); 
								 if(!parserProp.isCHECK_ALREADY_PROC() || !isAlreadyProcessed){
									 thread = new ParserThread(poolRef, parserManager, parserProp, parserInit,  file, entry_date ,ds, om, threadTracker);
									 poolRef.execute(thread);
								 } else if(isAlreadyProcessed){
									 logger.error("File "+file.getAbsolutePath()+" already processed!!");
									 System.out.println("File "+file.getAbsolutePath()+" already processed!!");
									 //backupping..
									 if(parserProp.getBACKUP_MECHANISM().equals(parserProp.BKP_RM_FILE)){
										if(!file.delete()){
											logger.error("Error deleting "+file.getName());
											System.err.println("Error deleting "+file.getName());
										}
									}else
										if(parserProp.getBACKUP_MECHANISM().equals(parserProp.BKP_MV_FILE)){
											if(!file.renameTo(new File(parserProp.getBACKUP_DIR()+"/"+file.getName()))){
												System.err.println("Error moving "+file.getName());
												logger.error("Error moving "+file.getName());
											}
										}
								 }
							 }
						 }
						
					 }
					 
					 //finish..
					 try {
						poolRef.shutdown();
						//make sure there is no thread submitted
						boolean isFinish = poolRef.awaitTermination(10, TimeUnit.MINUTES);
						if(isFinish){
							//do load buffer if any
								if(!parserProp.isGENERATE_SCHEMA_MODE() && thread!=null && thread.getParser()!=null){
									try {
										System.out.println("Loading buffer....");
										logger.info("Loading buffer....");
										Context ctx = thread.getCtx();
										LoaderHandlerManager loader = thread.getLoader();
										thread.getParser().LoadBuffer(loader, ctx);
										parserInit.mapBuffer.clear();
									} catch (Exception e) {
										e.printStackTrace();
										logger.error(e);
									}
								}
								
							//loader onFinish
							if(!parserProp.isGENERATE_SCHEMA_MODE()&&thread!=null && thread.getLoader()!=null)
								thread.getLoader().onParserEnded(thread.getCtx());
								
							System.out.println(((ThreadPoolExecutor)poolRef).getCompletedTaskCount()+" files has been processed!!");
							logger.info(((ThreadPoolExecutor)poolRef).getCompletedTaskCount()+" files has been processed!!");
							
						 }
					   } catch (InterruptedException e) {
							e.printStackTrace();
							logger.error(e);
					   }
					
				 }else
					 System.err.println(fl.getAbsolutePath()+" is Not Directory!!");
			 }
			 //else
				// System.out.println("Prop InValid!!");
		
	}
	
}