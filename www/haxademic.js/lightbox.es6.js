class Lightbox {

  constructor() {
    this.lightboxDiv = null;
    this.lightboxImgUrl = null;
    this.lightboxImageLoader = null;
    this.active = false;

    // listen for close events
    document.addEventListener('click', (e) => this.hideLightbox(e));
    document.addEventListener('keyup', (e) => this.checkEscClose(e));
    window.addEventListener('scroll', (e) => this.hideLightbox(e));

    // check to open an image
    document.addEventListener('click', (e) => this.checkDocumentClick(e));
  }

  closest(element, tagname) {
    tagname = tagname.toLowerCase();
    while (true) {
      if (element.nodeName.toLowerCase() === tagname) return element;
      if (!(element = element.parentNode)) break;
    }
    return null;
  }

  checkDocumentClick(e) {
    // check links
    let clickedEl = this.closest(e.target, 'a');
    if(clickedEl && clickedEl.getAttribute('rel') == 'lightbox') {
      e.preventDefault();
      this.handleLightboxLink(clickedEl.href);
    }
    // check images
    let clickedImg = this.closest(e.target, 'img');
    if(clickedImg && clickedImg.classList.contains('imagexpander')) {
      e.preventDefault();
      this.handleLightboxLink(clickedImg.getAttribute('src'));
    }
  }

  handleLightboxLink(imageUrl) {
    // load image
    this.lightboxImgUrl = imageUrl;
    this.lightboxImageLoader = new Image();
    this.lightboxImageLoader.addEventListener('load', (e) => this.lightboxImageLoaded(e));
    this.lightboxImageLoader.src = this.lightboxImgUrl;
  }

  lightboxImageLoaded() {
    // check if we need to let the image display at natural size
    // console.log('this.lightboxImageLoader.height', this.lightboxImageLoader.height);
    var containedClass = (this.lightboxImageLoader.height < window.innerHeight - 40 && this.lightboxImageLoader.width < window.innerWidth - 40) ? 'lightbox-image-contained' : '';

    // add elements to body
    this.lightboxDiv = document.createElement('div');
    this.lightboxDiv.className = 'lightbox';
    this.lightboxDiv.innerHTML = '<div class="lightbox-image-holder '+ containedClass +'" style="background-image:url('+ this.lightboxImgUrl +')"></div>';
    document.body.appendChild(this.lightboxDiv);

    this.active = true;
    requestAnimationFrame(() => {
      this.lightboxDiv.className = 'lightbox';
      requestAnimationFrame(() => {
        this.lightboxDiv.className = 'lightbox showing';
      });
    });
  }

  checkEscClose(e) {
    if(e.keyCode == 27) {
      this.hideLightbox();
    }
  }

  hideLightbox(e) {
    if(!this.active) return;
    if(!this.lightboxDiv) return;
    this.active = false;
    this.lightboxDiv.className = 'lightbox';
    setTimeout(() => {
      document.body.removeChild(this.lightboxDiv);
    }, 300);
  }

}
