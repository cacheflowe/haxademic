class JsonPoller {

  constructor(url, callback, errorCallback) {
    this.url = url;
    this.callback = callback;
    this.errorCallback = errorCallback;
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
          this.fetchData();
        });
      }).catch((error) => {
        this.errorCallback(error);
        this.fetchData();
      });
  }

}
