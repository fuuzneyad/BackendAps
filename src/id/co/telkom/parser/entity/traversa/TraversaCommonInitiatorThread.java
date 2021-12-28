package id.co.telkom.parser.entity.traversa;

public class TraversaCommonInitiatorThread extends Thread {
	private TraversaInitiator init;
	
	public TraversaCommonInitiatorThread(TraversaInitiator init){
		this.init=init;
	}
	
	@Override
	public void run() {
		init.ReadRaw();
	}
	
}
