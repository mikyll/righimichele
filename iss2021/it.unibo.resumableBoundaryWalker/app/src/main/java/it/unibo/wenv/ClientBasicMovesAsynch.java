/*
===============================================================
ClientBasicMovesAsynch.java
Use the cril language and the support specified in the
configuration file WebsocketBasicConfig.txt

===============================================================
*/
package it.unibo.wenv;
import it.unibo.annotations.IssProtocolSpec;
import it.unibo.supports.IssCommSupport;
import it.unibo.supports.IssCommsSupportFactory;

@IssProtocolSpec( configFile ="WebsocketBasicConfig.txt" )
public class ClientBasicMovesAsynch {
    public static final String my_forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 1000}";
    public static final String my_backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 400}";
    public static final String my_turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}";
    public static final String my_turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}";
    public static final String my_haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 100}";

    private IssCommSupport rs; //The IssCommSupport is required in order to add observers and close

    //Constructor
    public ClientBasicMovesAsynch(){
        rs             = new IssCommsSupportFactory().create( this  );
        rs.registerObserver( new RobotObserver() );
    }

    protected void delay( int dt ){
        try { Thread.sleep( dt ); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void doBasicMoves(){ //show that a move can be halted
        rs.request(  my_forwardMsg );
        delay(500 );
        rs.request(  my_haltMsg );
        delay( 1000 ); //give time to handle answers sent by WEnv
    }

    public static void main(String args[]){
        System.out.println("ClientBoundaryWebsockBasicAsynch | main start n_Threads=" + Thread.activeCount());
        ClientBasicMovesAsynch appl = new ClientBasicMovesAsynch();
        System.out.println("ClientBoundaryWebsockBasicSynch | appl n_Threads=" + Thread.activeCount());
        appl.doBasicMoves();
        System.out.println("ClientBoundaryWebsockBasicAsynch | main end n_Threads=" + Thread.activeCount());
    }
}
