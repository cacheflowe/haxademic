class SolidSocket {

  constructor(wsAddress) {
    this.wsAddress = wsAddress;
    this.socket = new WebSocket(wsAddress);
    this.addSocketListeners();
    this.lastConnectAttemptTime = Date.now();
    this.startMonitoringConnection();
  }

  // WebSocket LISTENERS

  addSocketListeners() {
    this.openHandler = this.onOpen.bind(this);
    this.socket.addEventListener('open', this.openHandler);
    this.messageHandler = this.onMessage.bind(this);
    this.socket.addEventListener('message', this.messageHandler);
    this.errorHandler = this.onError.bind(this);
    this.socket.addEventListener('error', this.errorHandler);
    this.closeHandler = this.onClose.bind(this);
    this.socket.addEventListener('close', this.closeHandler);
  }

  removeSocketListeners() {
    this.socket.removeEventListener('open', this.openHandler);
    this.socket.removeEventListener('message', this.messageHandler);
    this.socket.removeEventListener('error', this.errorHandler);
    this.socket.removeEventListener('close', this.closeHandler);
    this.socket.close();
  }

  // CALLBACKS

  onOpen(e) {
    if(this.openCallback) this.openCallback(e);
  }

  setOpenCallback(callback) {
    this.openCallback = callback;
  }

  onMessage(e) {
    if(this.messageCallback) this.messageCallback(e);
  }

  setMessageCallback(callback) {
    this.messageCallback = callback;
  }

  onError(e) {
    if(this.errorCallback) this.errorCallback(e);
  }

  setErrorCallback(callback) {
    this.errorCallback = callback;
  }

  onClose(e) {
    if(this.closeCallback) this.closeCallback(e);
  }

  setCloseCallback(callback) {
    this.closeCallback = callback;
  }

  // SEND

  sendMessage(message) {
    this.socket.send(message);
  }

  // MONITORING & RECONNECTION

  startMonitoringConnection() {
    this.checkConnection();
  }

  checkConnection() {
    if(this.socket.readyState != WebSocket.OPEN && this.socket.readyState != WebSocket.CONNECTING && Date.now() > this.lastConnectAttemptTime + SolidSocket.RECONNECT_INTERVAL) {
      // clean up failed socket object
      this.removeSocketListeners();
      // initialize a new socket object
      try{
        this.socket = new WebSocket(this.wsAddress);
        this.addSocketListeners();
      } catch(err) {
        console.log('Websocket couldn\'t connect: ', err);
      }
      this.lastConnectAttemptTime = Date.now();
    }
    requestAnimationFrame(() => this.checkConnection());
  }

}

SolidSocket.RECONNECT_INTERVAL = 2000;
