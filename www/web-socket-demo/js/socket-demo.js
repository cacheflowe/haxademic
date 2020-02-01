//////////////////////////
// globals
//////////////////////////
var debug = document.getElementById('debug');
var MAX_LOGS = 20;
var machineId = (document.location.hash.length > 0) ? document.location.hash.substring(1) : "machine-id-in-hash-please"

//////////////////////////
// WEBSOCKET SETUP
//////////////////////////

// callbacks

function onOpen(event) {
  solidSocket.sendMessage(JSON.stringify({
    event: "connect",
    machine: machineId,
    date: ""+Date.now()
  }));
};

function onMessage(event) {
  if(!event.data) return;
  var jsonData = window.JSON.parse(event.data);
  checkJSON(jsonData);
};

// init SolidSocket

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
  // add styling to new logging divs
  if(div != null) {
    // div.style.color = '#ffffff';
    div.style.padding = '2px 10px';
    let container = document.createElement('div');
    container.appendChild(div);
    document.getElementById('debug').appendChild(container);
  }
  // truncate log
  while(debug.childNodes.length > MAX_LOGS) {
    var el = debug.childNodes[0];
    el.parentNode.removeChild(el);
  }
}
