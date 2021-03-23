/**
 AnswerAvailable
 ===============================================================
 Utility class to capture information about the reply to a request
 sent by the server over the ws connection.
 The put operation is called by onMessage
 ===============================================================
 */
package it.unibo.supports;

public class AnswerAvailable{
    private String  answer  = null;
    public synchronized void put(String info, String move) {
            answer = info;
            notify();
    }
    public synchronized String get( ) {
        while (answer == null){
            try { wait(); }
            catch (InterruptedException e) { }
            finally { }
        }
        String myAnswer = answer;
        answer           = null;
        return myAnswer;
    }
}