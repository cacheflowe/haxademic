class DOMUtil {

  static closest(element, selector) {
    selector = selector.toLowerCase();
    let className = selector.split('.').length > 1 ? selector.split('.')[1] : '';
    selector = selector.split('.')[0];
    while (true) {
      if (element.nodeName.toLowerCase() === selector && element.className.indexOf(className) !== -1) {
        return element;
      }
      if (!(element = element.parentNode)) {
        break;
      }
    }
    return null;
  }

  static remove(el) {
    if (el && el.parentNode) {
      return el.parentNode.removeChild(el);
    }
    return null;
  }

  static stringToDomElement(str) {
    let div = document.createElement('div');
    div.innerHTML = str;
    return div.firstChild;
  }

  static elementToString(el) {
    return el.outerHTML;
  }

  static addLoadedClass() { // add class to enable animation a moment after window load
    window.addEventListener('load', (e) => {
      setTimeout(() => {
        document.body.classList.add('ready');
      }, 1000);
    });
  }

  static isElementVisible(el) {
    var rect = el.getBoundingClientRect();
    return rect.bottom > 0 &&
        rect.right > 0 &&
        rect.left < (window.innerWidth || document.documentElement.clientWidth) &&
        rect.top < (window.innerHeight || document.documentElement.clientHeight);
  }

}
