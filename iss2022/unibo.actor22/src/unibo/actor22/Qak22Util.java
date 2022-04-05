package unibo.actor22;

 
import it.unibo.kactor.*;
import unibo.actor22.annotations.AnnotUtil;
import unibo.actor22comm.proxy.ProxyAsClient;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

import java.util.HashMap;

public  class Qak22Util   {


    public static void showActors22(){
    	Qak22Util.showActors22();
    }
      
    //Usabile da Java: Distingue tra locale e remoto
    public static void sendAMsg( IApplMessage msg ){
    	sendAMsg( msg, msg.msgReceiver() );
    }
   
    public static void sendAMsg(IApplMessage msg, String destActorName){
		//ColorsOut.out("Qak22Util | sendAMsg " + msg  , ColorsOut.GREEN);	  
        QakActor22 dest = Qak22Context.getActor(destActorName);  
        if( dest != null ) { //attore locale
    		ColorsOut.out("Qak22Util | sendAMsg " + msg + " to:" + dest.getName() , ColorsOut.GREEN);
    		dest.queueMsg(msg);
        }else{ //invio di un msg ad un attore non locale : cerco in proxyMap
        	//ColorsOut.out("Qak22Util | send to a non local actor  " + msg, ColorsOut.GREEN  );
    		//CommUtils.aboutThreads("Qak22Util Before doRequest - ");
    		ProxyAsClient pxy    = Qak22Context.getProxy(destActorName);
    		//ColorsOut.out("Qak22Util | sendAMsg " + msg + " using:" + pxy , ColorsOut.GREEN);
    		if( pxy == null ) {
    			ColorsOut.outerr("Perhaps no setActorAsRemote for " + destActorName );
    			return;
    		}
//     		new Thread() {
//    			public void run() {
    		 		//ColorsOut.outappl( "Qak22Util  | doRequest " + msg + " pxy=" + pxy, ColorsOut.WHITE_BACKGROUND  );
    				pxy.sendMsgOnConnection( msg.toString()) ;
    				//NON Attende la risposta  
//    				IApplMessage reply= new ApplMessage( answerMsg );
//    				//ColorsOut.outappl("Qak22Util | answer=" + reply  , ColorsOut.YELLOW_BACKGROUND);
//    				QakActor22 sender = Qak22Context.getActor(msg.msgSender());
//    				if( sender != null )
//    					sender.queueMsg(reply); //the sender must handle the reply as msg				 
//    				else ColorsOut.outerr("Qak22Util | answer " + answerMsg + " for an unknown actor " + msg.msgSender());
//    			}			
//    		}.start();
			//CommUtils.delay(10);  //Per forzare il rescheduling
     		CommUtils.aboutThreads("Qak22Util After doRequest  - ");
        }
	}

 
    
 }
