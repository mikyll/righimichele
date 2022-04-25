/*
ClientUsingPost.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package unibo.wenvUsage22.wshttp;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.ws.*;
import unibo.wenvUsage22.common.ApplData;

import java.util.Observable;

public class ClientUsingWs implements IObserver{
 
	private Interaction2021 conn;
  
	protected void doBasicMoves() throws Exception {
		conn = WsConnection.create("localhost:8091" );
		((WsConnection)conn).addObserver(this);
 
 		//conn.forward( turnLeft( 800  ) );
 		conn.forward( ApplData.moveForward(2300) );
//		conn.forward( stop( ) );
////    	Thread.sleep( 500 );
//		conn.forward( turnRight( 400 ) );

//			conn.forward( turnRight(300) );
// 			CommUtils.delay(500);
//			conn.forward( stop() );
//			conn.forward( turnLeft(300) );
// 			CommUtils.delay(500);
//			conn.forward( stop() );
//			//CommUtils.delay(500);
//			conn.forward( moveForward(1000) );
 
//			CommUtils.delay(1500);
//			conn.close();
// 
	}
	
	@Override
	public void update(Observable source, Object data) {
		ColorsOut.out("ClientUsingWs update/2 receives:" + data);
//		JSONObject d = new JSONObject(""+data);
//		ColorsOut.outappl("ClientUsingWs update/2 collision=" + d.has("collision"), ColorsOut.MAGENTA);
		
	}
	@Override
	public void update(String data) {
		ColorsOut.out("ClientUsingWs update receives:" + data);
	}	
/*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new ClientUsingWs().doBasicMoves();
		CommUtils.aboutThreads("At end - ");
	}
	

	
 }
