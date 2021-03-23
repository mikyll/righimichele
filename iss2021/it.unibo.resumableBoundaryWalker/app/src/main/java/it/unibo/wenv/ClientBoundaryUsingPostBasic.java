/*
===============================================================
ClientBoundaryUsingPostBasic.java
USES IssProtoclConfig.txt and IssRobotConfig.txt
USES the cril robot move language

Synchronous REQUEST-RESPONSE used in a doBoundary operation
that implements the business logic in a functional style
based on recursive functions.

The computation works in 1 thread
===============================================================
*/
package it.unibo.wenv;
import it.unibo.annotations.IssProtocolSpec;
import it.unibo.interaction.IssOperations;
import it.unibo.interaction.MsgRobotUtil;
import it.unibo.supports.IssCommsSupportFactory;

@IssProtocolSpec( configFile ="HttpBasicConfig.txt" )
public class ClientBoundaryUsingPostBasic {
	private IssOperations rs;	//robot support

	public ClientBoundaryUsingPostBasic( ){   
 		rs    = new IssCommsSupportFactory().create( this  );
	}

	public String doBoundary( ) {
		return doBoundary( 1,"");
	}
	protected String doBoundary(int stepNum, String journey) {
		if (stepNum > 4) {
			return journey;
		}
		String answer = rs.requestSynch( MsgRobotUtil.forwardMsg );
		while( answer.equals("true") ){
			journey = journey + "w";
			answer = rs.requestSynch( MsgRobotUtil.forwardMsg );
		}
		//collision
		rs.requestSynch(MsgRobotUtil.turnLeftMsg);
		return doBoundary(stepNum + 1, journey + "l");
	}
/*
MAIN
 */
	public static void main(String[] args)   {
		System.out.println("ClientBoundaryUsingPostBasic | main start n_Threads=" + Thread.activeCount());
		ClientBoundaryUsingPostBasic appl = new ClientBoundaryUsingPostBasic();
		System.out.println("ClientBoundaryUsingPostBasic | appl  n_Threads=" + Thread.activeCount());
		String trip = appl.doBoundary( );
		System.out.println("trip="+trip);
	}
	
 }

