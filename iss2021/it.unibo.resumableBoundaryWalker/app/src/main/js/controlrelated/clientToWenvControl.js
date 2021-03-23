/*
clientToWenvControl.js
===============================================================
Use a socket on 3000 to send a command to webguiServer
and then to work as an observer
===============================================================
*/
const WebSocketClient = require('websocket').client;

var client = new WebSocketClient();

client.on('connectFailed', function(error) {
    console.log('Connect Error: ' + error.toString());
});

client.on('connect', function(connection) {
    console.log('WebSocket Client Connected');
    connection.on('error', function(error) {
        console.log("Connection Error: " + error.toString());
    });
    connection.on('close', function() {
        console.log('echo-protocol Connection Closed');
    });
    connection.on('message', function(message) {
        if (message.type === 'utf8') {
            console.log("Received: '" + message.utf8Data + "'");
        }
    });

    function doMove(move) {
        if (connection.connected) {
            //var number = Math.round(Math.random() * 0xFFFFFF);
            //connection.sendUTF(number.toString());
            //setTimeout(sendNumber, 1000);
            connection.send(move)
        }
    }
    doMove("turnLeft");
});

client.connect('ws://localhost:3000 ', ''); //'echo-protocol'


