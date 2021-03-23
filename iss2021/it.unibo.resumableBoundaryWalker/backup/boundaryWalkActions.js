/*
===============================================================
boundaryWalkActions.js
===============================================================
*/
var connSupport = null
function doBoundary( support  ){
     connSupport = support
     domove( "moveForward", 1, goon, ko)
}

function goon(numOfSteps){
     domove("moveForward", numOfSteps, ahead, ko)
}

function ko(numOfSteps){
    //console.log("ko numOfSteps=" + numOfSteps  )
    if( numOfSteps++ < 4 )  domove("turnLeft", numOfSteps, goon, ko)
    else  domove("turnLeft", numOfSteps, terminate, terminate)  //just to return to initial state
}

    function doMove( move, time ) {
        const moveJson = '{"robotmove":"'+ move +'"'+', "time":'+time+'}'
        console.log("doMove moveJson:" + moveJson);
        if (connSupport ) { connSupport.send(moveJson) }
    }

module.exports = { doBoundary }

