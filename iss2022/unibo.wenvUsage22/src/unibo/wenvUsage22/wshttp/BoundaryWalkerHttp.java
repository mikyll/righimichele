/*
ClientUsingHttp.java
*/
package unibo.wenvUsage22.wshttp;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.http.*;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.ApplData;

public class BoundaryWalkerHttp { 
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String HttpURL          = "http://"+localHostName+":"+port+"/api/move";
 

	private Interaction2021 conn;
	

	protected void doJob() throws Exception {
		conn = HttpConnection.create("localhost:8090" ); //INTERROMPIBILE usando WebGui
  		String answer = "";
  		boolean obstacle = false;
  		for( int i=1; i<=4; i++ ) {
	  		while( ! obstacle ) {
	  			answer = conn.request( ApplData.moveForward(500) );
				  ColorsOut.outappl(i + " answer= " + answer, ColorsOut.BLACK  );
	  			obstacle = answer.contains("collision");
	  			CommUtils.delay(500);
	  		}
	  		obstacle = false;
	  		answer = conn.request( ApplData.turnLeft(300) );
  		}
 
		ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
 	}
 
 /*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new BoundaryWalkerHttp().doJob();
		CommUtils.aboutThreads("At end - ");
	}
	
 
	
 }
