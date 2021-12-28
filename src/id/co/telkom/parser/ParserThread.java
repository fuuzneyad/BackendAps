package id.co.telkom.parser;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;


import javax.sql.DataSource;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.RowIncrementor;
import id.co.telkom.parser.common.util.ThreadTracker;

public class ParserThread extends Thread {

	private File file;
	private ParserPropReader cynapseProp;
	private ParserManager cynapseManager;
	private LoaderHandlerManager loader;
	private Context ctx;
	private AbstractInitiator cynapseInit;
	private static final Logger logger = Logger.getLogger(ParserThread.class);
	private ExecutorService poolRef;
	private AbstractParser parser;
	private final ThreadTracker threadTracker;
	
	public ParserThread (
			ExecutorService poolRef,
			ParserManager cynapseManager,
			ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit,
			File file, 
			Timestamp tanggal,
			DataSource ds,
			OutputMethodPropReader om,
			ThreadTracker threadTracker
			){
		this.poolRef=poolRef;
		this.cynapseManager=cynapseManager;
		this.cynapseProp=cynapseProp;
		this.cynapseInit=cynapseInit;
		this.file=file;
		this.ctx=new Context();
			ctx.setDate(tanggal);
			ctx.setSource(cynapseProp.getSOURCE_ID());
		this.loader=new LoaderHandlerManager(om, ds, cynapseProp, cynapseInit);
		this.threadTracker=threadTracker;
	}
	
	
	@Override
	public void run() {
		threadTracker.incrementCounter();
		Object[] paramConstructor = new Object[]{cynapseProp, cynapseInit};
		Constructor<?> c =cynapseManager.getParserContructor(cynapseProp, paramConstructor);
		try {
			parser = (AbstractParser)c.newInstance(paramConstructor);
			if(this.cynapseProp.isGENERATE_SCHEMA_MODE()){
				for(File fl:file.listFiles()){
					String rgx=cynapseProp.getFILE_PATTERN().trim();
					if(fl.getName().matches(rgx)){
						try {
							System.out.println("Processing "+((ThreadPoolExecutor)poolRef).getActiveCount()+" file(s) "+fl.getName());
							logger.info("Processing "+((ThreadPoolExecutor)poolRef).getActiveCount()+" file(s) "+fl.getName());
							parser.ProcessFile(fl, loader, ctx);
						} catch (Exception e) {
							logger.error(e);
							e.printStackTrace();
						} 
					}
				}
			parser.CreateSchemaFromMap();
			} else {
				try {
					System.out.println("Processing "+((ThreadPoolExecutor)poolRef).getActiveCount()+" file(s) "+file.getName());
					logger.info("Processing "+((ThreadPoolExecutor)poolRef).getActiveCount()+" file(s) "+file.getName());
					parser.ProcessFile(this.file, loader, ctx);
					if(cynapseProp.isCHECK_ALREADY_PROC()){
						RowIncrementor ri = loader.getRowIncrementor();
						Timestamp endTime = new Timestamp(new Date().getTime());
						cynapseInit.dbWriter.writeFileLog(this.file, 
														 cynapseProp.getSOURCE_ID(), 
														 ctx.date, 
														 "-", 
														 "SUCCESS",
														 ri.getRow(),
														 endTime);
					}
				} catch (Exception e) {
					System.out.println("error parsing "+this.file.getName()+" "+e.getMessage());
					logger.error(e);
					if(cynapseProp.isCHECK_ALREADY_PROC())
						cynapseInit.dbWriter.writeFileLog(this.file, cynapseProp.getSOURCE_ID(), ctx.date, e.getMessage(), "ERROR");
					e.printStackTrace();
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			//backupping after files has been processed..
			 if(cynapseProp.getBACKUP_MECHANISM().equals(cynapseProp.BKP_RM_FILE)){
				if(!file.delete()){
					logger.error("Error deleting "+file.getName());
					System.err.println("Error deleting "+file.getName());
				}
			}else
				if(cynapseProp.getBACKUP_MECHANISM().equals(cynapseProp.BKP_MV_FILE)){
					if(!file.renameTo(new File(cynapseProp.getBACKUP_DIR()+"/"+file.getName()))){
						System.err.println("Error moving "+file.getName());
						logger.error("Error moving "+file.getName());
					}
				}
			 threadTracker.decrementCounter();
		}
	}

	public AbstractParser getParser() {
		return parser;
	}

	public Context getCtx() {
		return ctx;
	}
	
	public LoaderHandlerManager getLoader(){
		return loader;
	}
}
