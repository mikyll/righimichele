/*
boundaryReactiveWs.js
https://www.npmjs.com/package/ws
===============================================================
    walks along the boundary of the room
    and then works as an observer
===============================================================
*/
const WebSocket             = require('ws')
const { walkAlongBoundary } = require('./tripBoundaryBusinessLogic')

const url        = 'ws://localhost:8091'    
var tripdone     = false

const connection = new WebSocket(url)

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
connection.onopen = () => {
  console.log("wsBoundaryReactive | opened")       
  initMoving( connection )
}
connection.onmessage = (msg) => {
    if( tripdone ) {
        console.log("wsBoundaryReactive tripdone | BYE "    )
        connection.close()  //send 'disconnect'
    }
    else   tripdone = walkAlongBoundary(msg.data, connection, doMove)
 }

connection.onerror = (error) => {
  console.log(`WebSocket error: ${error}`)
  console.log( error.message )
}





