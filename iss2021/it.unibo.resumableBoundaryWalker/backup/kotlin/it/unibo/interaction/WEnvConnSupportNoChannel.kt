/**
 * WEnvConnSupportNoChannel
 * @author AN - DISI - Unibo
===============================================================
cril  = concrete-robot interaction language
hril  = highlevel robot interaction language
Using the javax.websocket library:
  sets a connection via websocket with the WENv working at the given hostAddr
  provides a method (sendMessage) to send commands written in hril to a robot able to understand the the cril language
  shows the messages sent on the websocket by the WENv
===============================================================
 */
package it.unibo.interaction

import kotlinx.coroutines.channels.Channel
import org.glassfish.tyrus.client.ClientManager
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import javax.websocket.*


@ClientEndpoint
public class WEnvConnSupportNoChannel(val hostAddr  : String,
                                      val moveDuration : String = "600") {

    val moveTime                                  = "600"
    val turnTime                                  = "300"
    var userSession: Session?                     = null
    private var messageHandler: MessageHandler?   = null

    interface MessageHandler {
        //@Throws(ParseException::class)
        fun handleMessage(message: String)
    }
    init{
        initConn( hostAddr )
    }
    public fun initConn(addr: String) {
        try {
            //ALTERNATIVE WAY:
            //val container = ContainerProvider.getWebSocketContainer()
            //container.connectToServer(this, URI("ws://$addr"))
            val endpointURI = URI( "ws://$addr/" )
            println("WEnvConnSupportNoChannel | initClientConn $endpointURI")
            val client = ClientManager.createClient()
            client.connectToServer(this, endpointURI)
        } catch (ex: URISyntaxException) {
            println("WEnvConnSupportNoChannel | URISyntaxException exception: " + ex.message)
        } catch (e1: DeploymentException) {
            println("WEnvConnSupportNoChannel | DeploymentException : " + e1.message)
        } catch (e2: IOException) {
            println("WEnvConnSupportNoChannel | IOException : " + e2.message)
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    fun onOpen(userSession: Session?) {
        println("WEnvConnSupportNoChannel | opening websocket")
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
        println("WEnvConnSupportNoChannel | closing websocket")
        this.userSession = null
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    //@Throws(ParseException::class)
    fun onMessage(message: String) {
        println("WEnvConnSupportNoChannel | websocket receives: $message ")
        //TODO: the message should be redirected to the application level
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
     * @param message   w | s ...
     */
    @Throws(Exception::class)
    fun sendMessage(message: String) {
        println("WEnvConnSupportNoChannel | sendMessage $message")
        //userSession!!.getAsyncRemote().sendText(translate(message));
        if( userSession != null)
            userSession!!.basicRemote.sendText( translate(message) ) //synch: blocks until the message has been transmitted
        else println("WEnvConnSupportNoChannel | sorry, no userSession")
    }

    fun sendCrilMessage(message: String) {  //cril=concrete-robot interaction language
        println("WEnvConnSupportNoChannel | sendMessage $message")
        //"{ 'type': 'alarm', 'arg': -1 }"
        //userSession!!.getAsyncRemote().sendText(translate(message));
        if( userSession != null)
            userSession!!.basicRemote.sendText(  message ) //synch: blocks until the message has been transmitted
        else println("WEnvConnSupportNoChannel | sorry, no userSession")
    }

    fun translate(cmd: String) : String{ //cmd is written in application-language
        var jsonMsg = "{\"robotmove\":\"MOVE\" , \"time\": DURATION}"  //"{ 'type': 'alarm', 'arg': -1 }"
        when( cmd ){
            "msg(w)", "w" -> jsonMsg = jsonMsg.replace("MOVE","moveForward").replace("DURATION", moveTime)
            "msg(s)", "s" -> jsonMsg = jsonMsg.replace("MOVE","moveBackward").replace("DURATION", moveTime)
                    //"{ 'type': 'moveBackward', 'arg': -1 }"
            "msg(a)", "a", "l" -> jsonMsg = jsonMsg.replace("MOVE","turnLeft").replace("DURATION", turnTime)
                    //"{ 'type': 'turnLeft',  'arg': -1  }"
            "msg(d)", "d", "r" -> jsonMsg = jsonMsg.replace("MOVE","turnRight").replace("DURATION", turnTime)
            //"{ 'type': 'turnRight', 'arg': -1  }"
            //"msg(l)", "l" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': 300 }"
            //"msg(r)", "r" -> jsonMsg = "{ 'type': 'turnRight', 'arg': 300 }"
            //"msg(z)", "z" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': -1  }"
            //"msg(x)", "x" -> jsonMsg = "{ 'type': 'turnRight', 'arg': -1  }"
            "msg(h)", "h" -> jsonMsg = jsonMsg.replace("MOVE","alarm").replace("DURATION", "100")
            //"{ 'type': 'alarm',     'arg': 100 }"

            else -> println("WEnvConnSupportNoChannel command $cmd unknown")
        }
        val jsonObject = JSONObject( jsonMsg )
        val msg        =  jsonObject.toString()
        //println("WEnvConnSupportNoChannel | translate output= $msg ")
        return msg
    }
 
}