package unibo.actor22.distrib;

import it.unibo.kactor.IApplMessage;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.common.ApplData;
import unibo.actor22.common.ControllerActor;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
 
 
 
/*
 * Sistema che usa led e controller come attori locali
 */
 
public class ControllerOnPcUsingLedRemote {
   
	public void doJob() {
		ColorsOut.outappl("ControllerOnPcUsingLedRemote | Start", ColorsOut.BLUE);
		configure();
		CommUtils.aboutThreads("Before execute - ");
		//BasicUtils.waitTheUser();
		execute();
		terminate();
	}
	
	protected void configure() {
		DomainSystemConfig.simulation   = true;			
		DomainSystemConfig.ledGui       = true;			
		DomainSystemConfig.tracing      = false;					
		CommSystemConfig.tracing        = false;
		
 		String raspHostAddr             = "localhost";
                 
		Qak22Context.setActorAsRemote( 
				ApplData.ledName, ""+ApplData.ctxPort, raspHostAddr, ApplData.protocol);

		new ControllerActor ( ApplData.controllerName );
  	}
	
	protected void execute() {
		ColorsOut.outappl("ControllerOnPcUsingLedRemote | execute", ColorsOut.MAGENTA);
  		Qak22Util.sendAMsg( ApplData.activateCrtl );
  		//askLedStateForTesting();
	} 
	
	//IL main può fare richieste, ma non è in grado di ricevere risposte
	//in quanto le richieste adesso sono ASINCRONE
	protected void askLedStateForTesting() {
		IApplMessage getStateRequest  = ApplData.buildRequest("main","ask", ApplData.reqLedState, ApplData.ledName);
		for( int i=1; i<=3; i++) {
	 		ColorsOut.outappl( "ControllerOnPcUsingLedRemote  | doRequest "  , ColorsOut.YELLOW_BACKGROUND  );
			Qak22Util.sendAMsg( getStateRequest );
  		}		
	}

	public void terminate() {
  	    CommUtils.delay(5000);	//TODO terminate when the controller has finished
		CommUtils.aboutThreads("Before exit - ");
		System.exit(0);
	}
	
	public static void main( String[] args) {
		BasicUtils.aboutThreads("Before start - ");
		new ControllerOnPcUsingLedRemote().doJob();
 		BasicUtils.aboutThreads("Before end - ");
	}

}

/*
 * Threads:
 */
