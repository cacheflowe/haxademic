var lastMonitorTime = Date.now();
document.body.classList.add('no-socket');

function checkSocket() {
  if(Date.now() > lastMonitorTime + 500) {
    if(_store.solidSocket) {
      if(_store.solidSocket.socket && _store.solidSocket.socket.readyState == WebSocket.OPEN) {
        document.body.classList.add('has-socket');
        document.body.classList.remove('no-socket');
      } else {
        document.body.classList.add('no-socket');
        document.body.classList.remove('has-socket');
      }
    }
    lastMonitorTime = Date.now();
  }
  requestAnimationFrame(checkSocket);
}
checkSocket();
