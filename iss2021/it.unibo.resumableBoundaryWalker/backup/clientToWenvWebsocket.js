/*
clientToWenvWebsocket.js
===============================================================
    performs forward - backward
    and then works as an observer
        rings a bell if there is a collision
===============================================================
*/
const WebSocketClient = require('websocket').client;

var client     = new WebSocketClient();
var numOfSteps = 1
var moves      = "w"
var connection = null

    function doMove( move ) {
        const moveJson =  {robotmove : move, time : 600}
        const moveStr  = JSON.stringify(moveJson)
        console.log("doMove connection:" + connection + " moveStr=" + moveStr);
        if (connection) { connection.send( moveStr ) }
    }

function elabMsg( msg   ){
console.log("clientToWenvWebsocket  | elabMsg msg:"  )
console.log( msg)

                if(msg.collision) {
                   console.log("Received: collision=" + msgJson.collision)
                   console.log('\u0007')  //RING THE BELL
                   connection.close()   //send 'disconnect'
                }
                if(msg.sonarName){
                   console.log("Received: sonar=" + msgJson.sonarName + " distance=" + msgJson.distance)
                }

    const msgJson = msg //JSON.parse( msg )
    if( msgJson.endmove && msgJson.move == "moveForward"){  //moveForward DONE OK
        doMove( "moveForward"  )
        return "ahead"
    }
    if( msgJson.endmove && msgJson.move == "turnLeft"){ //turnLeft DONE OK
        doMove( "moveForward"  )
        return "ahead"
    }
    if(msgJson.collision) { //FOUND OBSTACLE
       doMove( "turnLeft"  )
       return "turned"
    }
    if(msgJson.sonarName){
        console.log("Received: sonar=" + msgJson.sonarName + " distance=" + msgJson.distance)
        return "sonar"
    }
}

function doJob(connection){
     doMove( "moveForward"  )
     setTimeout( () => {
        doMove( "moveBackward"  );
        console.log("now working as an observer  ... " );
     }, 1000 )
}

    client.on('connect', function(myconnection) {
        console.log('Client Connected')
        //doJob(connection)
        connection = myconnection

                connection.on('error', function(error) {
                    console.log("Connection Error: " + error.toString());
                });
                connection.on('close', function() {
                    console.log('Connection Closed');
                });
                connection.on('message', function(message) {
                    if (message.type === 'utf8') {
                        const msg = message.utf8Data
                        console.log( msg  )
                        elabMsg( msg  )
                        //const msgJson = JSON.parse( msg )
                        //if(msgJson.endmove) { console.log("Received: endmove=" + msgJson.endmove) }
                    }
                });
    });

    client.on('connectFailed', function(error) {
         console.log('Connect Error: ' + error.toString());
    });

client.connect('ws://localhost:8091', ''); //'echo-protocol'



