class PageVisibility {

  constructor(activeCallback, inactiveCallback) {
    this.activeCallback = activeCallback || null;
    this.inactiveCallback = inactiveCallback || null;
    this.getPrefix();
    this.initPageVisibilityApi();
  }

  // from: http://www.sitepoint.com/introduction-to-page-visibility-api/
  getPrefix() {
    this.prefix = null;
    if (document.hidden !== undefined)
      this.prefix = "";
    else {
      var browserPrefixes = ["webkit","moz","ms","o"];
      // Test all vendor prefixes
      for(var i = 0; i < browserPrefixes.length; i++) {
        if (document[browserPrefixes[i] + "Hidden"] != undefined) {
          this.prefix = browserPrefixes[i];
          break;
        }
      }
    }
  }

  updateState(e) {
    if (document.hidden === false || document[this.prefix + "Hidden"] === false) {
      if(this.activeCallback != null) this.activeCallback();
    } else {
      if(this.inactiveCallback != null) this.inactiveCallback();
    }
  }

  initPageVisibilityApi() {
    if (this.prefix === null)
      console.log( "Your browser does not support Page Visibility API");
    else {
      document.addEventListener(this.prefix + "visibilitychange", (e) => this.updateState(e));
    }
  }

}
