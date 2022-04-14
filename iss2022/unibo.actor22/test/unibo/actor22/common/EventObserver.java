package unibo.actor22.common;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22;
import unibo.actor22comm.utils.ColorsOut;

public class EventObserver extends QakActor22{

	public EventObserver(String name) {
		super(name);
 	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | handleMsg " + msg, ColorsOut.CYAN);		
	}

}
