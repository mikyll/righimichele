/*
===============================================================
ClientBoundaryWebsockBasicSynch.java
Use the cril language and the support specified in the
configuration file WebsocketBasicConfig.txt
===============================================================
*/
package it.unibo.wenv;
import it.unibo.annotations.IssProtocolSpec;
import it.unibo.interaction.IssOperations;
import it.unibo.interaction.MsgRobotUtil;
import it.unibo.supports.IssCommsSupportFactory;

@IssProtocolSpec( configFile ="WebsocketBasicConfig.txt" )
public class ClientBoundaryWebsockBasicSynch {
    private IssOperations support;  //The full IssCommSupport is not required

    //Factory method
    public static ClientBoundaryWebsockBasicSynch create(){
        ClientBoundaryWebsockBasicSynch obj  = new ClientBoundaryWebsockBasicSynch();
        IssOperations support                = new IssCommsSupportFactory().create( obj  );
        obj.setCommSupport(support);
        return obj;
    }

    protected void setCommSupport(IssOperations support){
        this.support = support;
    }

    public String boundary( ) {
        return doBoundary(1,"",support);
    }

    private String doBoundary(int stepNum, String journey, IssOperations rs) {
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
        return doBoundary(stepNum + 1, journey + "l", rs);
    }


    public static void main(String args[]){
        System.out.println("ClientBoundaryWebsockBasicSynch | main start n_Threads=" + Thread.activeCount());
        ClientBoundaryWebsockBasicSynch appl = ClientBoundaryWebsockBasicSynch.create();
        System.out.println("ClientBoundaryWebsockBasicSynch | appl  n_Threads=" + Thread.activeCount());
        String trip = appl.boundary();
        System.out.println("trip="+trip);
        System.out.println("ClientBoundaryWebsockBasicSynch | main end n_Threads=" + Thread.activeCount());
    }
}
