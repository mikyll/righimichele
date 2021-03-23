/*
httpClientToWenv.js
===============================================================
walks along the boundary by using callbacks, no global state
===============================================================
*/
const axios    = require('axios')
const URL      = 'http://localhost:8090/api/move' ;
var numOfSteps = 1
var moves      = "w"

function doBoundary( crilMsg  ){
    //console.log("axiosClientToWenv | elabMoveResponse numOfSteps=" + numOfSteps)
    console.log( crilMsg )
    if( crilMsg.move=="turnLeft"){
        if( numOfSteps < 4 ) {
             numOfSteps++
             moves = moves + "l"
             doHttpPost("moveForward")
        }else if( numOfSteps++ >= 4 ){
            console.log("Buoundary explored moves=" + moves  )
        }
       return
    }
    //answer to moveForward
    if( crilMsg.endmove == 'true' ){
        moves = moves + "w"
        doHttpPost("moveForward")
    }
    else doHttpPost("turnLeft")
}

function handleCollision(){
    if( numOfSteps < 4 ){
        doHttpPost("turnLeft" )
    }
    else {
        console.log("Buoundary explored"   )
        doHttpPost("turnLeft")    //to restore direction=south
    }
}

//------------------------------------------------------------
function doHttpPost( move )  {
    axios({
            url: URL,
            data: { robotmove: move, time: 400 },
            method: 'POST',
            timeout: 900,
            headers: { 'Content-Type': 'application/json' }
    }).then(response => {   //continues when the action has been done
        doBoundary(response.data)
    })
    .catch(error => {
    console.log(error)
    })
}



//main
doHttpPost("moveForward")