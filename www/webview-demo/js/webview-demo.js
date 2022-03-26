class WebViewDemo {

  // MAKE NATIVE BRIDGE CONNECTION!

  constructor() {
    this.listenForNativeInterface();
  }

  addPropertyChangedCallback(obj, prop, callback) {
    // lets us listen for properties changing on an object
    // in this case we're listening for an injected value from the native app
    if (obj[prop]) callback(obj[prop]);   // if value already exists, do callback immediately
    let val = obj[prop];                  // cache value & create listener for changes
    Object.defineProperty(obj, prop, {
      get() { return val; }, // return the cached value
      set(newVal) {
        val = newVal;
        callback(val);
      }
    });
  }

  listenForNativeInterface() {
    // set platform flags
    this.nativeDesktopReady = false;

    // listen for specific window property to init bridge: 'nativeBridge'
    // this value is injected by parent native app's WebView
    this.addPropertyChangedCallback(window, 'nativeBridge', () => this.desktopBridgeInit());
  }

  desktopBridgeInit() {
    // set flag and send message up to native app
    this.nativeDesktopReady = true;
    let messageOut = {
      "type": "string",
      "key": "bridge-init",
      "value": `=== .js -> Desktop bridge connected ===`,
    }
    this.sendJsonToNativeDesktop(JSON.stringify(messageOut));

    // start animation loop
    this.animate();
  }
  
  sendJsonToNativeDesktop(jsonData) {
    // JavaFX WebView-specific call to native side. Expects JSON string
    try {
      window.nativeBridge.webCallback(jsonData);
    } catch(err) {
      alert("sendJsonToNativeDesktop FAILED");
    }
  }

  // Once connected, we can stream data both ways

  animate() {
    requestAnimationFrame(() => this.animate());

    // steam a number to the native app
    let timeOut = (Date.now() / 1000) % 10000;
    document.getElementById('debug-out').innerHTML = `${timeOut}`;
    if(this.nativeDesktopReady) {
      this.sendJsonToNativeDesktop(JSON.stringify({
        "type": "number",
        "key": "time",
        "value": timeOut,
      }));
    }
  }

  setBGColor(r, g, b) {
    let newBgColor = `rgb(${r}, ${g}, ${b})`
    document.body.style.backgroundColor = newBgColor;
    document.getElementById('debug-in').innerHTML = `${newBgColor}`;
  }

}

window.app = new WebViewDemo();