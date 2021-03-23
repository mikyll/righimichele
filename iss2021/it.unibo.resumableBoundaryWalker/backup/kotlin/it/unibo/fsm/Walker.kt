/**
 * Walker.kt
 * @author AN - DISI - Unibo
===============================================================
A fsm that works as the control for the virtualrobot (walker).
We map events raised from WEnv into dispatches sent to walker.
The walker goes ahead (step by step) until an obstacle (a wall) is found.
The result of the computation is the number of steps done.
We could set the step time so that (e.g. 400)
each step will cover a distance equal to the length of the robot.
===============================================================
 */
package it.unibo.fsm

import it.unibo.interaction.WEnvConnSupport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mapRoomKotlin.mapUtil
import org.json.JSONObject

lateinit var walker : Walker
//lateinit var hh : WEnvConnSupport

class Walker (name: String, scope: CoroutineScope, val hh : WEnvConnSupport,
			  discardMessages:Boolean=true ) : Fsm( name, scope, discardMessages ){
	override fun getInitialState() : String{
		return "init"
	}

	fun updateMap( move : String, showMap : Boolean = true ){
		if( move == "obstacle")  mapUtil.setObstacle(  )
		else mapUtil.doMove(move)
		if(showMap) mapUtil.showMap()
	}

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	override fun getBody() : (Fsm.() -> Unit){
		//val steprobot = stepper("stepper", scope, usemqtt=false, owner=myself )
		var nstep = 0
		return{
			state("init") {	
				action {
					println("walker | START ")
					hh.sendMessage("w")//move the robot
				}
			    transition( edgeName="t0",targetState="walk", cond=doswitch() )
			}

			state("walk"){
				action {
					nstep++
					println("walker | walk nstep=$nstep")
					delay(700)
					hh.sendMessage("w")//move the robot
				}
				transition( edgeName="t3",targetState="handleSonar",    cond=whenDispatch("sonar") )
				transition( edgeName="t0",targetState="endmoveok",      cond=whenDispatch("stepdone") )
				transition( edgeName="t1",targetState="walkend",        cond=whenDispatch("stepfail") )
				transition( edgeName="t2",targetState="handleObstacle", cond=whenDispatch("obstacle") )
			}

			state( "endmoveok" ){
				action{
					updateMap("w", true)
				}
				transition( edgeName="t0",targetState="walk", cond=doswitch() )
			}


 			state("walkend"){
				action {
					println( "walker |  walkend: currentMsg=$currentMsg nstep=$nstep" )
					terminate()
				}
				transition( edgeName="t0",targetState="walk", cond=doswitch() )	//to handle sonar (if any)
			}

			state("handleObstacle"){
				action {
					println( "walker |  handleObstacle: currentMsg=$currentMsg nstep=$nstep" )
					updateMap("obstacle")
					terminate()
				}
			}

			state("handleSonar"){
				action {
					println( "walker |  handleSonar: currentMsg=$currentMsg nstep=$nstep" )
					//terminate()
				}
				transition( edgeName="t0",targetState="walk", cond=doswitch() )	//to handle other events (if any)
			}
		}
	}

}//walker 


//transform messages sent from WEnv in dispatches (stepdone, stepfail)

suspend fun mapWEnvEventToDispatch( jsonMsg : String ) { //called by startReceiver in WEnvConnSupport
	println("handleWEnvEvent | receives: $jsonMsg ")
	val jsonObject    = JSONObject( jsonMsg )
	if( jsonObject.has("endmove") ) {
		val endmove = jsonObject.getBoolean("endmove")
		//println("handleWEnvEvent | endmove:  ${endmove} ")
		if(endmove) Messages.forward("wenv","stepdone","ok", walker )
		else Messages.forward("wenv","stepfail","todo(Time)", walker )
	}
	else if( jsonObject.has("collision") ){
		Messages.forward("wenv","obstacle","obstacle", walker )
	}
	else if( jsonObject.has("sonarName") ){
		val distance = jsonObject.getInt("distance").toString()
		Messages.forward("wenv","sonar", distance, walker )
	}

}//mapWEnvEventToDispatch



@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun main() = runBlocking{
	val hh = WEnvConnSupport( this, "localhost:8091", "400" ) //blocking
	walker = Walker("walker", this, hh )
	hh.startReceiver( ::mapWEnvEventToDispatch  )

	//Messages.forward("main","msg","w", walker )
	walker.waitTermination()
	delay(3000)  //to see some sonar message, if it is the case
	hh.stopReceiver()
	println("walker main ends")
}