/*
WEnvHTTPSupport.java
===============================================================
===============================================================
*/
package it.unibo.interaction

import mapRoomKotlin.mapUtil
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import java.net.URI

class WEnvHTTPSupport(hostAddr : String){
    //private val localHostName = "localhost" //"localhost"; 192.168.1.7
    //private val port = 8090
    private val URL = "http://$hostAddr/api/move"
    //private val containerHostName = "wenv"
    private val httpclient = HttpClients.createDefault()

    fun updateMap( move : String, showMap : Boolean = true ){
        if( move == "obstacle")  mapUtil.setObstacle(  )
        else mapUtil.doMove(move)
        if(showMap) mapUtil.showMap()
    }


    protected fun sendCmd( move: String,  time : Int ): Boolean {
         try {
            println("WEnvHTTPSupport | sendCmd: $move ")
            //String json         = "{\"robotmove\":\"" + move + "\"}";
            val json   = "{\"robotmove\":\"$move\" , \"time\": $time}"
            val entity = StringEntity(json)
            val httppost = RequestBuilder.post()
                    .setUri(URI(URL))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Accept", "application/json")
                    .setEntity(entity)
                    .build()
            val response = httpclient.execute(httppost)
            println( "WEnvHTTPSupport | sendCmd response= " + response );
            return checkCollision(move, response)
        } catch (e: Exception) {
            println("WEnvHTTPSupport | ERROR:" + e.message)
            throw e
        }
    }

    protected fun checkCollision(move: String, response: CloseableHttpResponse): Boolean {
        try {
            //response.getEntity().getContent() is an InputStream
            val jsonStr = EntityUtils.toString(response.entity)
            println("WEnvHTTPSupport | checkCollision jsonStr= $jsonStr")
            //jsonStr = {"endmove":true,"move":"moveForward"}
            val jsonObj   = JSONObject(jsonStr)
            var collision = false
            if (jsonObj["endmove"] != null) {
                collision = ! jsonObj.getBoolean("endmove" )
                println("WEnvHTTPSupport | checkCollision collision=$collision")
            }
            if( collision ) updateMap("obstacle")
            else updateMap(move )
            return collision
        } catch (e: Exception) {
            println("WEnvHTTPSupport | checkCollision ERROR:" + e.message)
            throw e
        }
    }

    //Method defined also in WEnvConnSupport

    fun sendMessage( move: String ) : Boolean {
        when( move ){
            "w" ->  return moveForward()
            "s" ->  return moveBackward()
            "l" ->  return moveLeft()
            "r" ->  return moveRight()
            "h" ->  return moveStop()
        }
        return moveStop()
    }
    fun moveForward(time:Int=400):  Boolean  {  return sendCmd("moveForward", time)  }
    fun moveBackward(time:Int=400): Boolean {  return sendCmd("moveBackward", time)  }
    fun moveLeft(): Boolean     {  return sendCmd("turnLeft", 300)      }
    fun moveRight(): Boolean    {  return sendCmd("turnRight", 300)     }
    fun moveStop(): Boolean     {  return sendCmd("alarm", 10)          }

}
