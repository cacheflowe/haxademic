class MobileUtil {

  // TOUCHSCREEN HELPERS
  static isFullscreenApp() {
    return window.navigator.standalone;
  }

  static enablePseudoStyles() {
    document.addEventListener("touchstart", function(){}, false);
  }

  static setDeviceInputClass() {
    const deviceClass = (MobileUtil.isMobileBrowser()) ? 'mobile' : 'desktop';
    document.body.classList.add(deviceClass);
  }

  static lockTouchScreen( isLocked ) {
    if( isLocked == false ) {
      document.ontouchmove = null;
    } else {
      document.ontouchmove = function( event ) {
        event.preventDefault();
      };
    }
  }

  static disableZoom() {
    document.addEventListener('touchmove', function(event) {
      event = event.originalEvent || event;
      if(event.scale != 1) {
        event.preventDefault();
      }
    }, false);
  }

  static disableTrackpadZoom() {
    window.addEventListener('mousewheel', function(e) {
      if(e.ctrlKey) {
        e.preventDefault();
      }
    });
  }

  static hideSoftKeyboard() {
    if(document.activeElement && document.activeElement.blur) document.activeElement.blur()
    var inputs = document.querySelectorAll('input');
    for (var i = 0; i < inputs.length; i++) inputs[i].blur();
  }

  static unlockWebAudioOnTouch() {
    window.addEventListener('touchstart', MobileUtil.playEmptyWebAudioSound);
  }

  static playEmptyWebAudioSound() {
    var myContext = (AudioContext != null) ? new AudioContext() : new webkitAudioContext(); // create empty buffer and play it
    var buffer = myContext.createBuffer(1, 1, 44100);
    var source = myContext.createBufferSource();
    source.buffer = buffer;
    source.connect(myContext.destination);                                                  // connect to output (your speakers)
    source.start();                                                                         // play the file
    window.removeEventListener('touchstart', MobileUtil.playEmptyWebAudioSound);            // clean up the event listener
  }

  // PLATFORM DETECTION

  static isMobileBrowser() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/android|iphone|ipad|ipod/i)) return true;
    return false;
  }

  static isIOS() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/iphone/i)) return true;
    if(userAgent.match(/ipad/i)) return true;
    if(userAgent.match(/ipod/i)) return true;
    if(userAgent.match(/crios/i)) return true;
    return false;
  }

  static isIPhone() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/iphone/i)) return true;
    if(userAgent.match(/ipod/i)) return true;
    return false;
  }

  static isAndroid() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/android/i)) return true;
    return false;
  }

  static isSafari() {
    var userAgent = navigator.userAgent.toLowerCase()
    var isChrome = (userAgent.match(/chrome/i)) ? true : false;
    var isSafari = (userAgent.match(/safari/i)) ? true : false;
    return (isSafari == true && isChrome == false) ? true : false;
  }

  static isIE11() {
    return !!window.MSInputMethodContext && !!document.documentMode;
  }

  // MOBILE HELPERS

  static alertErrors() {
    // alert errors on mobile to help detect bugs
    if(!window.addEventListener) return;
    window.addEventListener('error', (e) => {
      var fileComponents = e.filename.split('/');
      var file = fileComponents[fileComponents.length-1];
      var line = e.lineno;
      var message = e.message;
      alert('ERROR\n'+'Line '+line+' in '+file+'\n'+message);
    });
  };

  static openNewWindow(href) {
    // gets around native mobile popup blockers
    var link = document.createElement('a');
    link.setAttribute('href', href);
    link.setAttribute('target','_blank');
    var clickevent = document.createEvent('Event');
    clickevent.initEvent('click', true, false);
    link.dispatchEvent(clickevent);
    return false;
  }

}
