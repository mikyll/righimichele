package unibo.actor22comm.events;

import java.util.HashMap;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.*;
import unibo.actor22comm.utils.ColorsOut;

/*
 * Gestisce gli eventi generati da emit locali o remote
 * Viene creato dalla prima chiamata a  Qak22Context.registerAsEventObserver
 */
public class EventMsgHandler extends QakActor22{
public static final String myName = "eventhandler";

protected HashMap<String,String> eventObserverMap = new HashMap<String,String>();  

 	
	public EventMsgHandler( ) {
		super(myName);
 	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		//ColorsOut.outappl(myName + " handles:" + msg + " ", ColorsOut.MAGENTA);
		if( msg.isDispatch() && msg.msgId().equals(Qak22Context.registerForEvent)) {
			ColorsOut.outappl(myName + " register:" + msg.msgSender() + " for "+ msg.msgContent(), ColorsOut.MAGENTA);
			eventObserverMap.put(msg.msgSender(), msg.msgContent());
		}else if( msg.isDispatch() && msg.msgId().equals(Qak22Context.unregisterForEvent)) {
			ColorsOut.outappl(myName + " unregister:" + msg.msgSender() + " for "+ msg.msgContent(), ColorsOut.MAGENTA);
			eventObserverMap.remove(msg.msgSender(), msg.msgContent());
		}else if( msg.isEvent()) {
 			updateTheObservers( msg );
		}else {
			ColorsOut.outerr(myName + " msg unknown");
		}
	}

	protected void updateTheObservers(IApplMessage msg) {
		eventObserverMap.forEach(
				( String actorName,  String evName) -> {
					ColorsOut.outappl("updateTheObservers:" + actorName + " evName" + evName, ColorsOut.MAGENTA); 
					if( evName.equals(msg.msgId()) ) {
						Qak22Util.sendAMsg( msg,  actorName);
					}
		} ) ;
	}
}
