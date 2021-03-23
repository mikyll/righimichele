/*
===============================================================
wsclientBoundaryLogic.js
used also in a webpage (see wsclientToWenv.html)
===============================================================
*/
var  numOfSteps = 1
var  moves      = ""
var isInPage    = ( typeof location  !=  'undefined' )

    function cmdRobot(move, time, connection) {
        const moveJson = '{"robotmove":"'+ move +'"'+', "time":'+time+'}'
        console.log("cmdRobot moveJson:" + moveJson + " isInPage=" + isInPage + " conn=" + connection);
        if (connection ) { connection.send(moveJson) }
    }

function walkBoundary( crilJsonMsg, connection ){
    console.log("wsclientBoundaryLogic | walkBoundary numOfSteps=" + numOfSteps + " p=" + moves)
    console.log( crilJsonMsg  )
    //handle endmove => do boundary walk
    console.log( crilJsonMsg.move  )
    if( crilJsonMsg.move == "turnLeft"){
        if( numOfSteps < 4 ) {
             numOfSteps++
             moves = moves + "l"
             cmdRobot("moveForward", 400, connection)
        }else if( numOfSteps++ >= 4 ){
            moves = moves + "l"
            console.log("Boundary explored; moves=" + moves  )
            connection.close()
        }
       return moves
    }//turnLeft
    //answer to moveForward
    if( crilJsonMsg.endmove == 'true' ){
        moves = moves + "w"
        cmdRobot("moveForward", 400, connection)
    }
    else cmdRobot("turnLeft", 300, connection)
    return moves
}

if ( typeof location ==  'undefined'){  //to allow loading in a HTML page. See wsclientBoundary.html
    exports.walkBoundary = walkBoundary;
    exports.cmdRobot     = cmdRobot;
}

