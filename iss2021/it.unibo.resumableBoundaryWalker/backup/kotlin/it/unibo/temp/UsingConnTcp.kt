package it.unibo.temp

import org.json.JSONObject
import java.io.PrintWriter
import java.net.Socket

class UsingConnTcp {

    companion object {
        private const val localHostName = "localhost"
        public const val containerHostName = "wenv"
        private const val port = 8999
        private const val sep = ";"
        protected var clientSocket: Socket? = null
        protected var outToServer: PrintWriter? = null

        //Just for testing
        /*
        @JvmStatic
        fun main(args: Array<String>) {
            UsingConnTcp().doJob()
        }*/
    }

    @Throws(Exception::class)
    protected fun initClientConn() {
        try { //attempt to connect for a local application
            clientSocket = Socket(localHostName, port)
            outToServer = PrintWriter(clientSocket!!.getOutputStream())
        } catch (e1: Exception) {
            for (i in 1..10) { //See https://docs.docker.com/compose/startup-order/
                try {
                    println("initClientConn attempt $i to connect in container ")
                    clientSocket = Socket(containerHostName, port)
                    outToServer  = PrintWriter(clientSocket!!.getOutputStream())
                    break
                } catch (e: Exception) {
                    println("initClientConn RETRY since ERROR ${e.message } " )
                    Thread.sleep(1000)
                }
            }
        }
    }

    @Throws(Exception::class)
    fun sendCmd(msg1: String) {
        var msg = msg1
        if (outToServer == null) return
        val jsonString = "{ 'type': '$msg', 'arg': 800 }"
        val jsonObject = JSONObject(jsonString)
        msg = sep + jsonObject.toString() + sep
        println("sending msg=$msg")
        outToServer!!.println(msg)
        outToServer!!.flush()
    }

    fun mbotForward() {
        try {
            sendCmd("moveForward")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun mbotBackward() {
        try {
            sendCmd("moveBackward")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun mbotLeft() {
        try {
            sendCmd("turnLeft")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun mbotRight() {
        try {
            sendCmd("turnRight")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun mbotStop() {
        try {
            sendCmd("alarm")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doJob() {
        try {
            //Thread.sleep(5000);
            println("STARTING ... ")
            initClientConn()
            mbotForward()
            Thread.sleep(1000)
            mbotBackward()
            Thread.sleep(1000)
            /*
			mbotLeft() ;
			Thread.sleep(1000);
			mbotForward();
			Thread.sleep(1000);
			mbotRight(  ) ;
			Thread.sleep(1000);
			mbotForward();
			Thread.sleep(1000);
			 */mbotStop()
            //Thread.sleep(5000);	//avoids premature termination (in docker-compose)
            println("END")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun main(args: Array<String>) {
    val sys = UsingConnTcp()
    sys.doJob()
}

