package unibo.actor22comm.tcp;

 
import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.IApplMsgHandler;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
 

/*
 * Ente attivo per la ricezione di messaggi su una connessione Interaction2021
 */
public class TcpApplMessageHandler extends Thread{
private  IApplMsgHandler handler ;
private Interaction2021 conn;

public TcpApplMessageHandler(  IApplMsgHandler handler, Interaction2021 conn ) {
		this.handler = handler;
		this.conn    = conn;
 		this.start();
	}
 	
	@Override 
	public void run() {
		String name = handler.getName();
		try {
			ColorsOut.out( "TcpApplMessageHandler | STARTS with handler=" + name + " conn=" + conn, ColorsOut.BLUE );
			while( true ) {
				//ColorsOut.out(name + " | waits for message  ...");
			    String msg = conn.receiveMsg();
			    //ColorsOut.outappl(name + "  | TcpApplMessageHandler received:" + msg, ColorsOut.MAGENTA );
			    if( msg == null ) {
			    	conn.close();
			    	break;
			    } else{ 
			    	IApplMessage m = new ApplMessage(msg);
			    	handler.elaborate( m, conn ); //chiama  ctxH
			    }
			}
			ColorsOut.out("TcpApplMessageHandler  |  BYE", ColorsOut.BLUE   );
		}catch( Exception e) {
			ColorsOut.outerr( "TcpApplMessageHandler | ERROR:" + e.getMessage()  );
		}	
	}
}
