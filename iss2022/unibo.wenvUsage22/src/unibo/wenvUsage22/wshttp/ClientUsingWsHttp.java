/*
ClientUsingPost.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package unibo.wenvUsage22.wshttp;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.ws.*;
import unibo.wenvUsage22.common.ApplData;
import unibo.actor22comm.http.*;

import java.net.URI;
import java.util.Observable;

public class ClientUsingWsHttp implements IObserver{
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String HttpURL          = "http://"+localHostName+":"+port+"/api/move";
 

	private Interaction2021 connWs, connHttp;
	
//	protected String crilCmd(String move, int time){
//		String crilCmd  = "{\"robotmove\":\"" + move + "\" , \"time\": " + time + "}";
//		//ColorsOut.out( "ClientNaiveUsingPost |  buildCrilCmd:" + crilCmd );
//		return crilCmd;
//	}
//	public String moveForward(int duration)  { return crilCmd("moveForward", duration) ; }
//	public String moveBackward(int duration) { return crilCmd("moveBackward", duration); }
//	public String turnLeft(int duration)     { return crilCmd("turnLeft", duration);     }
//	public String turnRight(int duration)    { return crilCmd("turnRight", duration);    }
//	public String stop(int duration)         { return crilCmd("alarm", duration);        }
//	public String stop( )                    { return crilCmd("alarm", 10);        }

	protected void doBasicMoves() throws Exception {
		connWs   = WsConnection.create("localhost:8091" );
		connHttp = HttpConnection.create("localhost:8090" );
		((WsConnection)connWs).addObserver(this);
		String answer = connHttp.request( ApplData.moveForward(2000) );
		ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );

	}
 
	protected void doBasicMovesTest() throws Exception {
 		
			ColorsOut.out("STARTING doBasicMoves ... ");
			boolean endmove = false;
			
			connWs.forward( ApplData.turnRight(1300) );
 			CommUtils.delay(500);
			connWs.forward( ApplData.stop() );
			connWs.forward( ApplData.turnLeft(300) );
 			CommUtils.delay(200);
			connWs.forward( ApplData.stop() );
			//CommUtils.delay(500);
			connWs.forward( ApplData.moveForward(500) );
			
//			endmove = requestSynch( turnLeft(300) );
//			ColorsOut.out("turnLeft endmove=" + endmove);
//			endmove = requestSynch( turnRight(300) );
//			ColorsOut.out("turnRight endmove=" + endmove);
//
//			//Now the value of endmove depends on the position of the robot
//			endmove = requestSynch( moveForward(1800) );
//			ColorsOut.out("moveForward endmove=" + endmove);
//			endmove = requestSynch( moveBackward(800) );
			CommUtils.delay(1500);
			connWs.close();
 
	}
/*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new ClientUsingWsHttp().doBasicMoves();
		CommUtils.aboutThreads("At end - ");
	}
	
	@Override
	public void update(Observable source, Object data) {
		ColorsOut.outappl("ClientUsingWsHttp update/2 receives:" + data, ColorsOut.MAGENTA);
//		JSONObject d = new JSONObject(""+data);
//		ColorsOut.outappl("ClientUsingWsHttp update/2 collision=" + d.has("collision"), ColorsOut.MAGENTA);
		
	}
	@Override
	public void update(String data) {
		ColorsOut.out("ClientUsingWsHttp update receives:" + data);
	}
	
 }
