/*
boundaryReactiveWebsocket.js
===============================================================
    walks along the boundary of the room
    and then works as an observer
===============================================================
*/
const WebSocketClient       = require('websocket').client;
const { walkAlongBoundary } = require('./tripBoundaryBusinessLogic')

var client     = new WebSocketClient();
var tripdone   = false

    function initMoving(connection){
         isObserver   = false
         doMove( "moveForward", connection )
     }

    function doMove(move, connection) {
        //const moveJson = '{"robotmove":"'+ move +'"}'
        const moveJson = {robotmove : move, time : 600}
        const moveStr  = JSON.stringify(moveJson)
        console.log("doMove moveJson:" + moveStr);
        if (connection) { connection.send(moveStr) }
    }


 /*
 ----------------------------------------------------------
 REACTIVE PART
 ----------------------------------------------------------
 */
    client.on('connect', function(connection) {
        console.log('boundaryReactiveWebsocket | Client Connected')
        initMoving( connection )     //START moving

        connection.on('error', function(error) {
            console.log("Connection Error: " + error.toString());

        });

        connection.on('close', function() {
            console.log('Connection Closed');
        });

        connection.on('message', function(msg) {
        //message { type: 'utf8', utf8Data: '{"endmove":true,"move":"moveForward"}' }
        if (msg.type === 'utf8') {
            if( tripdone ) console.log("boundaryReactiveWebsocket as OBSERVER | " + msg.utf8Data   )
            else   tripdone = walkAlongBoundary(msg.utf8Data, connection, doMove)
        }
    });
    client.on('connectFailed', function(error) {
        console.log('Connect Error: ' + error.toString());
    });

});


client.connect('ws://localhost:8091', ''); //'echo-protocol'



