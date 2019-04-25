class AppStoreDebug {

  constructor(showing=false) {
    this.showing = showing;
    this.buildElement();
    _store.addListener(this);
    this.initKeyListener();
  }

  initKeyListener() {
    window.addEventListener('keyup', (e) => {
      var key = e.keyCode ? e.keyCode : e.which;
      // console.log('key', key);
      if(key == 32) {
        this.showing = !this.showing;
      }
      if(this.showing == false) {
        this.container.innerHTML = '';
      } else {
        this.printStore();
      }
    });
  }

  buildElement() {
    this.container = document.createElement( 'div' );
    this.container.style.cssText = 'position:fixed;top:0;left:0;height:100%;overflow-y:scroll;opacity:0.9;z-index:9999;background:rgba(255,255,255,0.8);color:#000 !important;';
    document.body.appendChild(this.container);
  }

  storeUpdated(key, value) {
    if(this.showing) this.printStore();
  }

  printStore() {
    let htmlStr = '<table>';
    for(let storeKey in _store.state) {
      let val = _store.state[storeKey];
      if(val && typeof val == "object" && val.length && val.length > 0) val = `Array(${val.length})`; // special display for arrays
      htmlStr += `<tr><td>${storeKey}</td><td>${val}</td></tr>`;
    }
    htmlStr += '</table>';
    this.container.innerHTML = htmlStr;
  }

}
