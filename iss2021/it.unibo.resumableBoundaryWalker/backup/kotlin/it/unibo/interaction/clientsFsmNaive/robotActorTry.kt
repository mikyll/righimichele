/*
robotActorTry.kt
===============================================================
Provides
See /it.unibo.kotlinIntro/userDocs/FirstActorRobot.html
===============================================================
*/

package it.unibo.interaction.clientsFsmNaive


//import kotlinx.coroutines.runBlocking
import it.unibo.interaction.WEnvConnSupportNoChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import org.json.JSONObject

val showEvents  = { v : String ->  println("showWEnvEvents: $v ")  }//showWEnvEvents

//Actor that includes the business logic; the behavior is message-driven
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
val robotActorTry  : SendChannel<String>	= CoroutineScope( Dispatchers.Default ).actor {
    var state    = "working"
    val hh       = WEnvConnSupportNoChannel( "localhost:8091", "300" )
    fun doInit() = { println("robotActorTry |  INIT" ) }
    fun doEnd()  = { state = "robotActorTry | end"  }
    fun doSensor(msg : String){ println("robotActorTry | doSensor should handle: $msg") }

    fun doCollision(msg : String){
        println("robotActorTry handles $msg going back a little");
        //val goback =  "{ 'type': 'moveBackward', 'arg': 100 }"
        hh.sendMessage( "s"  )
        //delay(500)
    }

    fun doMove( move : String ){ //msgSplitted : List<String>
         println("robotActorTry | doMove cmd: $move ") //{ 'type': 'moveForward', 'arg': 2000 }
        hh.sendCrilMessage( move )
    }

    while( state == "working" ){
        var msg = channel.receive()
        println("robotActorTry | receives: $msg ") // FUNCTOR( ... )
        val msgSplitted = msg.split('(')
        val msgFunctor  = msgSplitted[0]
        val move        = msgSplitted[1].replace(")","")
        //println("robotActorTry | msgFunctor $msgFunctor ")
        when( msgFunctor ){
            "init"      -> doInit()
            "end"       -> doEnd()
            "sensor"    -> doSensor(msg)        //non active in this version
            "collision" -> doCollision(msg)
            "move"      -> doMove(move) //
            else        -> println("NO HANDLE for $msg")
        }
    }
    println("robotActorTry | ENDS state=$state")
}


