/*
ClientUsingHttp.java
*/
package unibo.wenvUsage22.wshttp;
import org.json.JSONObject;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.http.*;
import java.util.Observable;
import unibo.wenvUsage22.common.ApplData;

public class ClientUsingHttp { //implements IObserver
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String HttpURL          = "http://"+localHostName+":"+port+"/api/move";
 

	private Interaction2021 conn;
	
	protected void doBasicMoves() throws Exception {
  		conn = HttpConnection.create("localhost:8090" ); //INTERROMPIBILE usando WebGui
  		String answer = "";
 		answer = conn.request( ApplData.turnLeft(300) );
		ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
		answer = conn.request( ApplData.turnRight(300) );
		ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
 		answer = conn.request( ApplData.moveForward(1000) ); 
 		//risposta dopo duration a meno di interruzioni DA ALTRA FONTE
		ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
 	}
 
 /*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new ClientUsingHttp().doBasicMoves();
		CommUtils.aboutThreads("At end - ");
	}
	
	
 }
