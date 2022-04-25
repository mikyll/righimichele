package unibo.wenvUsage22.actors.basic;

import java.util.Observable;
import org.json.JSONObject;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22;
import unibo.actor22comm.http.HttpConnection;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.ApplData;

public class ActorUsingWEnv extends QakActor22 implements IObserver{
	private Interaction2021 conn;
	private int n = 0;
	
	public ActorUsingWEnv(String name) {
		super(name);
		init();
	}

	protected void init() {
		conn = WsConnection.create("localhost:8091" );
		((WsConnection)conn).addObserver(this);
		ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
	}
	
	protected void doBasicMovesHttp()  {
		try {
	  		conn = HttpConnection.create("localhost:8090" ); //INTERROMPIBILE usando WebGui
	  		String answer = "";
	 		answer = conn.request( ApplData.turnLeft(300) );
			ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
			answer = conn.request( ApplData.turnRight(300) );
			ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
	 		answer = conn.request( ApplData.moveForward(2000) ); 
	 		//risposta dopo duration a meno di interruzioni DA ALTRA FONTE
			ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
		}catch( Exception e) {
			ColorsOut.outerr( getName() +  " | doBasicMoves ERROR:" +  e.getMessage() );
		}
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
		//doBasicMovesHttp();
 		interpret(msg);
	}
	
	protected void interpret( IApplMessage m ) {
		if( m.msgId().equals( ApplData.activateId )) {
			autoMsg(ApplData.moveCmd(getName(),getName(),"w"));
			return;
		}
		if( ! m.msgId().equals( ApplData.moveCmdId )) {
			ColorsOut.outappl(getName() + " | sorry, I don't handle :" + m,  ColorsOut.BLUE);
			return;
		}
		//ColorsOut.outappl(getName() + " | interpret:" + m.msgContent(),  ColorsOut.BLUE);
		switch( m.msgContent() ) {
			case "w" : moveForward();break;
			case "a" : turnLeft();break;
			default: break;
		}
	}

 
	@Override
	public void update(String msg) {
		ColorsOut.outappl(getName() + " | update:" + msg,  ColorsOut.BLUE);		
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

	@Override
	public void update(Observable o, Object msg) {
		//ColorsOut.outappl(getName() + " | update/2:" + msg,  ColorsOut.BLUE);		
		update(msg.toString());
	}

}
