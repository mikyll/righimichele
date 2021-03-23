/**
 * WebSocketViaChannel
 * @author AN - DISI - Unibo
===============================================================
Use a channel to send commands via a WebSocket (support=okhttp3)
and to receive the answers sent form the WebSocket
===============================================================
 */

package it.unibo.temp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
//https://square.github.io/okhttp/4.x/okhttp/okhttp3/-web-socket/
import okhttp3.*        //A non-blocking interface to a web socket
//Si potrebbe usare ancora avax.websocket ?

val socketEventChannel: Channel<SocketUpdate> = Channel(10) //our channel buffer is 10 events

class WebSocketViaChannel(val scope : CoroutineScope) : WebSocketListener() {
    private val NORMAL_CLOSURE_STATUS = 1000

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("MyWebSocketListener | onOpen ${webSocket}"  )
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        scope.launch {
            //println("MyWebSocketListener | onMessage ${text}"  )
            socketEventChannel.send(SocketUpdate(text))
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        scope.launch {
            socketEventChannel.send(SocketUpdate(exception = SocketAbortedException()))
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        socketEventChannel.close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(exception = t))
        }
    }

}

class SocketAbortedException : Exception()

data class SocketUpdate(
        val text: String?           = null,
        val request: Boolean        = false,
        val exception: Throwable?   = null
)

