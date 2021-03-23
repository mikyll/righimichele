/**
 IssOperations.java
 ==========================================================================
 Defines high-level interaction operation
 These operations are introduced with reference to message-passing
 rather than procedure-call.
 Thus, forward is just 'fire and forget', while
 request assumes that the called will execute a reply related to that request

 requestSynch is introduced to help the transition to the new paradigm,

 ==========================================================================
 */
package it.unibo.interaction;

public interface IssOperations {
    void forward( String msg ) ;  //String related to cril, aril, AppMsg
    void request( String msg );
    void reply( String msg );
    String requestSynch( String msg );
}
