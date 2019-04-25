class PointerPos {

  constructor() {
    this.curX = -1;
    this.curY = -1;
    this.lastX = -1;
    this.lastY = -1;

    // add mouse/touch listeners
    document.addEventListener('mousedown', (e) => {
      this.pointerMoved(e.clientX, e.clientY);
    });
    document.addEventListener('mousemove', (e) => {
      this.pointerMoved(e.clientX, e.clientY);
    });
    document.addEventListener('touchstart', (e) => {
      this.pointerMoved(e.touches[0].clientX, e.touches[0].clientY);
    });
    document.addEventListener('touchmove', (e) => {
      this.pointerMoved(e.touches[0].clientX, e.touches[0].clientY);
    });
  }

  reset() {
    this.curX = -1;
    this.curY = -1;
    this.lastX = -1;
    this.lastY = -1;
  }

  pointerMoved(x, y) {
    this.lastX = this.curX;
    this.lastY = this.curY;
    this.curX = x;
    this.curY = y;
  }

  x(el = null) {
    if(el) {
      var offset = el.getBoundingClientRect();
      return this.curX - offset.left;
    }
    return this.curX;
  };

  y(el = null) {
    if(el) {
      var offset = el.getBoundingClientRect();
      return this.curY - offset.top;
    }
    return this.curY;
  };

  xPercent(el) {
    if(el != null) {
      var offset = el.getBoundingClientRect();
      var relativeX = this.curX - offset.left;
      return relativeX / offset.width;
    }
    return this.curX / window.innerWidth;
  };

  yPercent(el) {
    if(el != null) {
      var offset = el.getBoundingClientRect();
      var relativeY = this.curY - offset.top;
      return relativeY / offset.height;
    }
    return this.curY / window.innerHeight;
  };

  xDelta() {
    return (this.lastX == -1) ? 0 : this.curX - this.lastX;
  };

  yDelta() {
    return (this.lastY == -1) ? 0 : this.curY - this.lastY;
  };

}
