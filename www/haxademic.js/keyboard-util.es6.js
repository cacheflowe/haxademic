class KeyboardUtil {

  constructor() {}

  static addKeyListener(keycode, callback) {
    window.addEventListener('keydown', (e) => {
      var key = e.keyCode ? e.keyCode : e.which;
      // console.log(key);
      if (key == keycode) {
        callback(e);
      }
    });
  }
}
