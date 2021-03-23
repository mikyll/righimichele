/*
===============================================================
wsclientBoundaryToWenv.js
    moves along the room boundary using websocket
===============================================================
*/
const WebSocketClient  = require('websocket').client;
const walkLogic        = require('./wsclientBoundaryLogic');
const moveRobot        = require('./wsclientBoundaryLogic').cmdRobot;

var client             = new WebSocketClient();

function elabMoveResponse( crilJsonMsg, connection  ){
        console.log( crilJsonMsg  )
        if(crilJsonMsg.collision) { //{ collision: true, move: 'unknown' }
            console.log("collision=" + crilJsonMsg.collision)
            return
        }
        if(crilJsonMsg.sonarName){ //{ sonarName: 'sonar2', distance: 19, axis: 'x' }
            console.log("sonar=" + crilJsonMsg.sonarName + " distance=" + crilJsonMsg.distance)
            return
        }
        if(crilJsonMsg.endmove){
            //crilMsg: {"endmove":"...","move":"..."}
            walkLogic.walkBoundary(crilJsonMsg, connection) //code that works also within a HTML page
        }
}

    client.on('connectFailed', function(error) {
        console.log('Connect Error: ' + error.toString());
    });

    client.on('connect', function(connection) {
        console.log('WebSocket Client Connected')
        moveRobot( "moveForward", 400, connection )

        connection.on('error', function(error) {
            console.log("Connection Error: " + error.toString());
        });
        connection.on('close', function() {
            console.log('Connection Closed');
        });
        connection.on('message', function(msg){
        //msg: { type: 'utf8', utf8Data: '{...}' }
            if (msg.type === 'utf8') {
                const jsonMsg = JSON.parse( msg.utf8Data )
                elabMoveResponse( jsonMsg, connection )
            }
        })
    });

client.connect('ws://localhost:8091', '');


