class PointerUtil {

  // TODO: finish converting this !
  static multipleClickHandler() {
    // require quintuple-click
    var clickStream = [];
    var numClicks = 5;
    var timeWindow = 3000;
    cameraInput.addEventListener(window.tapEvent, function(e){
      clickStream.push(Date.now());
      while(clickStream.length > numClicks) clickStream.shift();
      var recentClicks = clickStream.filter(function(clickTime) {
        return clickTime > Date.now() - timeWindow;
      });
      if(recentClicks.length < numClicks) e.preventDefault();
      else clickStream.splice(0);
    });
  }

  static disableRightClick(el) {
    el.oncontextmenu = function(e){ return false; };
  }

}
