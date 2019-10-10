# cordova-plugin-nearby-connections
Supports Android [Nearby Connections](https://developers.google.com/nearby/connections/overview) API.


This plugin is a WIP. Not yet functional.
 
## Example of what this might support
 
Let's say the application has two modes that a device can operate in:
- Aggregator: Advertises itself as the aggregation service and performs a data sync between itself and a group of Peers.
- Peer: Advertises itself as a peer that can be sync'd by the master

The Aggregator discovers a list of the peers and begins the data sync with each peer. Since this application embraces 
eventual consistency, it must make two passes through this list to make sure all peers have data that has been collected by the aggregator.

## A simpler test

1. Device 2 advertises as a service (possibly have an option for requiring confirmation). 
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
const service = new window.cordova.NearbyConnectionsPlugin.Service()
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
const serviceManager = new window.cordova.NearbyConnectionsPlugin.serviceManager()
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
