# cordova-plugin-nearby-connections
 Supports Google Nearby Connections API
 
 This plugin is a WIP. Not yet functional.
 
 Example of what this might support...

1. Device 2 advertises as a service (possibly have an option for requiring confirmation)
2. Device 1 gets list of nearby advertised services 
3. Device 1 sends connection request to Device 2 (?)
4. Device 2 accepts connection request (?)
5. Device 1 sends a message to Device 2
6. Device 2 receives message from Device 1
7. Device 2 sends a message to Device 1
8. Device 1 receive message from Device 2

Here's what the API might look like, without connection confirmations for now. This would have Device 2 advertise a service, device 1 would connect and send "ping", device 2 would send "pong", and device 1 would keep this repeating.

From Device 2:
```javascript
const service = new window.cordova.P2PPayloadAPI.Service()
service.advertise()
service.onConnection((connection) => {
  console.log('Connection made!')
  connection.onReceive(() => {
    connection.send('pong')
  })
})
```

From Device 1:
```javascript
const serviceManager = new window.cordova.P2PPaylaodAPI.serviceManager()
serviceManger.getServices()
  .onServiceAvailable((service) => {
    service.makeConnection((connection) => {
      connection.onReceive(() => {
        connection.send('ping')
      })
      connection.send('ping')
    })
  })

```
