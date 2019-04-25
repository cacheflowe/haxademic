class SVGUtil {

  static rasterizeSVG(svgEl, renderedCallback, jpgQuality) {
    // WARNING! Inline <image> tags must have a base64-encoded image as their source. Linked image files will not work.
    // transform svg into base64 image
    const s = new XMLSerializer().serializeToString(svgEl);
    const uri = SVGUtil.dataImgPrefix + window.btoa(s);

    // load svg image into canvas
    const image = new Image();
    image.onload = function() {
      if(jpgQuality) {
        const canvas = SVGUtil.drawImageToNewCanvas(image, true);
        renderedCallback(canvas.toDataURL('image/jpeg', jpgQuality));
      } else {
        const canvas = SVGUtil.drawImageToNewCanvas(image);
        renderedCallback(canvas.toDataURL('image/png'));
      }
    }
    image.src = uri;
  }

  static drawImageToNewCanvas(image, drawBackground) {
    const canvas = document.createElement('canvas');
    canvas.width = image.width;
    canvas.height = image.height;
    const context = canvas.getContext('2d');
    if(drawBackground) { // set white background before rendering
      context.fillStyle = '#fff';
      context.fillRect(0, 0, canvas.width, canvas.height);
    }
    context.drawImage(image, 0, 0);
    return canvas;
  }

  static elementToString(el) {
    return el.outerHTML;
  }

  static setBase64ImageOnSvgImage(el, base64Img) {
    el.removeAttributeNS("http://www.w3.org/1999/xlink", "href");
    el.setAttributeNS("http://www.w3.org/1999/xlink", "href", base64Img);
  }

  static svgStrToBase64(svgStr) {
    return 'data:image/svg+xml;base64,' + btoa(svgStr);
  }

  static svgElToBase64(el, callback) {
    return SVGUtil.svgStrToBase64(el.outerHTML);
  }

}

SVGUtil.clearColor = 'rgba(0,0,0,0)';
SVGUtil.dataImgPrefix = 'data:image/svg+xml;base64,';
SVGUtil.testSVG = '<svg xmlns="http://www.w3.org/2000/svg" height="100" width="100"><circle cx="50" cy="50" r="40" stroke="black" stroke-width="3" fill="red"/></svg>';
SVGUtil.testSVG2 = '<svg xmlns="http://www.w3.org/2000/svg" height="100" width="100"><rect x="10" y="10" width="80" height="80" stroke="black" stroke-width="3" fill="red"/></svg>';
