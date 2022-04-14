package unibo.actor22comm;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.IApplMsgHandler;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;

public abstract class ApplMsgHandler  implements IApplMsgHandler {  
protected String name;
   
 	public ApplMsgHandler( String name  ) {  
		this.name = name;
	}
 	
 	public String getName() {
		return name;
	}	 
   	
  	public void sendMsgToClient( String message, Interaction2021 conn  ) {
 		try {
 			//ColorsOut.out(name + " | ApplMsgHandler sendMsgToClient message=" + message + " conn=" + conn, ColorsOut.BLUE);
			conn.forward( message );
		} catch (Exception e) {
 			ColorsOut.outerr(name + " | ApplMsgHandler sendMsgToClient ERROR " + e.getMessage());;
		}
 	} 
   	
 	@Override
 	public void sendAnswerToClient( String reply, Interaction2021 conn   ) {
 		try {
			//ColorsOut.outappl(name + " | ApplMsgHandler sendAnswerToClient reply " + reply, ColorsOut.YELLOW_BACKGROUND );
			conn.reply(reply);
		} catch (Exception e) {
			ColorsOut.outerr(name + " | ApplMsgHandler sendAnswerToClient ERROR " + e.getMessage() );
		}
 	}

 	
 	public abstract void elaborate(IApplMessage message, Interaction2021 conn) ;
 	
}
