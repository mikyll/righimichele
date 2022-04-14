package unibo.actor22.common;
 
import it.unibo.kactor.IApplMessage;
import it.unibo.kactor.MsgUtil;
import it.unibo.radarSystem22.domain.interfaces.ILed;
import unibo.actor22.QakActor22;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import it.unibo.radarSystem22.domain.DeviceFactory;


/*
 * Funge da interprete di comandi e richieste
 */
public class LedActor extends QakActor22{
private ILed led;

	public LedActor(String name) {
		super(name);
		led = DeviceFactory.createLed();
	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		CommUtils.aboutThreads(getName()  + " |  Before doJob - ");
		ColorsOut.out( getName()  + " | doJob " + msg, ColorsOut.CYAN);
		if( msg.isRequest() ) elabRequest(msg);
		else elabCmd(msg);
	}

	protected void elabCmd(IApplMessage msg) {
		String msgCmd = msg.msgContent();
 		switch( msgCmd ) {
			case ApplData.comdLedon  : led.turnOn();break;
			case ApplData.comdLedoff : led.turnOff();break;
			default: ColorsOut.outerr(getName()  + " | unknown " + msgCmd);
		}
	}
 
	protected void elabRequest(IApplMessage msg) {
		String msgReq = msg.msgContent();
		//ColorsOut.out( getName()  + " | elabRequest " + msgCmd, ColorsOut.CYAN);
		switch( msgReq ) {
			case ApplData.reqLedState  :{
				boolean b = led.getState();
				IApplMessage reply = MsgUtil.buildReply(getName(), ApplData.reqLedState, ""+b, msg.msgSender());
				ColorsOut.out( getName()  + " | reply= " + reply, ColorsOut.CYAN);
 				sendReply(msg, reply );				
				break;
			}
 			default: ColorsOut.outerr(getName()  + " | unknown " + msgReq);
		}
	}

}
