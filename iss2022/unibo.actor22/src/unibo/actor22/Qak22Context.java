package unibo.actor22;

import java.util.HashMap;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.annotations.AnnotUtil;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.events.EventMsgHandler;
import unibo.actor22comm.proxy.ProxyAsClient;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

 

public class Qak22Context {
	private static HashMap<String,QakActor22> ctxMap      = new HashMap<String,QakActor22>();
    private static HashMap<String,ProxyAsClient> proxyMap = new HashMap<String,ProxyAsClient>();

    public static final String actorReplyPrefix = "arp_";
    
    public static void setActorAsRemote(
    		String actorName, String entry, String host, ProtocolType protocol ) {
    		if( ! proxyMap.containsKey(actorName+"Pxy")) { //un solo proxy per contesto remoto
	    		ProxyAsClient pxy = new ProxyAsClient(actorName+"Pxy", host, entry, protocol);
	    		proxyMap.put(actorName, pxy);
    		}
    }
    

	public static void addActor(QakActor22 a) {	
        ctxMap.put(a.getName(), a);
    }
	public static void removeActor(QakActor22 a) {	
        ctxMap.remove( a.getName() );
    }	
    public static QakActor22 getActor(String actorName) {
        return ctxMap.get(actorName);
    }
    
//proxy
 
	public static ProxyAsClient getProxy(String actorName) {
        return proxyMap.get(actorName);
    }

	
	
//Annotations
	
    public static void handleLocalActorDecl(Object element) {
    	AnnotUtil.createActorLocal(element);
    }
    public static void handleRemoteActorDecl(Object element) {
     	AnnotUtil.createProxyForRemoteActors(element);
    }

    public static void handleActorDeclaration(Object element) {
    	AnnotUtil.createActorLocal(element);
    	AnnotUtil.createProxyForRemoteActors(element);
    }
   
//Events
    
    public static final String registerForEvent   = "registerForEvent";
    public static final String unregisterForEvent = "unregisterForEvent";
    
	
	public static void registerAsEventObserver(String observer, String evId) {
		QakActor22 a = getActor(EventMsgHandler.myName);
		if( a == null ) new EventMsgHandler();
		IApplMessage m =
				CommUtils.buildDispatch(observer, registerForEvent, evId, EventMsgHandler.myName);
		Qak22Util.sendAMsg( m, EventMsgHandler.myName );  //Redirection to store
	}
	
	public static void unregisterAsEventObserver(String observer, String evId) {
		IApplMessage m =
				CommUtils.buildDispatch(observer, unregisterForEvent, evId, EventMsgHandler.myName);
		Qak22Util.sendAMsg( m, EventMsgHandler.myName );  //Redirection to store
	}
	

    
}
