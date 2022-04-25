package unibo.wenvUsage22.actors.basic;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor;
import unibo.actor22.annotations.AnnotUtil;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;


//@Actor(name = MainActorUsingWEnv.myName, implement = ActorUsingWEnv.class)
@Actor(name = MainActorUsingWEnv.myName, implement = ActorUsingWEnvBetter.class)
public class MainActorUsingWEnv {
	public static final String myName = "wenvUse";
	public void doJob() {
		AnnotUtil.handleRepeatableActorDeclaration(this);
//		new ActorUsingWEnv(myName); //if no annotation
		CommSystemConfig.tracing = false;
		Qak22Context.showActorNames();
  		Qak22Util.sendAMsg( ApplData.startSysCmd("main",myName) );
	};

	public void terminate() {
		CommUtils.aboutThreads("Before end - ");		
		CommUtils.delay(12000); //Give time to work ...
		CommUtils.aboutThreads("At exit - ");		
		System.exit(0);
	}
	
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainActorUsingWEnv appl = new MainActorUsingWEnv( );
		appl.doJob();
		appl.terminate();
	}

}
