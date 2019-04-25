
class IframeUrlLoader {

  constructor(url, callback) {
    this.callback = callback;
    this.iframeLoader = document.createElement('iframe');
    this.iframeLoader.setAttribute('style', 'display:block; width: 1px; height: 1px; pointer-events: none; opacity: 0; position: absolute; top: -10px; left: -10px');
    document.body.appendChild(this.iframeLoader);
    this.iframeLoader.src = url;
    this.checkForIframeReady();
  }

  checkForIframeReady() {
    this.iframeLoader.contentDocument.addEventListener('DOMContentLoaded', () => this.iframeLoaded());
    this.iframeLoader.contentWindow.addEventListener('load', () => this.iframeLoaded());
    this.iframeLoader.addEventListener('load', () => this.iframeLoaded());
  }

  iframeLoaded() {
    this.callback();
    if(this.iframeLoader) {
      setTimeout(() => {
        document.body.removeChild(this.iframeLoader);
        this.iframeLoader = null;
      }, 2000);
    }
  }

  static recacheUrlForFB(url) {
    // use: let iFrameLoader = new IframeUrlLoader(IframeUrlLoader.recacheUrlForFB('https://cacheflowe.com'), () => {console.log('recached!'); });
    return `https://graph.facebook.com/?scrape=true&id=${window.encodeURIComponent(url)}`;
    // return `https://www.facebook.com/sharer/sharer.php?u=${this.getShareUrl()}`; // use this if the page redirects
  }
}
