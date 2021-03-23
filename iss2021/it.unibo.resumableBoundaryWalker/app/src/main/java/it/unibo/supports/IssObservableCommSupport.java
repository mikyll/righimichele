/**
 IssWsSupport.java
 ===============================================================
     See also AnswerAvailable
 ===============================================================
 */
package it.unibo.supports;

import it.unibo.interaction.IssObserver;
import org.json.JSONObject;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Vector;

/**
 IssObservableCommSupport.java
 ===============================================================

 ===============================================================
 */

public abstract class IssObservableCommSupport implements IssCommSupport {
    protected Vector<IssObserver> observers = new Vector<IssObserver>();

    protected void updateObservers(JSONObject jsonOnj ){
        //System.out.println("IssObservableCommSupport | updateObservers " + observers.size() );
        observers.forEach( v -> {
            //System.out.println("IssObservableCommSupport | updates " + v );
            v.handleInfo(jsonOnj);  //The observer should be a process ...
        } );
    }

//------------------------------ IssCommSupport ----------------------------------
    @Override
    public void registerObserver( IssObserver obs ){
        observers.add( obs );
    }

    @Override
    public void removeObserver( IssObserver obs ){
        observers.remove( obs );
    }

    @Override
    public abstract void close();
}
