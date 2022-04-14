package unibo.actor22;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import it.unibo.kactor.*;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import unibo.actor22comm.events.EventMsgHandler;
import unibo.actor22comm.proxy.ProxyAsClient;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

public abstract class QakActor22 extends ActorBasic{

protected kotlin.coroutines.Continuation<? super Unit> mycompletion;

	public QakActor22(@NotNull String name ) {      
		super(name, QakContext.Companion.createScope(), false, true, false, 50);
        if( Qak22Context.getActor(name) == null ) {
        	Qak22Context.addActor( this );
        	ColorsOut.out( getName()  + " | CREATED " , ColorsOut.CYAN);
        }
        else ColorsOut.outerr("QakActor22 | WARNING: an actor with name " + name + " already exists");	
	}


    
	protected abstract void handleMsg(IApplMessage msg);
	
	@Override
	public Object actorBody(@NotNull IApplMessage msg, @NotNull Continuation<? super Unit> $completion) {
        mycompletion = $completion;
        handleMsg(msg);
        return null;
	}
	
	//Invia la richiesta di elaborazione di un messaggio all'attore
	protected void queueMsg(IApplMessage msg) {
		super.autoMsg(msg, mycompletion);
	}
	
/*
 * INVIO MESSAGGI	 
 */
 	protected void sendMsg( IApplMessage msg ){
     	String destActorName=msg.msgReceiver();
		//ColorsOut.out("Qak22Util | sendAMsg " + msg  , ColorsOut.GREEN);	  
        QakActor22 dest = Qak22Context.getActor(destActorName);  
        if( dest != null ) { //attore locale
    		ColorsOut.out("QakActor22 | sendAMsg " + msg + " to:" + dest.getName() , ColorsOut.GREEN);
    		dest.queueMsg(msg);
        }else{  
        	sendMsgToRemoteActor(msg);
         }
	}
	
	protected void sendMsgToRemoteActor( IApplMessage msg ) {
		String destActorName = msg.msgReceiver();
		//Occorre un proxy al contesto
		ProxyAsClient pxy    = Qak22Context.getProxy(destActorName);
		//ColorsOut.out("QakActor22 | sendAMsg " + msg + " using:" + pxy , ColorsOut.GREEN);
		if( pxy == null ) {
			ColorsOut.outerr("Perhaps no setActorAsRemote for " + destActorName );
			return;
		}
		pxy.sendMsgOnConnection( msg.toString() ) ;
	}
	
	
	protected void autoMsg( IApplMessage msg ){
    	//WARNING: il sender di msg potrebbe essere qualsiasi
     	if( msg.msgReceiver().equals( getName() )) sendMsg( msg );
    		//sendMessageToActor(msg,msg.msgReceiver()); 
    	else ColorsOut.outerr("QakActor22 | autoMsg wrong receiver");
    }
    
	protected  void forward( IApplMessage msg ){
		//ColorsOut.outappl( "QakActor22 forward:" + msg, ColorsOut.CYAN);
		if( msg.isDispatch() ) sendMsg( msg );  
		else ColorsOut.outerr("QakActor22 | forward requires a dispatch");
	}
 
	protected void request( IApplMessage msg ){
    	if( msg.isRequest() ) sendMsg( msg ); 
    	else ColorsOut.outerr("QakActor22 |  requires a request");
    }
 
	protected void sendReply(IApplMessage msg, IApplMessage reply) {
		QakActor22 dest = Qak22Context.getActor( msg.msgSender() );
        if(dest != null) dest.queueMsg( reply );
        else replyToRemoteCaller(msg,reply);
    }	
	
	protected void replyToRemoteCaller(IApplMessage msg, IApplMessage reply) {
    	QakActor22 ar = Qak22Context.getActor(Qak22Context.actorReplyPrefix+msg.msgSender());  
        if(ar !=null) ar.queueMsg( reply );
        else ColorsOut.outerr("QakActor22 | WARNING: reply " + msg + " IMPOSSIBLE");		
	}

//-------------------------------------------------
	//EVENTS
    protected static HashMap<String,String> eventObserverMap = new HashMap<String,String>();  
	
    protected void emit(IApplMessage msg) {
    	if( msg.isEvent() ) {
    		ColorsOut.outappl( "QakActor22 | emit=" + msg  , ColorsOut.GREEN);
    		Qak22Util.sendAMsg( msg, EventMsgHandler.myName);
    	}   	
    }
    
    protected void handleEvent(IApplMessage msg) {
		try {
		ColorsOut.outappl( "QakActor22 handleEvent:" + msg, ColorsOut.MAGENTA);
		if( msg.isDispatch() && msg.msgId().equals(Qak22Context.registerForEvent)) {
			eventObserverMap.put(msg.msgSender(), msg.msgContent());
		}else if( msg.isEvent()) {
			eventObserverMap.forEach(
					( actorName,  evName) -> {
						ColorsOut.outappl(actorName + " " + evName, ColorsOut.CYAN); 
						if( evName.equals(msg.msgId()) ) {
							sendMsg( msg  );
						}
			} ) ;
		}else {
			ColorsOut.outerr( "QakActor22 handleEvent: msg unknown");
		}
		}catch( Exception e) {
			ColorsOut.outerr( "QakActor22 handleEvent ERROR:" + e.getMessage());
		}
	}    

	
}
