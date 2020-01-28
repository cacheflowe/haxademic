//////////////////////////
// globals
//////////////////////////
var debug = document.getElementById('debug');
var MAX_LOGS = 20;

//////////////////////////
// WEBSOCKET SETUP
//////////////////////////

function onOpen(event) {
  solidSocket.sendMessage(JSON.stringify({
    event: "connect",
    machine: "machine-id",
    date: ""+Date.now()
  }));
};

function onMessage(event) {
  if(!event.data) return;
  var jsonData = window.JSON.parse(event.data);
  checkJSON(jsonData);
};

const socketAddress = "ws://"+document.location.hostname+":3001";
const solidSocket = new SolidSocket(socketAddress);
solidSocket.setOpenCallback(onOpen);
solidSocket.setMessageCallback(onMessage);

//////////////////////////
// WEBSOCKET SEND DATA
//////////////////////////

function sendJsonData(data) {
  solidSocket.sendMessage(JSON.stringify(data));
};

// listen for button clicks with [json-data]
document.body.addEventListener('click', function(e) {
  // check for button clicks. preventDefault behavior, which might submit the form
  if(e.target && e.target.nodeName.toLowerCase() == 'button' && e.target.hasAttribute('data-json')) {
    e.preventDefault();
    let jsonData = JSON.parse(e.target.getAttribute('data-json'));
    sendJsonData(jsonData);
  }
});


//////////////////////////
// WEBSOCKET INCOMING EVENTS
//////////////////////////

function checkJSON(jsonData) {
  // console.log(jsonData, jsonData['event']);
  var div = null;
  if(jsonData['store'] && jsonData['type']) {
    div = document.createElement('code');
    div.innerHTML = JSON.stringify(jsonData);
    div.style.backgroundColor = "#5ED932";
  }
  // check for specific message data
  else if(jsonData['event'] && jsonData['event'] == 'heartbeat') {
    // add event to screen
    div = document.createElement('code');
    div.innerHTML = JSON.stringify(jsonData);
  }
  else if(jsonData['event'] && jsonData['event'] == 'WEB_EVENT') {
    div = document.createElement('code');
    div.innerHTML = JSON.stringify(jsonData);
    if(jsonData['command'] != null) {
      if(jsonData['command'] == 'start') div.style.backgroundColor = "#36B1BF";
      if(jsonData['command'] == 'stop') div.style.backgroundColor = "#D95E32";
    }
  }
  else {
    div = document.createElement('code');
    div.innerHTML = JSON.stringify(jsonData);
  }
  if(div != null) {
    // div.style.color = '#ffffff';
    div.style.padding = '2px 10px';
    let container = document.createElement('div');
    container.appendChild(div);
    document.getElementById('debug').appendChild(container);
  }
  // cut off log length
  while(debug.childNodes.length > MAX_LOGS) {
    var el = debug.childNodes[0];
    el.parentNode.removeChild(el);
  }
}

//////////////////////////
// EXTRA CREDIT: ACCELEROMETER
// ... requires an https server, which then requires a wss server, which isn't part of the plan here :(
//////////////////////////

/*
var gn = new GyroNorm();
gn.init().then(function(){
  gn.start(function(data){
    sendJsonData({
      rotX: data.do.beta,
      rotY: data.do.alpha,
      rotZ: data.do.gamma,
    });
    // Process:
    // data.do.alpha	( deviceorientation event alpha value )
    // data.do.beta		( deviceorientation event beta value )
    // data.do.gamma	( deviceorientation event gamma value )
    // data.do.absolute	( deviceorientation event absolute value )

    // data.dm.x		( devicemotion event acceleration x value )
    // data.dm.y		( devicemotion event acceleration y value )
    // data.dm.z		( devicemotion event acceleration z value )

    // data.dm.gx		( devicemotion event accelerationIncludingGravity x value )
    // data.dm.gy		( devicemotion event accelerationIncludingGravity y value )
    // data.dm.gz		( devicemotion event accelerationIncludingGravity z value )

    // data.dm.alpha	( devicemotion event rotationRate alpha value )
    // data.dm.beta		( devicemotion event rotationRate beta value )
    // data.dm.gamma	( devicemotion event rotationRate gamma value )
  });
}).catch(function(e){
  alert('FAIL');
  console.log(e);
  alert(e.toString());
  // Catch if the DeviceOrientation or DeviceMotion is not supported by the browser or device
});
*/