class CanvasFilters {

  static createImageData(w,h) {
    return CanvasFilters.tmpCtx.createImageData(w,h);
  };

  // helpers to overwrite canvas that's getting filtered
  static getCanvasImageData(canvas) {
    const w = canvas.width;
    const h = canvas.height;
    const ctx = canvas.getContext("2d");
    const imageData = ctx.getImageData(0, 0, w, h);
    return imageData;
  };

  static getCanvasContext(canvas) {
    return canvas.getContext("2d");
  }


  // from: https://gist.github.com/doctyper/992342
  // Desaturate Usage:
  static desaturate(canvas) {
    const imageData = CanvasFilters.getCanvasImageData(canvas);
    const pixels = imageData.data;
    for (let i = 0; i < pixels.length; i += 4) {
      const avg = (pixels[i] + pixels[i +1] + pixels[i +2]) / 3;
      pixels[i]     = avg; // red
      pixels[i + 1] = avg; // green
      pixels[i + 2] = avg; // blue
    }
    CanvasFilters.getCanvasContext(canvas).putImageData(imageData,0,0);
  };

  // from: https://github.com/meltingice/CamanJS/blob/master/src/lib/filters.coffee
  // Range is -1 to 1. Values < 0 will desaturate the image while values > 0 will saturate it.
  static saturation(canvas, adjustment) {
    adjustment *= -1;
    const imageData = CanvasFilters.getCanvasImageData(canvas);
    const pixels = imageData.data;
    for (let i = 0; i < pixels.length; i += 4) {
      const max = Math.max(pixels[i], pixels[i+1], pixels[i+2]);
      if(pixels[i  ] != max) pixels[i  ] += (max - pixels[i  ]) * adjustment;
      if(pixels[i+1] != max) pixels[i+1] += (max - pixels[i+1]) * adjustment;
      if(pixels[i+2] != max) pixels[i+2] += (max - pixels[i+2]) * adjustment;
    }
    CanvasFilters.getCanvasContext(canvas).putImageData(imageData,0,0);
  };

  static brightness(canvas, adjustment) {
    adjustment *= 255;
    const imageData = CanvasFilters.getCanvasImageData(canvas);
    const pixels = imageData.data;

    for (let i = 0; i < pixels.length; i += 4) {
      pixels[i] += adjustment;
      pixels[i+1] += adjustment;
      pixels[i+2] += adjustment;
    }
    CanvasFilters.getCanvasContext(canvas).putImageData(imageData,0,0);
  };


  // from: http://stackoverflow.com/questions/10521978/html5-canvas-image-contrast
  static contrastImage(canvas, contrast) {
    const imageData = CanvasFilters.getCanvasImageData(canvas);
    const pixels = imageData.data;

    const factor = (259 * (contrast + 255)) / (255 * (259 - contrast));

    for (let i = 0; i < pixels.length; i += 4) {
        pixels[i] = factor * (pixels[i] - 128) + 128;
        pixels[i+1] = factor * (pixels[i+1] - 128) + 128;
        pixels[i+2] = factor * (pixels[i+2] - 128) + 128;
    }
    CanvasFilters.getCanvasContext(canvas).putImageData(imageData,0,0);
  }


  // special convolution filter codes from:
  // http://www.html5rocks.com/en/tutorials/canvas/imagefilters/
  static convolute(pixels, weights, opaque) {
    const side = Math.round(Math.sqrt(weights.length));
    const halfSide = Math.floor(side/2);

    const src = pixels.data;
    const sw = pixels.width;
    const sh = pixels.height;

    const w = sw;
    const h = sh;
    const output = CanvasFilters.createImageData(w, h);
    const dst = output.data;

    const alphaFac = opaque ? 1 : 0;

    for (let y=0; y<h; y++) {
      for (let x=0; x<w; x++) {
        const sy = y;
        const sx = x;
        const dstOff = (y*w+x)*4;
        let r=0;
        let g=0;
        let b=0;
        let a=0;
        for (let cy=0; cy<side; cy++) {
          for (let cx=0; cx<side; cx++) {
            const scy = Math.min(sh-1, Math.max(0, sy + cy - halfSide));
            const scx = Math.min(sw-1, Math.max(0, sx + cx - halfSide));
            const srcOff = (scy*sw+scx)*4;
            const wt = weights[cy*side+cx];
            r += src[srcOff] * wt;
            g += src[srcOff+1] * wt;
            b += src[srcOff+2] * wt;
            a += src[srcOff+3] * wt;
          }
        }
        dst[dstOff] = r;
        dst[dstOff+1] = g;
        dst[dstOff+2] = b;
        dst[dstOff+3] = a + alphaFac*(255-a);
      }
    }
    return output;
  };


  static sharpen(canvas) {
    const filteredPixels = CanvasFilters.convolute(
      CanvasFilters.getCanvasImageData(canvas),
      [ 0, -1,  0,
       -1,  5, -1,
        0, -1,  0],
      false
    );
    CanvasFilters.getCanvasContext(canvas).putImageData(filteredPixels,0,0);
  }
}

CanvasFilters.tmpCanvas = document.createElement('canvas');
CanvasFilters.tmpCtx = CanvasFilters.tmpCanvas.getContext('2d');
