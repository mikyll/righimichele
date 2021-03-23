/*
axiosClientToWenv.js
===============================================================
walks along the boundary by using callbacks, no global state
===============================================================
*/
const axios    = require('axios')
const URL      = 'http://localhost:8090/api/move' ;
var numOfSteps = 0

function ahead(numOfSteps){
     domove("moveForward", numOfSteps, goon, ko)
}

function handleCollision(){
    if( numOfSteps++ < 4 )  domove("turnLeft", numOfSteps, goon, ko)
    else  domove("turnLeft", numOfSteps, terminate, terminate)  //just to return to initial state
}

function goon(numOfSteps){
     domove("moveForward", numOfSteps, ahead, ko)
}

function ko(numOfSteps){
    //console.log("ko numOfSteps=" + numOfSteps  )
    if( numOfSteps++ < 4 )  domove("turnLeft", numOfSteps, goon, ko)
    else  domove("turnLeft", numOfSteps, terminate, terminate)  //just to return to initial state
}

function terminate(){
    console.log("Buoundary explored"   )
}

//------------------------------------------------------------
function domove(move, numOfSteps, callbackOk, callbackCollision)  {
    axios({
            url: URL,
            data: { robotmove: move, time: 400 },
            method: 'POST',
            timeout: 900,
            headers: { 'Content-Type': 'application/json' }
    }).then(response => {   //continues when the action has been done
        //console.log("axiosClientToWenv domove | response.data: " )
        var answer    = response.data   //JSON obj { endmove: 'true', move: 'moveForward' }
        console.log(  answer )
        if( answer.endmove == 'true' ) callbackOk(numOfSteps)
        else callbackCollision(numOfSteps)
  })
  .catch(error => {
    console.log(error)
  })
}

//main
ahead(1)