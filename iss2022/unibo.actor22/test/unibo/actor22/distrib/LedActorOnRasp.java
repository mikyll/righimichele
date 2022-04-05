package unibo.actor22.distrib;

 
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.common.ApplData;
import unibo.actor22.common.LedActor;
import unibo.actor22comm.context.EnablerContextForActors;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
  

public class LedActorOnRasp {
private EnablerContextForActors ctx;

	public void doJob() {
		ColorsOut.outappl("LedActorOnRasp | Start", ColorsOut.BLUE);
		configure();
		CommUtils.aboutThreads("Before execute - ");
		//CommUtils.waitTheUser();
		execute();
		terminate();
	}
	
	protected void configure() {
		DomainSystemConfig.simulation   = true;			
		DomainSystemConfig.ledGui       = true;			
		DomainSystemConfig.tracing      = false;					
		CommSystemConfig.tracing        = false;
  
		ctx = new EnablerContextForActors( "ctx",ApplData.ctxPort,ApplData.protocol);
		new LedActor( ApplData.ledName );
 		//Registrazione dei componenti presso il contesto: NO MORE ... 
  	}
	
	protected void execute() {
		ColorsOut.outappl("LedActorOnRasp | execute", ColorsOut.MAGENTA);
		ctx.activate();
	} 

	public void terminate() {
		CommUtils.aboutThreads("Before exit - ");
// 	    CommUtils.delay(5000);
//		System.exit(0);
	}
	
	public static void main( String[] args) {
		CommUtils.aboutThreads("Before start - ");
		new LedActorOnRasp().doJob();
		CommUtils.aboutThreads("Before end - ");
	}

}

/*
 * Threads:
 */
