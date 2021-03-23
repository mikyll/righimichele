/*
WEnvHTTPSupport.java
===============================================================
===============================================================
*/
package it.unibo.interaction

import mapRoomKotlin.mapUtil

val support = WEnvHTTPSupport("localhost:8090")

fun boundary() {
        try {
            println("STARTING boundary ... ")
            for (i in 1..4) {
                var b = false
                while (!b) {
                    b = support.moveForward(300)
                    Thread.sleep(600)
                }
                support.moveLeft()
                Thread.sleep(300)
            }
            println("END")
        } catch (e: Exception) {
            println("ERROR " + e.message)
        }
    }

     fun testForwardBackward() {
        try {
            println("STARTING testForwardBackward ... ")
            var collision = false
            while (!collision) {
                Thread.sleep(500)
                collision = support.moveForward()
            }
            //Return to home
            Thread.sleep(1000)
            collision = false
            while (!collision) {
                collision = support.moveBackward()
                Thread.sleep(500)
            }
            println("END testForwardBackward")
        } catch (e: Exception) {
            println("ERROR " + e.message)
        }
    }

     fun testMoveLeft() {
        try {
            println("STARTING test ... ")
            val b = support.moveLeft()
            println("END")
        } catch (e: Exception) {
            println("ERROR " + e.message)
        }
    }

fun main(args: Array<String>) {
     //testMoveLeft();
     //testForwardBackward();
     boundary()
}