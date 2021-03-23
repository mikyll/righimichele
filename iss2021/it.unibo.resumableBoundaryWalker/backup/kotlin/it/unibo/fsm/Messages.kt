/**
 * Messages.kt
 * @author AN - DISI - Unibo
===============================================================

===============================================================
 */
package it.unibo.fsm

object Messages{
	/*
	val initMsg        = AppMsg.create("init",    "main",	"robotboundary")
	val stopMsg        = AppMsg.create("stop",    "main",	"robotboundary")
	val startMsg       = AppMsg.create("start",   "main",	"robotboundary") 
	val resumeMsg      = AppMsg.create("resume",  "main",	"robotboundary")
	val workDoneMsg    = AppMsg.create("workdone","main",	"usermock"	   )
 	*/
/*
 Forward a dispatch to a destination actor given by reference
*/	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun forward(  sender: String, msgId : String, payload: String, dest : Fsm ){
	 	//println("forward  msgId: ${msgId} payload=$payload")
		val msg = AppMsg.buildDispatch(actor=sender, msgId=msgId , content=payload, dest=dest.name)
 		if( ! dest.fsmactor.isClosedForSend) dest.fsmactor.send( msg  )
		else println("WARNING: Messages.forward attempts to send ${msg} to closed ${dest.name} ")
	}

/*
 Forward a dispatch to a destination actor by using a given MQTT support
*/		
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun forward(  sender: String, msgId : String, payload: String, destName : String  ){
		val msg = AppMsg.buildDispatch(actor=sender, msgId=msgId , content=payload, dest=destName )

	}

/*
 Emit an event by using a given MQTT support
*/	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun emit(  sender: String, msgId : String, payload: String  ){
		val event = AppMsg.buildEvent(actor=sender, msgId=msgId , content=payload )

	}
	
	
			
}