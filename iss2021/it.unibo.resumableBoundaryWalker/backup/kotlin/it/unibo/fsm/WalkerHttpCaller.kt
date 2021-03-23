/**
 * WalkerHttpCaller.kt
 * @author AN - DISI - Unibo
===============================================================

===============================================================
 */
package it.unibo.fsm

import it.unibo.interaction.WEnvHTTPSupport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


lateinit var walkerhttp : WalkerHttpCaller

class WalkerHttpCaller (name: String, scope: CoroutineScope, val hh : WEnvHTTPSupport,
					   discardMessages:Boolean=true ) : Fsm( name, scope, discardMessages ){
	override fun getInitialState() : String{
		return "init"
	}

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	override fun getBody() : (Fsm.() -> Unit){
		//val steprobot = stepper("stepper", scope, usemqtt=false, owner=myself )
		var nstep   = 0
		var endmove = false
		return{
			state("init") {	
				action {
					println("WalkerHttpCaller | START ")
					hh.sendMessage("w")//move the robot
				}
			    transition( edgeName="t0",targetState="walk", cond=doswitch() )
			}

			state("walk"){
				action {
					nstep++
					println("WalkerHttpCaller | walk nstep=$nstep")
					delay(700)
					endmove = hh.sendMessage("w")//move the robot
				}
				transition( edgeName="goto",targetState="walkend", cond=doswitchGuarded({  endmove  }) )
				transition( edgeName="goto",targetState="walk",    cond=doswitchGuarded({ ! endmove  }) )
			}

 			state("walkend"){
				action {
					println( "WalkerHttpCaller |  walkend: currentMsg=$currentMsg nstep=$nstep" )
					terminate()
				}
			}
		}
	}

}//WalkerHttpCaller 

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun main() = runBlocking{
	val hh     = WEnvHTTPSupport( "localhost:8090"  ) //blocking
	walkerhttp = WalkerHttpCaller("walker", this, hh )
	walkerhttp.waitTermination()
 	println("WalkerHttpCaller main ends")
}