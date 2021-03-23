/*
===============================================================
ClientBoundaryWebsockBasicAsynch.java
Use the cril language and the support specified in the
configuration file WebsocketBasicConfig.txt

GOAL: use request instead of requestSynch
and handle the information sent by WEnv over the cmdSocket-8091
===============================================================
*/
package it.unibo.wenv;
import it.unibo.annotations.IssProtocolSpec;
import it.unibo.supports.IssCommSupport;
import it.unibo.interaction.IssObserver;
import it.unibo.supports.IssCommsSupportFactory;

@IssProtocolSpec( configFile ="WebsocketBasicConfig.txt" )
public class ClientBoundaryWebsockBasicAsynch {
    private IssCommSupport support; //The IssCommSupport is required in order to add observers and close it
    private IssObserver    controller;

    //Factory method
    public static ClientBoundaryWebsockBasicAsynch createAndRun(){
        ClientBoundaryWebsockBasicAsynch obj = new ClientBoundaryWebsockBasicAsynch();
        IssCommSupport support               = new IssCommsSupportFactory().create( obj  );
        obj.setCommSupport(support);    //inject
        obj.controller = new RobotControllerBoundary(support, false);
        support.registerObserver( obj.controller );
        //support.registerObserver( new RobotObserver() );    //ANOTHER OBSERVER
        return obj;
    }

    protected void setCommSupport(IssCommSupport support){
        this.support = support;
    }

    public static void main(String args[]){
        try {
            System.out.println("ClientBoundaryWebsockBasicAsynch | main start n_Threads=" + Thread.activeCount());
            ClientBoundaryWebsockBasicAsynch appl = ClientBoundaryWebsockBasicAsynch.createAndRun();
            RobotControllerBoundary ctrl = (RobotControllerBoundary) appl.controller;
            System.out.println("ClientBoundaryWebsockBasicSynch | appl n_Threads=" + Thread.activeCount());
            String trip = ctrl.doBoundary();       //wait until completion
            System.out.println("ClientBoundaryWebsockBasicAsynch | trip=" + trip  );
            System.out.println("ClientBoundaryWebsockBasicAsynch | main end n_Threads=" + Thread.activeCount());
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}
