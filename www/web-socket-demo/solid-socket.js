var SolidWebsocket = function(wsAddress, openCallback, messageCallback, errorCallback, closeCallback) {
  // connect
  var _socket = new WebSocket(wsAddress);
  addSocketListeners();

  // auto-reconnect monitor
  var lastConnectAttemptTime = Date.now();
  function wsMonitor() {
    if(_socket.readyState != WebSocket.OPEN && Date.now() - lastConnectAttemptTime > 2000) {
      // console.log('Attempting to reconnect to Websocket');
      // clean up busted socket object
      _socket.onopen = _socket.onmessage = _socket.onerror = _socket.onclose = null;
      //reinitialize
      try{
        _socket = new WebSocket(wsAddress);
        addSocketListeners();
      } catch(err) {
        console.log('Websocket connect error: ', err);
      }
      lastConnectAttemptTime = Date.now();
    }
    requestAnimationFrame(wsMonitor);
  }
  requestAnimationFrame(wsMonitor);

  function addSocketListeners() {
    if(openCallback) _socket.onopen = openCallback;
    if(messageCallback) _socket.onmessage = messageCallback;
    if(errorCallback) _socket.onerror = errorCallback;
    if(closeCallback) _socket.onclose = closeCallback;
  }

  return {
    socket: function() { return _socket; },
    isConnected: function() { return _socket.readyState == WebSocket.OPEN; }
  }
};
