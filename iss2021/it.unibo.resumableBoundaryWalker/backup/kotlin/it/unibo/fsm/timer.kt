package it.unibo.fsm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay


@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
class timer ( name: String, scope: CoroutineScope, usemqtt:Boolean=false, val owner: Fsm ) : Fsm( name, scope, true, usemqtt){
val ndnt ="   "	
	override fun getInitialState() : String{
		return "init"
	}

	override fun getBody() : (Fsm.() -> Unit){
		return {
			state("init") {
				action {
					println("$ndnt timer init ")
				}
				transition( edgeName="t0",targetState="waitcmd", cond=doswitch() )
			}
			
			state( "waitcmd" ){
				action {
//					println("$ndnt timer waits ... ")
				}				
				transition( edgeName="t0",targetState="work",    cond=whenDispatch("gauge") ) 
				transition( edgeName="t0",targetState="endwork", cond=whenDispatch("end") ) 
			}
			
			state("work") {
				action {
					 val delayTime = currentMsg.CONTENT.toLong()
//					 println("$ndnt timer work $delayTime")
					 delay( delayTime )
					 forward( msgId="endgauge", payload="$delayTime", dest=owner  )					
				}
				transition( edgeName="t0",targetState="waitcmd", cond=doswitch() ) 
			}				
			state("endwork") {
				action {
  					terminate()
  				}
 			}									
		}//return
	}//getBody
}//timer
