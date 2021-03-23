const WebSocket = require('ws')

const ws = new WebSocket.Server({ port: 8070 })

ws.on('connection', (ws) => {
  ws.on('message', (message) => {
    console.log(`Received message => ${message}`)
  })
  ws.send('ho!')
})