/**
 * ActorNaive.java
 ===============================================================
 ===============================================================

 */
package it.unibo.wenv;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActorNaive extends Thread{
    private String info  = null;
    private boolean goon         = true;
    private Vector<String> queue = new Vector<String>();
    private BlockingQueue<String>  bqueue = new LinkedBlockingQueue<String>(10);

    @Override
    public void run(){
        while( goon )  waitInputAndElab();
    }

    public void terminate(){
        goon = false;
        put("bye");
    }

    public  void put( String info ){
        try {
            bqueue.put(info);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected  void waitInputAndElab(){
        try {
            String info = bqueue.take();
            if(  goon ) handleInput( info );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void handleInput(String info){
        System.out.println("ActorNaive | ------------ " + info );
    }

}
