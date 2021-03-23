/*
robotActorAppMsg.kt
===============================================================
A component that embeds an actor that works as a naive FSM
The component provides a forward operation to send
Uses WEnvConnSupport (time=2500)
===============================================================
*/
package it.unibo.interaction.clientsFsmNaive

import it.unibo.fsm.AppMsg
import it.unibo.interaction.WEnvConnSupport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mapRoomKotlin.mapUtil
import org.json.JSONObject

val moveDurationTime = "3000"
class robotActorAppMsg(
        val scope: CoroutineScope,    //scope required for channel
        val hostAddr: String,
        val moveDuration: String = "600") {

    lateinit var hh     : WEnvConnSupport
    var currentMove     = "h"
    //var glued           = false

    fun updateMap( move : String, showMap : Boolean = true ){
        if( move.startsWith("obstacle_") ) { mapUtil.setObstacle( move.replace("obstacle_","") )
        }else if( move != "h"){
            mapUtil.doMove(move)
            if(showMap) mapUtil.showMap()
        }
    }

    fun getActor() : SendChannel<AppMsg>{
        return myactor
    }

    val myactor: SendChannel<AppMsg> = CoroutineScope(Dispatchers.Default).actor {
        var state = "working"

        fun doInit()  {
            println("myactor | INIT")
            mapUtil.showMap()
        }

        fun doEnd()  {
            println("myactor | END")
            state = "end"
        }

        suspend fun doCollision(msgContent: String) {
            //println("\u0007")  println(7.toChar())        //Ring the Terminal BELL ??
            java.awt.Toolkit.getDefaultToolkit().beep();    //Ring the Terminal BELL
            hh.sendMessage("l")
            delay(400)
            hh.sendMessage("r")
            delay(400)
            if( currentMove  == "w"){
                println("myactor | handles ${msgContent} by going back a little ");
                hh.sendMessage("s")
                delay(250)
                hh.sendMessage("h")
                delay(500)
                updateMap( "obstacle_w" )
            }else if( currentMove  == "s"){
                //WARNING: the virtual robot is 'glued' to the obstacle
                hh.sendMessage("w")
                delay(150)
                hh.sendMessage("h")
                println("WARNINNG: the virtual robot could be glued ...");
                //state = "glued"
                updateMap( "obstacle_s"  )
            }
            currentMove  = "h"

        }


        fun doMove(move: String) {  //move w | s | ...
            println("myactor | move todo=$move")
            currentMove = move
            hh.sendMessage(move)        //move in the application-language
            updateMap(move)
        }

        //Sate machine loop
        while (state != "end") {    //intially working
                var msg = channel.receive() //AppMsg
                // println("myactor | state=$state msg=$msg")
                 when( state ){
                     "working" -> {
                         when( msg.MSGID ){
                             "init"  -> doInit()
                             "end"   -> doEnd()
                             "move"  -> doMove(msg.CONTENT)
                             "endMoveFail" ->  doCollision( msg.CONTENT )
                         }
                     }
                     "glued" -> doEnd()     //perhaps no more necessary ....
                }//when
        }//while
        hh.stopReceiver()

    }

    fun forward(msg: AppMsg){
        //println("selfMsg myactor=$myactor msg=$msg"  )
        scope.launch { myactor.send(msg) }
    }

    /*
   Called when a new  WEnv-event is raised
    */
    fun handleWEnvEvent(msgContent: String) {
        println("           myactor | handleWEnvEvent ... : ${msgContent}  ")
        if( msgContent.contains("collision") ){ //{"collision":true,"move":"unknown"}
            forward(AppMsg.create("endMoveFail", "handleWEnvEvents", "robotActorAppMsg", msgContent))
        }
    }

    init{
        hh = WEnvConnSupport(scope, hostAddr, moveDurationTime)
        val WEnvEvent_Handler  = { v: String -> handleWEnvEvent( v ) }
        scope.launch { hh.activateReceiver( WEnvEvent_Handler ) }
    }


}

/*
The duration of a move (moveDurationTime) is quite long
The 'endmove' event sent by WEnv is discarded by handleWEnvEvent (or lost if the appl is terminates)
The duration of a move is established by the interval between two move-commands, i.e.
a move ends when the user sends another move command => the robot state remains 'working'

A move can fail because a collision => the collision event is mapped into a 'endMoveFail' message
 */