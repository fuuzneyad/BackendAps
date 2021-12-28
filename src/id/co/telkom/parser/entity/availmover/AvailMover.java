package id.co.telkom.parser.entity.availmover;

import java.io.File;
import java.util.Map;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class AvailMover extends AbstractParser{
	private Map<String, AvailMoverModel> modelMap;
	private ParserPropReader cynapseProp;
	
	@SuppressWarnings("unchecked")
	public AvailMover(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, AvailMoverModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()&& cynapseProp.getMAX_THREAD()==1){
			try {
				for(Map.Entry<String, AvailMoverModel> mp:modelMap.entrySet()){
		            KettleEnvironment.init();
		            EnvUtil.environmentInit();
		            TransMeta transMeta = new TransMeta(mp.getValue().getKtrFile());
		            Trans trans = new Trans(transMeta);
			            trans.setParameterValue("INPUT_DB", mp.getValue().getShema());
			            trans.setParameterValue("INPUT_HOST", mp.getValue().getIp());
			            trans.setParameterValue("INPUT_PASSWORD", mp.getValue().getPassword());
			            trans.setParameterValue("INPUT_PORT", mp.getValue().getPort());
			            trans.setParameterValue("INPUT_USERNAME", mp.getValue().getUsername());
			            
//			            System.out.println("INPUT_HOST.. "+trans.getParameterValue("INPUT_HOST"));
			            System.out.println("INPUT_DB.. "+trans.getParameterValue("INPUT_DB"));
//			            System.out.println("INPUT_PASSWORD.. "+trans.getParameterValue("INPUT_PASSWORD"));
//			            System.out.println("INPUT_PORT.. "+trans.getParameterValue("INPUT_PORT"));
//			            System.out.println("INPUT_USERNAME.. "+trans.getParameterValue("INPUT_USERNAME"));
			            
			            trans.execute(null);
			            trans.waitUntilFinished();
				}
			}
			catch ( KettleException e ) {
			    System.out.println("e1:"+e);
			}
			catch (Exception e){
				System.out.println("e1:"+e);
			}
		}else
			if(cynapseProp.getMAX_THREAD()!=1)
				System.out.println("Max Thread must be set 1!!");
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		System.err.println("Generate Schema not Suppoted!!");
	}

}
