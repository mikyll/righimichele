/*
===============================================================
RobotSupport.java
Utility class that should be used by a robot controller
(e.g. RobotControllerBoundary)

This resource implements asynch IssOperations so to avoid move not allowed
===============================================================
*/
package it.unibo.supports;
import it.unibo.interaction.IssObserver;
import it.unibo.interaction.IssOperations;

import java.io.IOException;
import java.util.Vector;

//Adapter, if we do not want to change the basic supports
public class RobotSupport implements IssCommSupport {
    private IssOperations rs;

    public  enum  robotLangs {cril, aril };
    private Vector<IssObserver> observers = new Vector<IssObserver>();

    public RobotSupport(IssOperations rs){
        this.rs = rs;
    }

    public void moveForward( int time ){

    }
    public void request(String jsonMoveStr ) {
         forward( jsonMoveStr );
    }
    public void forward(String jsonMoveStr ) {
        //System.out.println("RobotSupport | forward rs=" + rs + " move:" + jsonMoveStr);
        rs.forward(jsonMoveStr );
        //if( rs instanceof  IssWsSupport ) rs.forward(jsonMoveStr );
        //else if( rs instanceof  IssHttpSupport ) rs.request(jsonMoveStr);
    }
    public void reply( String msg ){
        rs.forward( msg );
    }
    public String requestSynch( String msg ){
        return rs.requestSynch( msg );
    }

//---------------------------------------------------------------

    @Override
    public void registerObserver( IssObserver obs ){
        observers.add( obs );
    }

    @Override
    public void removeObserver( IssObserver obs ){
        observers.remove( obs );
    }

    @Override
    public void close(){
        try { //userSession.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
