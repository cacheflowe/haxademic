class AppStoreDistributed extends AppStore {

  constructor(socketServerUrl) {
    super();
    // init websock connection
    this.solidSocket = new SolidSocket(socketServerUrl);
    this.solidSocket.setOpenCallback((e) => this.onOpen(e));
    this.solidSocket.setMessageCallback((e) => this.onMessage(e));
  }

  onOpen() {
    console.log('AppStoreDistributed connected!');
  }

  onMessage(event) {
    let jsonData = JSON.parse(event.data);
    if(jsonData['store'] && jsonData['type']) {
      this.set(jsonData['key'], jsonData['value']);
    }
  };

  set(key, value, broadcast) {
    super.set(key, value);
    if(broadcast) {
      // todo: check types: string, number, boolean, json
      let data = {
        key: key,
        value: value,
        store: true,
        type: isNaN(value) ? "string" : "number"
      };
      this.solidSocket.sendMessage(JSON.stringify(data));
    }
  }

  // var sendJsonData = function (data) {
  //   solidSocket.sendMessage(JSON.stringify(data));
  // };


}