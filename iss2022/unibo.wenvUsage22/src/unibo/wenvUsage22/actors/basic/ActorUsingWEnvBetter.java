package unibo.wenvUsage22.actors.basic;

import org.json.JSONObject;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.http.HttpConnection;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnectionForActors;
import unibo.wenvUsage22.common.ApplData;

public class ActorUsingWEnvBetter extends QakActor22  {
	private Interaction2021 conn;
	private int n = 0;
	
	public ActorUsingWEnvBetter(String name) {
		super(name);
		init();
	}

	protected void init() {
		conn = WsConnectionForActors.create("localhost:8091", getName() );
 		ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
	}
 

	protected void moveForward()  {
		try {
			ColorsOut.outappl(getName() + " | moveForward conn:" + conn,  ColorsOut.BLUE);
			conn.forward( ApplData.moveForward(2300) );
		}catch( Exception e) {
			ColorsOut.outerr( getName() +  " | doBasicMoves ERROR:" +  e.getMessage() );
		}
		
	}
	
	protected void turnLeft() {
		try {
			conn.forward( ApplData.turnLeft( 300  ) );
		}catch( Exception e) {
			ColorsOut.outerr( getName() +  " | turnLeft ERROR:" +  e.getMessage() );
		}
	
	}
	
	@Override
	protected void handleMsg(IApplMessage msg) {
		ColorsOut.outappl(getName() + " | handleMsg:" + msg,  ColorsOut.BLUE);
		interpret(msg);
	}
	
	protected void interpret( IApplMessage m ) {
		if( m.isEvent() || m.msgId().equals( SystemData.wsEventId ) ) {
			handleWsInfo(m);
			return;
		}
 		if( m.msgId().equals( ApplData.activateId )) {
			autoMsg(ApplData.moveCmd(getName(),getName(),"w"));
			return;
		}
		if( ! m.msgId().equals( ApplData.moveCmdId )) {
			ColorsOut.outappl(getName() + " | sorry, I don't handle :" + m,  ColorsOut.YELLOW);
			return;
		}
		//ColorsOut.outappl(getName() + " | interpret:" + m.msgContent(),  ColorsOut.BLUE);
		switch( m.msgContent() ) {
			case "w" : moveForward();break;
			case "a" : turnLeft();break;
			default: break;
		}
	}

    protected void handleWsInfo(IApplMessage m) {
		ColorsOut.outappl(getName() + " | handleWsInfo:" + m,  ColorsOut.GREEN);	
		String msg = m.msgContent().replace("'", "");
		JSONObject d = new JSONObject(""+msg);
		if( d.has("collision")) {
			n++;
			//autoMsg(ApplData.moveCmd(getName(),getName(),"a"));
			sendMsg(ApplData.moveCmd(getName(),getName(),"a"));
		}
		if( d.has("endmove") && d.getBoolean("endmove") && n < 4) 
			//autoMsg(ApplData.moveCmd(getName(),getName(),"w"));
			sendMsg(ApplData.moveCmd(getName(),getName(),"w"));
   	
    }
 
}
