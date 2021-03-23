/*
tripBoundaryBusinessLogic.js
===============================================================================
Business logic for a robot that walks along a boundary in a event-driven way
abstracting from the technical details about (websocket) connections
===============================================================================
*/
 /*
BUSINESS LOGIC
*/
var stepCount = 0
function walkAlongBoundary( msg, connection, doMove ){
        var answer = elabMsg(msg, connection, doMove )
        console.log("walkAlongBoundary | answer=" + answer)
        if( answer == "turned") stepCount++
        if( stepCount == 4)  console.log("walkAlongBoundary | AT HOME AGAIN"  )
        return stepCount == 4
}

function elabMsg(msg, connection, doMove  ){
console.log("walkAlongBoundary | elabMsg msg="  )
console.log( msg)
    const msgJson = JSON.parse( msg )
    if( msgJson.endmove && msgJson.move == "moveForward"){  //moveForward DONE OK
        doMove( "moveForward", connection )
        return "ahead"
    }
    if( msgJson.endmove && msgJson.move == "turnLeft"){ //turnLeft DONE OK
        doMove( "moveForward", connection )
        return "ahead"
    }
    if(msgJson.collision) { //FOUND OBSTACLE
       doMove( "turnLeft", connection )
       return "turned"
    }
    if(msgJson.sonarName){
        console.log("Received: sonar=" + msgJson.sonarName + " distance=" + msgJson.distance)
        return "sonar"
    }
}

module.exports = { walkAlongBoundary }

