package unibo.actor22comm.interfaces;

import it.unibo.kactor.IApplMessage;

public interface IApplMsgHandler  {
	public String getName(); 
	//public void elaborate( String message, Interaction2021 conn ) ;	
	public void elaborate( IApplMessage message, Interaction2021 conn );//ESTENSIONE dopo Context
	public void sendMsgToClient( String message, Interaction2021 conn );
	public void sendAnswerToClient( String message, Interaction2021 conn  );
}
