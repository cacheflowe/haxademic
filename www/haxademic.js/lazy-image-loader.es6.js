class LazyImageLoader {

  constructor(el) {
    this.el = el;
    this.scrollHandler = null;
    // this.imgQueue = null;
    let imgNodes = this.el.querySelectorAll('[data-src]');
    this.imageEls = [];
    for(let i=0; i < imgNodes.length; i++) this.imageEls.push(imgNodes[i]);
    this.queuedEls = [];
    this.loading = false;
    this.queueVisibleImages();
    this.addScrollHandler();
  }

  // scroll handling

  addScrollHandler() {
    this.scrollHandler = this.scrolled.bind(this);
    window.addEventListener('scroll', this.scrollHandler);
  }

  removeScrollHandler() {
    if(this.scrollHandler == null) return;
    window.removeEventListener('scroll', this.scrollHandler);
    this.scrollHandler = null;
  }

  scrolled() {
    this.queueVisibleImages();
  }

  // check for visible images & queue them up

  queueVisibleImages() {
    // queue up visible images - splice in reverse
    let newVisibleImages = [];
    for (var i = this.imageEls.length - 1; i >= 0; i--) {
      if(DOMUtil.isElementVisible(this.imageEls[i])) {
        let visibleImg = this.imageEls.splice(i, 1)[0];
        newVisibleImages.push(visibleImg);
      }
    }
    // reverse found images and push into queue
    newVisibleImages.reverse();
    newVisibleImages.forEach((el, i) => {
      this.queuedEls.push(el);
    });
    // kick off loading if we're not already loading
    if(this.loading == false) this.loadNextImage();
    // clean up if all images are loaded
    if(this.imageEls.length == 0 && this.queuedEls.length == 0) {
      this.dispose();
    }
  }

  loadNextImage() {
    if(this.queuedEls.length > 0) {
      let curImg = this.queuedEls.shift();
      this.loadImage(curImg);
    }
  }

  loadImage(curImg) {
    this.loading = true;
    let img = new Image();
    // complete/error callbacks
    img.onload = () => {
      this.loading = false;
      this.cleanUpImg(curImg, img);
      this.loadNextImage();
    };
    img.onerror = () => {
      this.loading = false;
      this.loadNextImage();
    };
    // trigger load from image path
    img.src = curImg.getAttribute('data-src');
  }

  cleanUpImg(curImg, img) {
    if(curImg.getAttribute('data-src-bg')) {
      curImg.style.backgroundImage = `url(${img.src})`;
    } else {
      curImg.setAttribute('src', img.src);
    }
    curImg.removeAttribute('data-src-bg');
    curImg.removeAttribute('data-src');
  }

  // array helpers

  clearArray(array) {
    array.splice(0, array.length);
  }

  // lifecycle

	dispose() {
    this.removeScrollHandler();
    this.clearArray(this.imageEls);
    this.clearArray(this.queuedEls);
	}

}
