/**
 * ClientWebsockJavax
 * @author AN - DISI - Unibo
===============================================================
Kotlin version of the Java version
walks along the boundary of the room and then works as an observer
===============================================================
 */
package it.unibo.virtualrobotclient

import org.glassfish.tyrus.client.ClientManager
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import javax.websocket.*

interface IWalk {
    @Throws(Exception::class)
    fun nextStep(collision: Boolean)
}

@ClientEndpoint
class ClientWebsockJavax(addr: String) {
    var userSession: Session? = null
    private var messageHandler: MessageHandler? = null
    //private var simpleparser: JSONParser? = null

    interface MessageHandler {
        @Throws(Exception::class)
        fun handleMessage(message: String)
    }

    init {
        if( addr.length > 0){
            println("ClientWebsockJavax |  CREATING ... $addr")
            initConn(addr)
        }
    }

    protected fun initConn(addr: String) {
        try {
            //ALTERNATIVE
            //val container = ContainerProvider.getWebSocketContainer()
            //container.connectToServer(this, URI("ws://$addr"))
            val endpointURI = URI( "ws://$addr/" )
            println("ClientWebsockJavaxUsingCoroutines | initClientConn $endpointURI")
            val client = ClientManager.createClient()
            client.connectToServer(this, endpointURI)
        } catch (ex: URISyntaxException) {
            println("ClientWebsockJavax | URISyntaxException exception: " + ex.message)
        } catch (e1: DeploymentException) {
            println("ClientWebsockJavaxUsingCoroutines | DeploymentException : " + e1.message)
        } catch (e2: IOException) {
            println("ClientWebsockJavaxUsingCoroutines | IOException : " + e2.message)
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    fun onOpen(userSession: Session?) {
        println("ClientWebsockJavax | opening websocket")
             this.userSession = userSession
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    fun onClose(userSession: Session?, reason: CloseReason?) {
        println("ClientWebsockJavax | closing websocket")
        this.userSession = null
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    @Throws(Exception::class)
    fun onMessage(message: String) {
        if (messageHandler != null) {
            messageHandler!!.handleMessage(message)
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    fun addMessageHandler(msgHandler: MessageHandler?) {
        messageHandler = msgHandler
    }

    /**
     * Send a message.
     *
     * @param message
     */
    @Throws(Exception::class)
    fun sendMessage(message: String) {
        println("ClientWebsockJavax | sendMessage $message")
        //userSession!!.getAsyncRemote().sendText(message);
        if( userSession != null)
            userSession!!.basicRemote.sendText(message) //synch: blocks until the message has been transmitted
        else println("ClientWebsockJavax | sorry, no userSession")
    }

/*
BUSINESS LOGIC
 */


    protected fun setObserver(walker : IWalk) {
        // add listener
        addMessageHandler(object : MessageHandler {
            override fun handleMessage(message: String) {
                try {
                    //{"collision":"true ","move":"..."} or {"sonarName":"sonar2","distance":19,"axis":"x"}
                    println("ClientWebsockJavax | handleMessage:$message")
                    //val jsonObj = simpleparser!!.parse(message) as JSONObject
                    val jsonObj = org.json.JSONObject( message )
                    if (jsonObj["endmove"] != null) {
                        val endmove = jsonObj.get("endmove") as Boolean //jsonObj["endmove"].toString() == "true"
                        val move    = jsonObj["move"].toString()
                        println("ClientWebsockJavax | handleMessage $move endmove=$endmove")
                        walker.nextStep( !endmove )
                    } else if (jsonObj.has("collision" ) ) {
                        val collision = jsonObj.get("collision") as Boolean
                        val move = jsonObj["move"].toString()
                        println("ClientWebsockJavax | handleMessage collision=$collision move=$move")
                        walker.nextStep(collision)
                    } else if (jsonObj["sonarName"] != null) {
                        val sonarNAme = jsonObj["sonarName"].toString()
                        val distance = jsonObj["distance"].toString()
                        println("ClientWebsockJavax | handleMessage sonaraAme=$sonarNAme distance=$distance")
                    }
                } catch (e: Exception) {
                }
            }
        })
    }

    @Throws(Exception::class)
    fun doJob() {
        setObserver( walkLogic( ::sendMessage )  )
        println("ClientWebsockJavax | doJob ENDS ======================================= ")
    }//doJob

}//ClientWebsockJavax

class walkLogic( val sendMessage : (String)->Unit )  : IWalk {
    private var count = 0

    init{
        count = 1
        var cmd = "{\"robotmove\":\"moveForward\", \"time\": 600}"
        sendMessage(cmd)
    }

    override fun nextStep(collision: Boolean) {
        System.out.println(" %%% nextStep collision=" + collision + " count=" + count)
        if (count > 4) {
            println("ClientWebsockJavax | BYE (from nextStep)")
            return
        }
        //Thread.sleep(500) ;   //interval before the next move
        //System.in.read();
        if (collision) {
            if (count++ <= 4) {
                var cmd = "{\"robotmove\":\"turnLeft\" , \"time\": 300}"
                sendMessage(cmd)
            }
        } else {  //no collision
            var cmd = "{\"robotmove\":\"moveForward\" , \"time\": 600}"
            sendMessage(cmd)
        }
    }
}

fun main(args: Array<String>) {
    try {
        ClientWebsockJavax("localhost:8091").doJob()
        // wait  for messages from websocket
        Thread.sleep(15000)
    } catch (ex: Exception) {
        System.err.println("ClientWebsockJavax | InterruptedException exception: " + ex.message)
    }
}
