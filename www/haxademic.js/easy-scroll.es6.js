class EasyScroll {

  constructor(scrollEl=window) {
    this.scrollEl = scrollEl;
    this.startScrollY = 0;
    this.scrollDist = 0;
    this.frame = 0;
    this.frames = 0;
  }

  easeInOutQuad(t, b, c, d) {
    if ((t/=d/2) < 1) return c/2*t*t + b;
    return -c/2 * ((--t)*(t-2) - 1) + b;
  }

  animateScroll() {
    this.frame++;
    if(this.frame <= this.frames) requestAnimationFrame(() => this.animateScroll());
    let percentComplete = this.frame / this.frames;
    let scrollProgress = this.scrollDist * this.easeInOutQuad(percentComplete, 0, 1, 1);
    if(this.scrollEl == window) {
      window.scrollTo(0, Math.round((this.startScrollY - scrollProgress)));
    } else {
      this.scrollEl.scrollTop = Math.round(this.startScrollY - scrollProgress);
    }
  }

  scrollByY(duration, scrollAmount) {
    this.startScrollY = (this.scrollEl == window) ? window.scrollY : this.scrollEl.scrollTop;
    this.scrollDist = scrollAmount;
    this.frame = 0;
    this.frames = Math.floor(duration / 16);
    requestAnimationFrame(() => this.animateScroll());
  }

  scrollToEl(duration, el, offset) {
    let pageOffset = (this.scrollEl == window) ? 0 : this.scrollEl.getBoundingClientRect().top;
    this.scrollByY(duration, -el.getBoundingClientRect().top + offset + pageOffset);
  }

  setScrollEl(el) {
    this.scrollEl = el;
  }

}
