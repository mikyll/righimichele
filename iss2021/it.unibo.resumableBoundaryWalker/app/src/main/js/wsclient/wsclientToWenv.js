/*
===============================================================
wsclientToWenv.js
    performs forward - backward
    and then works as an observer
        rings a bell if there is a collision
===============================================================
*/
const WebSocketClient  = require('websocket').client;

var client      = new WebSocketClient();
var conn8091    = null
var jobCounter  = 0

    function doMove(move, time) {
        const moveJson = '{"robotmove":"'+ move +'"'+', "time":'+time+'}'
        console.log("doMove moveJson:" + moveJson);
        if (conn8091) { conn8091.send(moveJson) }
    }

function doJob(){
     doMove( "moveForward", 800 )
     setTimeout( () => {
        doMove( "moveBackward", 800 );
        console.log("now working as an observer  ... " );
        //otherwise ...
        //conn8091.close()
	 }, 800 )
}

function elabMoveResponse( crilJsonMsg  ){
        console.log( crilJsonMsg  )
        if(crilJsonMsg.collision) console.log("collision=" + crilJsonMsg.collision)
        if(crilJsonMsg.sonarName){
            console.log('\u0007');  //RING THE BELL
            console.log("sonar=" + crilJsonMsg.sonarName + " distance=" + crilJsonMsg.distance)
        }
        if(crilJsonMsg.endmove){
            //crilMsg: {"endmove":"...","move":"..."}
            //if( jobCounter++ < 1) doJob()
        }
}

    client.on('connectFailed', function(error) {
        console.log('Connect Error: ' + error.toString());
    });

    client.on('connect', function(connection) {
        console.log('WebSocket Client Connected')
        conn8091 = connection
        doJob()

        connection.on('error', function(error) {
            console.log("Connection Error: " + error.toString());
        });
        connection.on('close', function() {
            console.log('Connection Closed');
        });
        connection.on('message', function(msg){
        //msg: { type: 'utf8', utf8Data: '{"endmove":"true","move":"turnLeft"}' }
            if (msg.type === 'utf8') {
                const jsonMsg = JSON.parse( msg.utf8Data )
                elabMoveResponse( jsonMsg )
            }
        })
    });

client.connect('ws://localhost:8091', '');
//doJob()       //WARNING: not here, since connect returns the control immediately


