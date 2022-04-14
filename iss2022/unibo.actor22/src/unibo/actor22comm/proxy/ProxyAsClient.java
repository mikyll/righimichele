package unibo.actor22comm.proxy;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Util;
import unibo.actor22.QakActor22;
import unibo.actor22.Qak22Context;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.tcp.TcpClientSupport;
import unibo.actor22comm.udp.UdpClientSupport;

public class ProxyAsClient {
private Interaction2021 conn; 
protected String name ;		//could be a uri
protected ProtocolType protocol ;

/*
 * Realizza la connessione di tipo Interaction2021 (concetto astratto)
 * in modo diverso, a seconda del protocollo indicato (tecnologia specifica)
 */
	public ProxyAsClient( String name, String host, String entry, ProtocolType protocol ) {
		try {
			ColorsOut.out(name+"  | CREATING entry= "+entry+" protocol=" + protocol, ColorsOut.BLUE );
			this.name     = name;
			this.protocol = protocol;			 
			setConnection(host,  entry,  protocol);
			activateReceiver(conn);
			ColorsOut.out(name+"  | CREATED entry= "+entry+" conn=" + conn, ColorsOut.BLUE );
		} catch (Exception e) {
			ColorsOut.outerr( name+"  |  ERROR " + e.getMessage());		}
	}
	
 	public String getName() {
 		return name;
 	}
 	
	protected void setConnection( String host, String entry, ProtocolType protocol  ) throws Exception {
		switch( protocol ) {
			case tcp : {
				int port = Integer.parseInt(entry);
				//conn = new TcpConnection( new Socket( host, port ) ) ; //non fa attempts
				conn = TcpClientSupport.connect(host,  port, 10); //10 = num of attempts
				ColorsOut.out(name + " |  setConnection "  + conn, ColorsOut.BLUE );		
				break;
			}
			case udp : {
				int port = Integer.parseInt(entry);
 				conn = UdpClientSupport.connect(host,  port );  
				break;
			}
			case coap : {
 				//conn = new CoapConnection( host,  entry );
				break;
			}
			case mqtt : {
				//La connessione col Broker viene stabilita in fase di configurazione
				//La entry è quella definita per ricevere risposte;
				//ColorsOut.out(name+"  | ProxyAsClient connect MQTT entry=" + entry );
				//conn = MqttConnection.getSupport();					
 				break;
			}	
			default :{
				ColorsOut.outerr(name + " | Protocol unknown");
			}
		}
	}
  	
	//msg potrebbe essere QUALSISI (anche request)
	public synchronized void sendMsgOnConnection( String msg )  {
 		//ColorsOut.outappl( name+"  | sendMsgOnConnection " + msg + " conn=" + conn, ColorsOut.WHITE_BACKGROUND );
		try {
			conn.forward(msg);
		} catch (Exception e) {
			ColorsOut.outerr( name+"  | sendMsgOnConnection ERROR=" + e.getMessage()  );
		}
	}
 
	public Interaction2021 getConn() {
		return conn;
	}
	
	
	public void close() {
		try {
			conn.close();
			ColorsOut.out(name + " |  CLOSED " + conn  );
		} catch (Exception e) {
			ColorsOut.outerr( name+"  | sendRequestOnConnection ERROR=" + e.getMessage()  );		
		}
	}
	
//------------------------------------------------
	protected void activateReceiver( Interaction2021 conn) {
		new Thread() {
			public void run() {
				try {
					ColorsOut.out(name + " |  activateReceiver STARTED on conn=" + conn  );
					while(true) {
						String msgStr    = conn.receiveMsg();
						IApplMessage msg = new ApplMessage(msgStr);
						//ColorsOut.out(name + " |  activateReceiver RECEIVES " + msg  );
						QakActor22 a = Qak22Context.getActor(msg.msgReceiver());
						if( a != null ) Qak22Util.sendAMsg( msg ); 		
						else ColorsOut.outerr(name + " | activateReceiver: actor " + msg.msgReceiver() + " non local (I should not be here) ");					
					}
				} catch (Exception e) {
					ColorsOut.outerr( name+"  | activateReceiver ERROR=" + e.getMessage()  );				} 
				}
		}.start();
	}

}
