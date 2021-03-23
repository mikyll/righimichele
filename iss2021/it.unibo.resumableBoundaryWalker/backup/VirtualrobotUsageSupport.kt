/**
 * VirtualrobotUsageSupport
 * @author AN - DISI - Unibo
===============================================================
provides API to invoke commands to move the virtual robot
===============================================================
 */
package it.unibo.virtualrobotclient

import org.json.*
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.IOException
import java.io.PrintWriter
import java.net.URI
import java.net.URISyntaxException
import javax.websocket.*

@ClientEndpoint
class VirtualrobotUsageSupport( ) {
    var userSession: Session? = null
    private var messageHandler: MessageHandler? = null
    private var simpleparser: JSONParser?       = null
    private val sep                             = ";"
    private var outToServer : PrintWriter?      = null

    
    interface MessageHandler {
        @Throws(ParseException::class)
        fun handleMessage(message: String)
    }

    protected fun init(addr: String) {
        try {
            simpleparser = JSONParser()
            val container = ContainerProvider.getWebSocketContainer()
            container.connectToServer(this, URI("ws://$addr"))
        } catch (ex: URISyntaxException) {
            System.err.println("VirtualrobotUsageSupport | URISyntaxException exception: " + ex.message)
        } catch (e: DeploymentException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    fun onOpen(userSession: Session?) {
        println("VirtualrobotUsageSupport | opening websocket")
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
        println("VirtualrobotUsageSupport | closing websocket")
        this.userSession = null
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    @Throws(ParseException::class)
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
        println("VirtualrobotUsageSupport | sendMessage $message")
        //userSession!!.getAsyncRemote().sendText(message);
        userSession!!.basicRemote.sendText(message) //synch: blocks until the message has been transmitted
    }

    /*
BUSINESS LOGIC
 */
    interface IGoon {
        @Throws(Exception::class)
        fun nextStep(collision: Boolean)
    }

    protected fun setObserver(goon: IGoon) {
 
    }

    //setObserver
    private var count = 0

    @Throws(Exception::class)
    fun doSomeMOve() {
        //domove("")
        sendMessage("{\"robotmove\":\"turnLeft\"}")
        Thread.sleep(1000)
        sendMessage("{\"robotmove\":\"turnRight\"}")
    }


    fun domove(cmd: String) {	//cmd is written in cril
        val jsonObject = JSONObject(cmd )
        val msg = "$sep${jsonObject.toString()}$sep"

        //outToServer?.println(msg)
        //outToServer?.flush()
    }
    //translates application-language in cril
    fun translate(cmd: String) : String{ //cmd is written in application-language
        var jsonMsg = "{ 'type': 'alarm', 'arg': -1 }"
        when( cmd ){
            "msg(w)", "w" -> jsonMsg = "{ 'type': 'moveForward',  'arg': -1 }"
            "msg(s)", "s" -> jsonMsg = "{ 'type': 'moveBackward', 'arg': -1 }"
            "msg(a)", "a" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': -1  }"
            "msg(d)", "d" -> jsonMsg = "{ 'type': 'turnRight', 'arg': -1  }"
            "msg(l)", "l" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': 300 }"
            "msg(r)", "r" -> jsonMsg = "{ 'type': 'turnRight', 'arg': 300 }"
            "msg(z)", "z" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': -1  }"
            "msg(x)", "x" -> jsonMsg = "{ 'type': 'turnRight', 'arg': -1  }"
            "msg(h)", "h" -> jsonMsg = "{ 'type': 'alarm',     'arg': 100 }"
            else -> println("VirtualrobotUsageSupport command $cmd unknown")
        }
        val jsonObject = JSONObject( jsonMsg )
        val msg = "$sep${jsonObject.toString()}$sep"
        return msg
    }

    fun halt(){
        domove("{ 'type': 'alarm',     'arg': 100 }")
    }

}