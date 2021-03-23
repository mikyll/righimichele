/*
aclientws.js
*/

const WebSocket = require('ws')
const url        = 'ws://localhost:8091'   //3001 8070
const connection = new WebSocket(url)

connection.onopen = () => {
  //connection.send('turnLeft')
  console.log(`aclientws | opened`)
}

connection.onerror = (error) => {
  console.log(`WebSocket error: ${error}`)
  console.log( error.message )
}

connection.onmessage = (e) => {
  console.log(e.data)
}