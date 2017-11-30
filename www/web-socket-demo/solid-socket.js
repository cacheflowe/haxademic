'use strict';

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var SolidSocket = function () {
  function SolidSocket(wsAddress) {
    _classCallCheck(this, SolidSocket);

    this.wsAddress = wsAddress;
    this.socket = new WebSocket(wsAddress);
    this.addSocketListeners();
    this.lastConnectAttemptTime = Date.now();
    this.startMonitoringConnection();
  }

  // WebSocket LISTENERS

  _createClass(SolidSocket, [{
    key: 'addSocketListeners',
    value: function addSocketListeners() {
      this.openHandler = this.onOpen.bind(this);
      this.socket.addEventListener('open', this.openHandler);
      this.messageHandler = this.onMessage.bind(this);
      this.socket.addEventListener('message', this.messageHandler);
      this.errorHandler = this.onError.bind(this);
      this.socket.addEventListener('error', this.errorHandler);
      this.closeHandler = this.onClose.bind(this);
      this.socket.addEventListener('close', this.closeHandler);
    }
  }, {
    key: 'removeSocketListeners',
    value: function removeSocketListeners() {
      this.socket.removeEventListener('open', this.openHandler);
      this.socket.removeEventListener('message', this.messageHandler);
      this.socket.removeEventListener('error', this.errorHandler);
      this.socket.removeEventListener('close', this.closeHandler);
      this.socket.close();
    }

    // CALLBACKS

  }, {
    key: 'onOpen',
    value: function onOpen(e) {
      if (this.openCallback) this.openCallback(e);
    }
  }, {
    key: 'setOpenCallback',
    value: function setOpenCallback(callback) {
      this.openCallback = callback;
    }
  }, {
    key: 'onMessage',
    value: function onMessage(e) {
      if (this.messageCallback) this.messageCallback(e);
    }
  }, {
    key: 'setMessageCallback',
    value: function setMessageCallback(callback) {
      this.messageCallback = callback;
    }
  }, {
    key: 'onError',
    value: function onError(e) {
      if (this.errorCallback) this.errorCallback(e);
    }
  }, {
    key: 'setErrorCallback',
    value: function setErrorCallback(callback) {
      this.errorCallback = callback;
    }
  }, {
    key: 'onClose',
    value: function onClose(e) {
      if (this.closeCallback) this.closeCallback(e);
    }
  }, {
    key: 'setCloseCallback',
    value: function setCloseCallback(callback) {
      this.closeCallback = callback;
    }

    // SEND

  }, {
    key: 'sendMessage',
    value: function sendMessage(message) {
      this.socket.send(message);
    }

    // MONITORING & RECONNECTION

  }, {
    key: 'startMonitoringConnection',
    value: function startMonitoringConnection() {
      this.checkConnection();
    }
  }, {
    key: 'checkConnection',
    value: function checkConnection() {
      var _this = this;

      if (this.socket.readyState != WebSocket.OPEN && this.socket.readyState != WebSocket.CONNECTING && Date.now() > this.lastConnectAttemptTime + SolidSocket.RECONNECT_INTERVAL) {
        // clean up failed socket object
        this.removeSocketListeners();
        // initialize a new socket object
        try {
          this.socket = new WebSocket(this.wsAddress);
          this.addSocketListeners();
        } catch (err) {
          console.log('Websocket couldn\'t connect: ', err);
        }
        this.lastConnectAttemptTime = Date.now();
      }
      requestAnimationFrame(function () {
        return _this.checkConnection();
      });
    }
  }]);

  return SolidSocket;
}();

SolidSocket.RECONNECT_INTERVAL = 2000;
