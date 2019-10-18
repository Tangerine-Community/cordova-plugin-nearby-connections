# cordova-plugin-nearby-connections
Supports Android [Nearby Connections](https://developers.google.com/nearby/connections/overview) API.
 
## Example of what this might support
 
Let's say the application has two modes that a device can operate in:
- Aggregator: Advertises itself as the aggregation service and performs a data sync between itself and a group of Peers.
- Peer: Advertises itself as a peer that can be sync'd by the master

The Aggregator discovers a list of the peers and begins the data sync with each peer. Since this application embraces 
eventual consistency, it must make two passes through this list to make sure all peers have data that has been collected by the aggregator.

## Where this API needs to go

Log 10/10/2019: The startAdvertising function in the current endpoint also performs discovery. It populates a list of mDiscoveredEndpoints. 
The onEndpointDiscovered(Endpoint endpoint) method currently stops discovery and population of this list when it finds a peer. 
Next step is to change this behaviour so that a list of all peers is created. I'd like to see startAdvertising return this list of peers, 
which we could display in the UI. Then the Aggregator would loop through this list, connectToEndpoint, transferData, and repeat this process to 
ensure consistency across the peers. Both the aggregator and the peer would run listenForTransfer and confirm that the transfers are successful.

Log 10/18/2019: The startAdvertising function now returns a list of discovered endpoints. (onEndpointDiscovered was disabled.) Documented the Javascript API below.

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

Javascript API

getPermission: Ensures that the necessary permissions are granted. You may need to restart the app once these permissions are granted. (TODO test if that is true.)

```
document.getElementById("getPermission").addEventListener("click", function(){
    window.cordova.plugins.NearbyConnectionsPlugin.getPermission(null,
        function(response) {
            if (response["messageType"] === "log") {
                let logEl = document.querySelector('#log')
                logEl.innerHTML = logEl.innerHTML +  "<p>" + response["message"] + "</p>\n";
            }
        },
        function(error) {
            console.log("error:" + error);
            let logEl = document.querySelector('#log')
            logEl.innerHTML = logEl.innerHTML +  "<p>" + "error:" + error + "</p>\n";
        }
    );
});
```

For most of the js functions, this plugin leaves the plugin callback connection open (`pluginResult.setKeepCallback` is true) 
to pass messages back to the client. 

startAdvertising: To get a list of endpoints, check `if (response["messageType"] === "endpoints")`

Activate startAdvertising on both the device sending data as well as the device(s) receiving data. By inspecting response["messageType"] 
you can see how to process the response from the plugin by the following message types:
 - log: to monitor phases of discovery, connection, and transfer.
 - localEndpointName: useful for displaying the endpoint name of your device
 - endpoints: display a list of endpoints
 - payload: for processing transferred data

```

document.getElementById("startAdvertising").addEventListener("click", function(){
    window.cordova.plugins.NearbyConnectionsPlugin.startAdvertising(null,
        function(response) {

            if (response["messageType"] === "log") {
                let logEl = document.querySelector('#log')
                logEl.innerHTML = logEl.innerHTML +  "<p>" + response["message"] + "</p>\n";
            } else if (response["messageType"] === "localEndpointName") {
                let el = document.querySelector('#localEndpointName')
                el.innerHTML =  "<p>Device Name: " + response["message"] + "</p>\n";
            } else if (response["messageType"] === "endpoints") {
                let el = document.querySelector('#endpoints')
                console.log("endpoints: " + JSON.stringify(response["object"]))
                let endpoints = response["object"]
                for (let [key, value] of Object.entries(endpoints)) {
                    console.log(`${key}: ${value}`);
                    el.innerHTML = `<div id='endpoint_${value}'><button class='endpoint bigText' onclick='connectToEndpoint("${key}","${value}")'>${value}</button></div>`;
                }
            } else if (response.messageType === 'payload') {
                const messageStr = response.message;
                // TODO: JSONObject is available if we need it.
                // const object = response.object;
                PouchDB.plugin(window['PouchReplicationStream'].plugin);
                PouchDB.adapter('writableStream', window['PouchReplicationStream'].adapters.writableStream);
                const writeStream =  new window['Memorystream'];
                writeStream.end(messageStr);
                const dest = new PouchDB('kittens');
                const pluginMessage = 'I loaded data from the peer device.';
                dest.load(writeStream).then(function () {
                    document.querySelector('#log').innerHTML += pluginMessage + '<br/>';
                }).catch(function (err) {
                    let message = 'oh no an error: ' + err;
                    console.log(message);
                    document.querySelector('#log').innerHTML += message + '<br/>';
                });
            }
        },
        function(error) {
            console.log("error:" + error);
            let logEl = document.querySelector('#log')
            logEl.innerHTML = logEl.innerHTML +  "<p>" + "error:" + error + "</p>\n";
        }
    );
});
```

To connect to an endpoint, pass a string that concats the id and name of the enpoint. See the code above (`if (response["messageType"] === "endpoints") `)
to see how to get the id and name from the discovery.

```javascript
let connectToEndpoint = function(id, name){
// let targetId = event.target.id
// let idArray = targetId.split('_')
// let id = idArray[1]
// let name = idArray[2]
    let idName = id + "_" + name;
    window.cordova.plugins.NearbyConnectionsPlugin.connectToEndpoint(idName,
        function(response) {
            if (response["messageType"] === "log") {
                let logEl = document.querySelector('#log')
                logEl.innerHTML = logEl.innerHTML +  "<p>" + response["message"] + "</p>\n";
            } else if (response["messageType"] === "connected") {
                let endpoint = response["object"]
                let endpointName = Object.values(endpoint)[0]
                console.log("endpoint: " + JSON.stringify(response["object"]) + " endpointId: " + endpointName)
                let el = document.querySelector('#endpoint_' + endpointName)
                el.innerHTML = `<button class='endpoint bigText' onclick='transferData("${endpointName}")'>Transfer to ${endpointName}</button>`;
            }
        },
        function(error) {
            console.log("error:" + error);
            let logEl = document.querySelector('#log')
            logEl.innerHTML = logEl.innerHTML +  "<p>" + "error:" + error + "</p>\n";
        }
    );
}
```

transferData: Pass a string to another device. Here is an example of passing data from a Pouch database:

```javascript
let transferData = async function() {
    PouchDB.plugin(window['PouchReplicationStream'].plugin);
    PouchDB.adapter('writableStream', window['PouchReplicationStream'].adapters.writableStream);
    let dumpedString = '';
    const stream = new window['Memorystream']();
    stream.on('data', function(chunk) {
        dumpedString += chunk.toString();
    });
    const source = new PouchDB('kittens');
    var doc = {
        "_id": "mittens",
        "name": "Mittens",
        "occupation": "kitten",
        "age": 3,
        "hobbies": [
            "playing with balls of yarn",
            "chasing laser pointers",
            "lookin' hella cute"
        ]
    };
    try {
        source.put(doc);
    } catch (e) {
        console.log("Already have Mittens:" + e)
    }
    source.dump(stream).then(function () {
        // console.log('Yay, I have a dumpedString: ' + dumpedString);
        window.cordova.plugins.NearbyConnectionsPlugin.transferData(dumpedString, function(message) {
            const objectConstructor = ({}).constructor;
            if (message.constructor === objectConstructor) {
                const messageStr = message.message;
                document.querySelector('#log').innerHTML += messageStr + '<br/>';
            } else {
                console.log('Message: ' + message);
                document.querySelector('#log').innerHTML += message + '<br/>';
            }
        }, function(err) {
            console.log('TangyP2P error:: ' + err);
        });
    }).catch(function (err) {
        console.log('oh no an error', err);
    });
}
```

