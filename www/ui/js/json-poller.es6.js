class JsonPoller {

  constructor(url, callback, errorCallback, timeout=1000) {
    this.url = url;
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.timeout = timeout;
    this.requestCount = 0;
    this.fetchData();
  }

  fetchData() {
    this.requestCount++;
    //  + "&rand="+Math.round(Math.random() * 999999)
    fetch(this.url)
      .then((response) => {
        return response.json();
      }).then((jsonData) => {
        requestAnimationFrame(() => {  // detach from fetch Promise to prevent Error-throwing
          this.callback(jsonData);
        });
        setTimeout(() => {
          this.fetchData();
        }, this.timeout);
        // requestAnimationFrame(() => {  // detach from fetch Promise to prevent Error-throwing
        //   this.callback(jsonData);
        //   this.fetchData();
        // });
      }).catch((error) => {
        this.errorCallback(error);
        setTimeout(() => {
          this.fetchData();
        }, this.timeout);
      });
  }

}
