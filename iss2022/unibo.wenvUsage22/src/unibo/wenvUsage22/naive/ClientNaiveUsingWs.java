/**
 * ClientNaiveUsingWs
 ===============================================================
 * Technology-dependent application
 * TODO. eliminate the communication details from this level
 ===============================================================
 */

package unibo.wenvUsage22.naive;
import javax.websocket.*; 
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONObject;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;

@ClientEndpoint
public class ClientNaiveUsingWs {
    private Session userSession    = null;

    public ClientNaiveUsingWs(String addr) {
            ColorsOut.out("ClientNaiveUsingWs |  CREATING ..." + addr);
            init(addr);
    }

    protected void init(String addr){
        try {
            //ColorsOut.out("ClientNaiveUsingWs |  container=" + ContainerProvider.class.getName());
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("ws://"+addr));
        } catch (URISyntaxException ex) {
            System.err.println("ClientNaiveUsingWs | URISyntaxException exception: " + ex.getMessage());
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        ColorsOut.out("ClientNaiveUsingWs | opening websocket");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        ColorsOut.out("ClientNaiveUsingWs | closing websocket");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message)  {
        try {
            //{"collision":"true ","move":"..."} or {"sonarName":"sonar2","distance":19,"axis":"x"}
            ColorsOut.outappl("ClientNaiveUsingWs | onMessage:" + message, ColorsOut.GREEN);
            JSONObject jsonObj = new org.json.JSONObject(message);
            ColorsOut.out("ClientNaiveUsingWs | jsonObj:" + jsonObj);
            if (jsonObj.has("endmove")) {
                boolean endmove = jsonObj.getBoolean("endmove");
                String  move    = jsonObj.getString("move") ;
                //ColorsOut.out("ClientNaiveUsingWs | onMessage " + move + " endmove=" + endmove);
            } else if (jsonObj.has("collision")  ) {
                boolean collision = jsonObj.getBoolean("collision");
                String move = jsonObj.get("move").toString();
                //ColorsOut.out("ClientNaiveUsingWs | onMessage collision=" + collision + " move=" + move);
             } else if (jsonObj.has("sonarName")  ) {
                String sonarNAme = jsonObj.getString("sonarName") ;
                String distance = jsonObj.get("distance").toString();
                //ColorsOut.out("ClientNaiveUsingWs | onMessage sonaraAme=" + sonarNAme + " distance=" + distance);
            }
        } catch (Exception e) {
        	ColorsOut.outerr("onMessage " + message + " " +e.getMessage());
//        	try {
//				request( stop(100) );
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
        }

    }

/*
BUSINESS LOGIC
 */

    protected void request( String crilCmd ) throws Exception  {
        //ColorsOut.out("ClientNaiveUsingWs | request " + crilCmd);
        this.userSession.getAsyncRemote().sendText(crilCmd);
        //this.userSession.getBasicRemote().sendText(crilCmd);    
        //synch: blocks until the message has been transmitted
    }
    
    protected void requestSynch( String crilCmd ) throws Exception  {
        this.userSession.getBasicRemote().sendText(crilCmd);    
        JSONObject jsonObj = new JSONObject( crilCmd );
        int moveTime       = jsonObj.getInt("time");
        ColorsOut.out("ClientNaiveUsingWs | requestSynch " + crilCmd + " moveTime=" + moveTime);
        Thread.sleep( moveTime );
//      	request( stop(10) );
    }

    protected void doBasicMoves() throws Exception{
//    	request( moveForward(  1800) );
//    	Thread.sleep( 500 );
//    	request( stop( ) );
    	
//    	request( moveForward( 400  ) );
//     	request( moveBackward( 400 ) );
    	request( ApplData.turnLeft( 800  ) );
    	request( ApplData.stop( ) );
//    	Thread.sleep( 500 );
     	request( ApplData.turnRight( 400 ) );
     	
//     	requestSynch(  moveForward( 400  ) );
//     	requestSynch(  moveBackward( 400  ) );
     	
     	Thread.sleep( 2500 );  //Give time to receive msgs from WEnv
    	
    }

    protected void doBasicMovesOld() throws Exception{
//    	  request( stop(10) );
    	  requestSynch( ApplData.moveForward(  1400) );
//        requestDelayed( turnLeft(2800) );
//          request( moveForward(  1800) );
//          Thread.sleep( 500 );
//          request( stop(10) );
//          request( moveBackward(  1000) );
//          Thread.sleep( 500 );
//          request( stop(10) );
//         requestDelayed( turnRight(300) );
//         requestDelayed( moveForward(  1800) );
//         requestDelayed( moveBackward( 800) );
//        this.userSession = null;
    }

/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            ClientNaiveUsingWs appl = new ClientNaiveUsingWs("localhost:8091");
            appl.doBasicMoves();
      		CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            ColorsOut.outerr("ClientNaiveUsingWs | main ERROR: " + ex.getMessage());
        }
    }
}

/*
ClientNaiveUsingWs | main start n_Threads=1
ClientNaiveUsingWs |  CREATING ...
ClientNaiveUsingWs | opening websocket
ClientNaiveUsingWs | onMessage turnRight endmove=notallowed
ClientNaiveUsingWs | onMessage moveForward endmove=notallowed
ClientNaiveUsingWs | onMessage moveBackward endmove=notallowed
ClientNaiveUsingWs | onMessage turnLeft endmove=true
ClientNaiveUsingWs | onMessage turnLeft endmove=true
ClientNaiveUsingWs | onMessage turnRight endmove=true
ClientNaiveUsingWs | onMessage moveForward endmove=true
ClientNaiveUsingWs | appl  n_Threads=4
ClientNaiveUsingWs | onMessage moveBackward endmove=true
 */