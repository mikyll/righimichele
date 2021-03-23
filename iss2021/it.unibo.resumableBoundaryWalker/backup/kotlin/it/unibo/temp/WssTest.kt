package it.unibo.temp;

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
//https://square.github.io/okhttp/4.x/okhttp/okhttp3/-web-socket/
import okhttp3.OkHttpClient //A non-blocking interface to a web socket
import okhttp3.Request
import okhttp3.WebSocket

//Si potrebbe usare ancora avax.websocket ?

fun main() = runBlocking {
    try {
        println("WssTest | START ")
        val sys = WssTest()
        sys.connect( this )
        sys.receiver(this)
        delay(1000)
        sys.sender(this)
        delay(1000)
        println("WssTest | END ")
    } catch (ex: Exception) {
        println("WssTest | exception: " + ex.message)
    }
}


public class WssTest() {
    var webSocket8091: WebSocket?  = null

    suspend fun receiver(scope : CoroutineScope) {
        val receiver = scope.launch {
            while ( true ) {
                val v = socketEventChannel.receive()
                println("RECEIVER | receives $v ")  //in ${curThread()}
                if(v.request ) {
                    if( webSocket8091 != null ) webSocket8091!!.send(v.text!!)
                }else {
                     println("RECEIVER | receives ${v.text} ")
                }
            }
        }
    }//receiver

    suspend fun sender(scope : CoroutineScope  ) {
        val turnLeft  = "{\"robotmove\":\"turnLeft\", \"time\": 400}"
        val turnRight = "{\"robotmove\":\"turnRight\", \"time\": 400}"
        scope.launch {
            socketEventChannel.send( SocketUpdate( turnLeft, true  ) )
            delay(1000)
            socketEventChannel.send( SocketUpdate( turnRight, true  ) )

        }
    }//sender

    fun connect( scope : CoroutineScope ) {
        val client = OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                //.sslSocketFactory() - ? нужно ли его указывать дополнительно
                .build()
        val request = Request.Builder()
                //.url("wss://echo.websocket.org") //see https://www.websocket.org/echo.html
                .url("ws://localhost:8091")
                .build()
        println("WssTest | doJob ${request}"  )
        val wsListener = WebSocketViaChannel( scope )
        webSocket8091  = client.newWebSocket(request, wsListener)  // this provide to make 'Open ws connection'

    }

}
