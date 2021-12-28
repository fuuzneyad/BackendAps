package id.co.telkom.grabber;

import id.co.telkom.grabber.ftp.Ericsson3GGetFilesThread;
import id.co.telkom.grabber.ftp.GetFilesThread;

public class GrabberController {
	
	public static void handle(){
			Thread[] t1 ={ 
			new GetFilesThread(
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null)
			,new Ericsson3GGetFilesThread(null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null)
			}
					;	
			
			Thread.enumerate(t1);
	}
}
