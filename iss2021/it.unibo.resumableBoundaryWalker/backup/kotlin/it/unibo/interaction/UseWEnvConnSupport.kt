/*
UseWEnvConnSupport.kt
===============================================================
See https://www.websocket.org/echo.html
Uses the WEnvConnSupport to move a virtual robot along the room boundary.
The business logic is embedded in the callback given to the activateReceiver method
===============================================================
*/

package it.unibo.interaction


import it.unibo.interaction.WEnvConnSupport
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

interface IWalker {
    @Throws(Exception::class)
    fun nextStep(collision: Boolean)
}

class Walker( val hh : WEnvConnSupport)  : IWalker {
    private var count = 1

    override fun nextStep(collision: Boolean) {
        println(" %%% nextStep collision= $collision count=$count" )
        if (count > 4) {
            println("Walker | BYE (from nextStep)")
            return
        }
        //Thread.sleep(500) ;   //interval before the next move
        //System.in.read();
        if (collision) {
            if (count++ <= 4) {
                 hh.sendMessage("l")
            }
        } else {  //no collision
            hh.sendMessage("w")
        }
    }
}//walker

lateinit var walker : Walker

fun handlerToWalk( message : String ) {

        try {
            //{"collision":"true ","move":"..."} or {"sonarName":"sonar2","distance":19,"axis":"x"}
            println("handlerToWalk | handleMessage:$message")
            //val jsonObj = simpleparser!!.parse(message) as JSONObject
            val jsonObj = org.json.JSONObject( message )
            if (jsonObj["endmove"] != null) {
                val endmove = jsonObj.get("endmove") as Boolean //jsonObj["endmove"].toString() == "true"
                val move    = jsonObj["move"].toString()
                println("handlerToWalk | handleMessage $move endmove=$endmove")
                walker.nextStep( !endmove )
            } else if (jsonObj.has("collision" ) ) {
                val collision = jsonObj.get("collision") as Boolean
                val move = jsonObj["move"].toString()
                println("handlerToWalk | handleMessage collision=$collision move=$move")
                walker.nextStep( collision )
            } else if (jsonObj["sonarName"] != null) {
                val sonarNAme = jsonObj["sonarName"].toString()
                val distance = jsonObj["distance"].toString()
                println("handlerToWalk | handleMessage sonarNAme=$sonarNAme distance=$distance")
            }
        } catch (e: Exception) {
        }


}

val showWEnvEvents  = { v : String ->  println("showWEnvEvents: $v ")  }//showWEnvEvents
fun handleWEnvEvent( jsonMsg : String ) {
    println("handleWEnvEvent | receives: $jsonMsg ")
    val jsonObject    = JSONObject( jsonMsg )
    val hasendmove    = jsonObject.has("endmove")
    val hascollision  = jsonObject.has("collision")
    if( hasendmove   )  println("handleWEnvEvent | endmove: ${jsonObject.get("endmove")} ")
    if( hascollision ) println("handleWEnvEvent | collision: ${jsonObject.get("collision")} ")
}//handleWEnvEvent

suspend fun doSomeMove( hh : WEnvConnSupport){
    //var jsonString = "{\"robotmove\":\"turnLeft\" , \"time\": 300}"
    hh.sendMessage(  "l" )
    delay(1000)
    //jsonString = "{\"robotmove\":\"turnRight\" , \"time\": 300}"
    hh.sendMessage( "r" )
    //UseWEnvConnSupport.sendSomeCommand( this, hh  )
}

fun main( ) = runBlocking {
    println("==============================================")
    println("PLEASE, ACTIVATE WENV ")
    println("==============================================")
    val hh = WEnvConnSupport( this, "localhost:8091" )
    //hh.initConn("localhost:8091")       //blocking
    // hh.activateReceiver( showWEnvEvents )
    // hh.activateReceiver( ::handleWEnvEvent )
    //doSomeMove( hh )

    walker = Walker( hh )  //boundary

    hh.activateReceiver( ::handlerToWalk  )
    hh.sendMessage("w") //start walking

    delay(30000)    //to show data sent by WEnv
    println("BYE")
}


