class AppStore {

  constructor() {
    this.state = {};
    this.listeners = [];
    this.methods = {};
    window._store = this;
  }

  addListener(obj, key) {
    if(key) {
      if(!this.methods[key]) this.methods[key] = [];
      this.methods[key].push(obj);
    } else {
      this.listeners.push(obj);
    }
  }

  removeListener(obj, key) {
    if(key) {
      if(this.methods[key]) {
        const index = this.methods[key].indexOf(obj);
        if (index !== -1) this.methods[key].splice(index, 1);
      }
    } else {
      const index = this.listeners.indexOf(obj);
      if (index !== -1) this.listeners.splice(index, 1);
    }
  }

  set(key, value) {
    if(typeof key === "undefined") throw new Error('AppStore requires legit keys');
    this.state[key] = value;
    this.listeners.forEach((el, i, arr) => {
      el.storeUpdated(key, value);
    });
    // specific listener methods
    const objs = this.methods[key];
    if(objs) {
      objs.forEach((el) => {
        if(el[key]) el[key](value);
        else throw new Error('AppStore listener has no callback: ' + key);
      });
    }
  }

  get(key) {
    return this.state[key];
  }

  log() {
    for(let key in _store.state) {
      console.log(key, _store.state[key]);
    }
  }

}

class AppStoreDebug {

  constructor(showing=false) {
    this.showing = showing;
    this.buildElement();
    _store.addListener(this);
    this.initKeyListener();
  }

  initKeyListener() {
    window.addEventListener('keyup', (e) => {
      var key = e.keyCode ? e.keyCode : e.which;
      // console.log('key', key);
      if(key == 32) {
        this.showing = !this.showing;
      }
      if(this.showing == false) {
        this.container.innerHTML = '';
        this.container.style.display = 'none';
      } else {
        this.printStore();
        this.container.style.display = 'block';
      }
    });
  }

  buildElement() {
    this.container = document.createElement( 'div' );
    this.container.style.cssText = 'font-family:arial;font-size:12px;position:fixed;top:0;left:0;padding:12px;height:100%;overflow-y:auto;opacity:0.9;z-index:9999;background:rgba(0,0,0,0.8);color:#fff !important;';
    document.body.appendChild(this.container);
    this.container.style.display = 'none';
  }

  storeUpdated(key, value) {
    if(this.showing) this.printStore();
  }

  printStore() {
    let htmlStr = '<table>';
    for(let storeKey in _store.state) {
      let val = _store.state[storeKey];
      if(val && typeof val == "object" && val.length && val.length > 0) val = `Array(${val.length})`; // special display for arrays
      htmlStr += `<tr><td>${storeKey}</td><td>${val}</td></tr>`;
    }
    htmlStr += '</table>';
    this.container.innerHTML = htmlStr;
  }

}

class AppStoreDistributed extends AppStore {

  constructor(socketServerUrl) {
    super();
    // init websock connection
    this.socketServerUrl = socketServerUrl;
    this.solidSocket = new SolidSocket(socketServerUrl);
    this.solidSocket.setOpenCallback((e) => this.onOpen(e));
    this.solidSocket.setMessageCallback((e) => this.onMessage(e));
  }

  onOpen() {
    console.log('AppStoreDistributed connected to ' + this.socketServerUrl);
  }

  onMessage(event) {
    let jsonData = JSON.parse(event.data);
    if(jsonData['store'] && jsonData['type']) {
      this.set(jsonData['key'], jsonData['value']);
    } else {
      this.set('json', jsonData);
      // this.set('json', event.data); // just to see data in AppStoreDebug
    }
  };

  set(key, value, broadcast) {
    super.set(key, value);
    if(broadcast) {
      // get data type for java AppStore
      var type = "number";
      if(typeof value === "boolean") type = "boolean";
      if(typeof value === "string") type = "string";
      // set json object for AppStore
      let data = {
        key: key,
        value: value,
        store: true,
        type: type
      };
      this.solidSocket.sendMessage(JSON.stringify(data));
    }
  }

  broadcastJson(obj) {
    this.solidSocket.sendMessage(JSON.stringify(obj));
  }

}

class ArrayUtil {

  static removeElement(array, element) {
    const index = array.indexOf(element);
    if (index !== -1) return array.splice(index, 1);
    return null;
  }

  static clear(array) {
    array.splice(0, array.length);
  }

  static shuffle(array) {
    array.sort(() => {return 0.5 - Math.random()});
  }

  static randomElement(array) {
    return array[MathUtil.randRange(0, array.length - 1)];
  }

  static moveLastToFirst(array) {
    array.unshift(array.pop());
  }

  static uniqueArrayCopy(array) {
    return array.filter((el, i, arr) => {
      return arr.indexOf(el) === i;   // only return the first instance of an element
    });
  }

  static uniqueArrayCopy2(array) {
    return [...new Set(array)]; // via @addyosmani
  }

  static crossfadeEnds(array, fadeSize=0.1) {
    // number of elements to fade on either end
    let numToFade = Math.round(array.length * fadeSize);
    // average of start/end values
    let endAvg = (array[array.length - 1] + array[0]) / 2;
    for(var i = 0; i <= numToFade; i++) {
      // lerp strength increases
      let lerpStrength = i / numToFade;
      // indices go from inland towards the edges, increasingly fading towards the ends average
      let endIndex = array.length - numToFade - 1 + i;
      array[endIndex] = MathUtil.lerp(array[endIndex], endAvg, lerpStrength);
      let startIndex = numToFade - i;
      array[startIndex] = MathUtil.lerp(array[startIndex], endAvg, lerpStrength);
    }
  }

}

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

class CanvasUtil {
  /**
   *  Lets a developer know if the canvas element is supported.
   *  @return A boolean indicating canvas support.
   *  @use    {@code CanvasUtil.hasCanvas();}
   */
  static hasCanvas() {
    return (!document.createElement('canvas').getContext) ? false : true;
  }

  static hasWebGL() {
    var canvas = document.createElement( 'canvas' ); return !! ( window.WebGLRenderingContext && ( canvas.getContext( 'webgl' ) || canvas.getContext( 'experimental-webgl' ) ) );
  }

  static getCanvasContext(canvas) {
    return canvas.getContext("2d");
  }

  /**
   *  Draws a filled circle. Original code from Robin W. Spencer (http://scaledinnovation.com).
   *  @use    {@code CanvasUtil.drawCircle( context, 50, 50, 40 );}
   */
  static drawCircle( ctx, x, y, radius, extraSetup ) {
    ctx.save();
    ctx.beginPath();
    ctx.arc( x, y, radius, 0.0, 2 * Math.PI, false );
    ctx.closePath();
    ctx.stroke();
    ctx.fill();
    ctx.restore();
  }

  /**
   *  Draws an arc - a portion of a circle.
   *  @use    {@code CanvasUtil.drawArc(context, 50, 50, 40, 90, 180);}
   */
  static drawArc(ctx, x, y, radius, startRads, endRads, centered=true, connectCenter=false) {
    ctx.save();
    ctx.beginPath();
    if(centered) ctx.moveTo(x, y);
    ctx.arc(x, y, radius, startRads, endRads, connectCenter);
    ctx.closePath();
    ctx.stroke();
    ctx.fill();
    ctx.restore();
  }



  static getImageDataForContext( context ) {
    return context.getImageData( 0, 0, context.canvas.width, context.canvas.height ).data
  }

  static getPixelFromImageData( imageData, contextWidth, x, y ) {
    var rIndex = (y * contextWidth + x) * 4;
    return [imageData[rIndex], imageData[rIndex+1], imageData[rIndex+2]];
  }

  static getPixelColorFromContext( context, x, y ) {
    var pixelData = context.getImageData( x, y, 1, 1 ).data;
    return [pixelData[0], pixelData[1], pixelData[2]];
  }

  static pixelColorToCanvasColor( context, x, y ) {
    var color = CanvasUtil.getPixelColorFromContext( context, x, y );
    return CanvasUtil.rgbToCanvasColor( color[0], color[1], color[2], 1 );
  }

  static loadImageToCanvas( imagePath, callback ) {
      var image = new Image();
      image.crossOrigin = 'anonymous';
      image.onload = function() {
          var canvas = document.createElement("canvas");
          canvas.width = image.width;
          canvas.height = image.height;
          var context = canvas.getContext("2d");
          context.drawImage( image, 0, 0 );
          callback( canvas, image );
      };
      image.src = imagePath;
  }

  // useful for grabbing an image and caching it as a pixel source
  static loadImageToContext( imagePath, callback ) {
      var image = new Image();
      image.onload = function() {
          var canvasSource = document.createElement("canvas");
          canvasSource.width = image.width;
          canvasSource.height = image.height;
          var context = canvasSource.getContext("2d");
          context.drawImage( image, 0, 0 );
          callback( context, image );
      };
      image.src = imagePath;
  }

  static loadImageToContextFromInput( inputEl, callback ) {
    inputEl.addEventListener('change', function(e){
      callback( inputEl.files );
      e.stopPropagation();
      e.preventDefault();
    });
  }

  static loadImageToContextFromDrop( dropEl, callback ) {
    dropEl.addEventListener('dragenter', function(e){
      e.stopPropagation();
      e.preventDefault();
      dropEl.classList.add('drop-over');
    });
    dropEl.addEventListener('dragover', function(e){
      e.stopPropagation();
      e.preventDefault();
    });
    dropEl.addEventListener('dragleave', function(e){
      e.stopPropagation();
      e.preventDefault();
      dropEl.classList.remove('drop-over');
    });
    dropEl.addEventListener('drop', function(e){
      e.stopPropagation();
      e.preventDefault();
      callback( e.target.files || e.dataTransfer.files );
      dropEl.classList.remove('drop-over');
    }, false);
  }

  static imagesSelected( myFiles, callback ) {
    for (var i = 0, f; f = myFiles[i]; i++) {
      var imageReader = new FileReader();
      // onload callback when file loads
      imageReader.onload = (function(aFile) {
        return function(e) {
          var span = document.createElement('span');
          var image = document.createElement('img');
          image.src = e.target.result;  // base64 image string
          image.alt = aFile.name;       // file name
          // document.getElementById('drop').insertBefore(image, null);
          setTimeout(function(){
            var canvasSource = document.createElement("canvas");
            canvasSource.width = image.width;
            canvasSource.height = image.height;
            var context = canvasSource.getContext("2d");
            context.drawImage( image, 0, 0 );
            callback( context, image );
          },300);
        };
      })(f);
      imageReader.readAsDataURL(f); // load/read the file
    }
  }

  static copyPixels( source, destination, sourceX, sourceY, sourceW, sourceH, destX, destY, destW, destH ) {
      sourceX = sourceX || 0;
      sourceY = sourceY || 0;
      sourceW = sourceW || source.canvas.width;
      sourceH = sourceH || source.canvas.height;
      destX = destX || 0;
      destY = destY || 0;
      destW = destW || source.canvas.width;
      destH = destH || source.canvas.height;
      destination.putImageData( source.getImageData( sourceX, sourceY, sourceW, sourceH ), destX, destY, destX, destY, destW, destH );
  }

  // canvas saving
  static saveCanvas( ctx ){
    // set canvasImg image src to dataURL
    // so it can be saved as an image
    var saveImg = document.createElement('img');
    saveImg.id = 'save';
    document.body.appendChild(saveImg);
    saveImg.src = ctx.canvas.toDataURL();
    saveImg.width = ctx.canvas.width/4;
    saveImg.height = ctx.canvas.height/4;
    saveImg.id = '';
  }

  static cloneCanvas(oldCanvas) {
    // from: http://stackoverflow.com/a/8306028/352456
    var newCanvas = document.createElement('canvas');
    var context = newCanvas.getContext('2d');
    newCanvas.width = oldCanvas.width;
    newCanvas.height = oldCanvas.height;
    context.drawImage(oldCanvas, 0, 0);
    return newCanvas;
  }


  // texture mapping from: http://stackoverflow.com/a/4774298/352456
  static textureMap(ctx, texture, pts) {
      var tris = [[0, 1, 2], [2, 3, 0]]; // Split in two triangles
      for (var t=0; t<2; t++) {
          var pp = tris[t];
          var x0 = pts[pp[0]].x, x1 = pts[pp[1]].x, x2 = pts[pp[2]].x;
          var y0 = pts[pp[0]].y, y1 = pts[pp[1]].y, y2 = pts[pp[2]].y;
          var u0 = pts[pp[0]].u, u1 = pts[pp[1]].u, u2 = pts[pp[2]].u;
          var v0 = pts[pp[0]].v, v1 = pts[pp[1]].v, v2 = pts[pp[2]].v;

          // Set clipping area so that only pixels inside the triangle will
          // be affected by the image drawing operation
          ctx.save();
          ctx.beginPath();
          ctx.moveTo(x0, y0);
          ctx.lineTo(x1, y1);
          ctx.lineTo(x2, y2);
          ctx.closePath();
          ctx.clip();

          // Compute matrix transform
          var delta = u0*v1 + v0*u2 + u1*v2 - v1*u2 - v0*u1 - u0*v2;
          var delta_a = x0*v1 + v0*x2 + x1*v2 - v1*x2 - v0*x1 - x0*v2;
          var delta_b = u0*x1 + x0*u2 + u1*x2 - x1*u2 - x0*u1 - u0*x2;
          var delta_c = u0*v1*x2 + v0*x1*u2 + x0*u1*v2 - x0*v1*u2
                        - v0*u1*x2 - u0*x1*v2;
          var delta_d = y0*v1 + v0*y2 + y1*v2 - v1*y2 - v0*y1 - y0*v2;
          var delta_e = u0*y1 + y0*u2 + u1*y2 - y1*u2 - y0*u1 - u0*y2;
          var delta_f = u0*v1*y2 + v0*y1*u2 + y0*u1*v2 - y0*v1*u2
                        - v0*u1*y2 - u0*y1*v2;

          // Draw the transformed image
          ctx.transform(delta_a/delta, delta_d/delta,
                        delta_b/delta, delta_e/delta,
                        delta_c/delta, delta_f/delta);
          ctx.drawImage(texture, 0, 0);
          ctx.restore();
      }
  }
}

CanvasUtil.clearColor = 'rgba(0,0,0,0)';
CanvasUtil.dataImgPrefix = 'data:image/png;base64,';

class ColorRGB {

  constructor() {

  }

  static fromRGB(r, g, b) {

  }

  static fromHex(hexStr='#ffffff') {

  }
}

class ColorUtil {
  /**
   *  Converts a hex color value to canvas-friendly rgba. Original code from Robin W. Spencer (http://scaledinnovation.com).
   *  @return An rgba color string.
   *  @use    {@code CanvasUtil.hexToCanvasColor('#00ff00', 0.5);}
   */
  static hexToCanvasColor( hexColor, opacity ) {
    opacity = ( opacity != null ) ? opacity : "1.0";
    hexColor = hexColor.replace( "#", "" );
    var r = parseInt( hexColor.substring( 0, 2 ), 16 );
    var g = parseInt( hexColor.substring( 2, 4 ), 16 );
    var b = parseInt( hexColor.substring( 4, 6 ), 16 );
    return "rgba("+r+","+g+","+b+","+opacity+")";
  }

  /**
   *  Converts a hex value to a webGL-friendly number.
   *  @return A hex color string, without the hash.
   *  @use    {@code CanvasUtil.hexStringToNumberColor('ff00ff');}
   */
  static hexStringToNumberColor(hexStr) {
    return Number("0x"+hexStr.replace('#', ''));
  }


  /**
   *  Converts r, g, b, a values to a css-friendly hexadecimel string.
   *  @return An rgb color string.
   *  @use    {@code CanvasUtil.rgb2hex(0, 255, 0);}
   */
  static rgb2hex(r,g,b) {
    return "#" + Number(0x1000000 + r*0x10000 + g*0x100 + b).toString(16).substring(1);
  }

  /**
   *  Converts a hex string to an rgb object.
   *  @return A hex color string.
   *  @use    {@code CanvasUtil.hex2rgb('#ff00ff');}
   */
  static hex2rgb(hexColor) {
    hexColor = hexColor.replace( "#", "" );
    return {
      r: parseInt( hexColor.substring( 0, 2 ), 16 ),
      g: parseInt( hexColor.substring( 2, 4 ), 16 ),
      b: parseInt( hexColor.substring( 4, 6 ), 16 )
    };
  }

  /**
   *  Converts r, g, b, a values to a THREE/PIXI-friendly hexadecimel number.
   *  @return An rgb color string.
   *  @use    {@code CanvasUtil.rgb2hexNum(0, 255, 0);}
   */
  static rgb2hexNum(r,g,b) {
    return Number("0x"+ Number(0x1000000 + r*0x10000 + g*0x100 + b).toString(16).substring(1));
  }

  /**
   *  Converts a hex string to a THREE/PIXI-friendly hex number.
   *  @return A hex color string.
   *  @use    {@code ColorUtil.hexStr2HexNum('#ff0000);}
   */
  static hexStr2HexNum(str) {
    return parseInt(str.replace(/^#/, ''), 16);
  }

  /**
   *  Converts r, g, b, a values to canvas-friendly rgba string.
   *  @return An rgba color string.
   *  @use    {@code CanvasUtil.rgbToCanvasColor(0, 0, 0, 0.5);}
   */
  static rgbToCanvasColor( r, g, b, opacity ) {
    return "rgba("+r+","+g+","+b+","+opacity+")";
  }

  /**
   *  Converts r, g, b, to a brightness between 0-1.
   *  @return A brightness percentage.
   *  @use    {@code CanvasUtil.rgbToBrightness(0, 255, 0);}
   */
  static rgbToBrightness( r, g, b ) {
    return (r + g + b) / 765; // 765 is r,g,b: 255*3
  }

  // from: https://stackoverflow.com/questions/13806483/increase-or-decrease-color-saturation
  static rgb2hsv(color) {
    var r,g,b,h,s,v;
    r= color[0];
    g= color[1];
    b= color[2];
    min = Math.min( r, g, b );
    max = Math.max( r, g, b );


    v = max;
    delta = max - min;
    if( max != 0 )
        s = delta / max;        // s
    else {
        // r = g = b = 0        // s = 0, v is undefined
        s = 0;
        h = -1;
        return [h, s, undefined];
    }
    if( r === max )
        h = ( g - b ) / delta;      // between yellow & magenta
    else if( g === max )
        h = 2 + ( b - r ) / delta;  // between cyan & yellow
    else
        h = 4 + ( r - g ) / delta;  // between magenta & cyan
    h *= 60;                // degrees
    if( h < 0 )
        h += 360;
      if ( isNaN(h) )
          h = 0;
      return [h,s,v];
  }

  static hsv2rgb(color) {
    var i;
    var h,s,v,r,g,b;
    h = color[0];
    s = color[1];
    v = color[2];
    if(s === 0 ) {
        // achromatic (grey)
        r = g = b = v;
        return [r,g,b];
    }
    h /= 60;            // sector 0 to 5
    i = Math.floor( h );
    f = h - i;          // factorial part of h
    p = v * ( 1 - s );
    q = v * ( 1 - s * f );
    t = v * ( 1 - s * ( 1 - f ) );
    switch( i ) {
        case 0:
            r = v;
            g = t;
            b = p;
            break;
        case 1:
            r = q;
            g = v;
            b = p;
            break;
        case 2:
            r = p;
            g = v;
            b = t;
            break;
        case 3:
            r = p;
            g = q;
            b = v;
            break;
        case 4:
            r = t;
            g = p;
            b = v;
            break;
        default:        // case 5:
            r = v;
            g = p;
            b = q;
            break;
    }
    return [r,g,b];
  }

  static saturateRGB(rgbObj, saturationAmp) {
    var colorHSV = CanvasUtil.rgb2hsv([rgbObj.r, rgbObj.g, rgbObj.b]);
    colorHSV[1] *= saturationAmp;
    colorHSV[1] = Math.max(Math.min(0, colorHSV[1]), 1);
    var colorSaturatedRGB = hsv2rgb(colorHSV);
    return {r: colorSaturatedRGB[0], g: colorSaturatedRGB[1], b: colorSaturatedRGB[2]}
  }


  /**
   *  Returns the percent difference between 2 colors.
   *  @return A difference percentage.
   *  @use    {@code CanvasUtil.rgbDifference(0, 0, 0, 255, 255, 255);}
   */
  static rgbDifference( r1, g1, b1, r2, g2, b2 ) {
    return Math.abs((r1 + g1 + b1) - (r2 + g2 + b2)) / 765;
  }

  /**
   *  Converts a hex color value to a darker or lighter version. Original code from from: http://www.sitepoint.com/javascript-generate-lighter-darker-color/
   *  @return A hex color string.
   *  @use    {@code CanvasUtil.colorLuminance('00ff00', 0.5);}
   */
  static colorLuminance(hex, lum) {
    // validate hex string
    hex = hex.replace( "#", "" );
    hex = String(hex).replace(/[^0-9a-f]/gi, '');
    if (hex.length < 6) {
      hex = hex[0]+hex[0]+hex[1]+hex[1]+hex[2]+hex[2];
    }
    lum = lum || 0;

    // convert to decimal and change luminosity
    var rgb = "#", c, i;
    for (i = 0; i < 3; i++) {
      c = parseInt(hex.substr(i*2,2), 16);
      c = Math.round(Math.min(Math.max(0, c + (c * lum)), 255)).toString(16);
      rgb += ("00"+c).substr(c.length);
    }

    return rgb;
  }
}

console.todo = function(msg){
  console.log( '%c %s %s %s ', 'color: yellow; background-color: black;', '--', msg, '--');
};

console.important = function(msg){
  console.log( '%c%s %s %s', 'color: brown; font-weight: bold; text-decoration: underline;', '--', msg, '--');
};

class DataPrefix {}

DataPrefix.png = 'data:image/png;base64,';
DataPrefix.jpg = 'data:image/jpeg;base64,';
DataPrefix.svg = 'data:image/svg+xml;base64,';
DataPrefix.svgInline = 'data:image/svg+xml;utf8,';
DataPrefix.mp3 = 'data:audio/mpeg;base64,';
DataPrefix.mp4 = 'data:video/mp4;base64,';
DataPrefix.webm = 'data:video/webm;base64,';
DataPrefix.otf = 'data:font/opentype;base64,';
DataPrefix.ttf = 'data:font/truetype;base64,';
DataPrefix.css = 'data:text/css;base64,';
DataPrefix.js = 'data:text/javascript;base64,';
DataPrefix.html = 'data:text/html;charset=utf-8,';  // window.open('data:text/html;charset=utf-8,' + encodeURIComponent('<!DOCTYPE html><html lang="en"><head><title>Embedded Window</title></head><body><h1>42</h1></body></html>'));

class DateUtil {

  static getMillis() {
    return (new Date()).getTime();
  }

  static getTodayTimeStamp() {
    let today = new Date();
    return (today.getYear() + 1900) + '-' + (today.getMonth() + 1) + '-' + today.getDate();
  }

  static datesAreEqual(date1, date2) {
    return date1.getTime() == date2.getTime()
  }

}

DateUtil.midnightTimeSuffix = 'T00:00:00Z';

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

  static stringToElement(str) {
    let doc = new DOMParser().parseFromString(str, 'text/html');
    return doc.body.firstElementChild;
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

class EaseToValueCallback {

  constructor(value = 0, easeFactor = 10, callback = EaseToValueCallback.noop, finishRange = 0.01 ) {
    if(typeof EasingFloat === 'undefined') return console.warn('EaseToValueCallback requires EasingFloat');
    this.easingFloat = new EasingFloat( value, easeFactor );
    this.callback = callback;
    this.finishRange = finishRange;
  };

  setTarget( value ) {
    this.easingFloat.setTarget( value );
    this.easeToTarget();
  };

  setValue( value ) {
    this.easingFloat.setValue( value );
  };

  easeToTarget(){
    this.callback(this.easingFloat.update());
    if( Math.abs(this.easingFloat.value() - this.easingFloat.target() ) > this.finishRange) {   // keep easing if we're not close enough
      requestAnimationFrame(() => { this.easeToTarget(); });
    } else {
      this.easingFloat.setValue( this.easingFloat.target() );
      this.callback(this.easingFloat.value());                                                  // call the callback one last time with the final value
    }
  }
}

EaseToValueCallback.noop = () => {};

class EasingFloat {

  constructor(value = 0, easeFactor = 8, completeRange = 0.001) {
    this.val = value;
    this.targetVal = value;
    this.easeFactor = (easeFactor <= 1) ? 1 / easeFactor : easeFactor;
    this.completeRange = completeRange;
    this.speed = 0;
    this.delay = 0;
  }

  setTarget(value) {
    if(!isNaN(parseFloat(value))) this.targetVal = value;
    return this;
  }

  setValue( value ) {
    this.val = value;
    return this;
  }

  setEaseFactor( easeFactor ) {
    this.easeFactor = (easeFactor <= 1) ? 1 / easeFactor : easeFactor;
    return this;
  }

  setDelay(frames) {
    this.delay = frames;
    return this;
  }

  value() {
    return this.val;
  }

  target() {
    return this.targetVal;
  }

  isComplete() {
    return this.val == this.targetVal;
  }

  update(accelerates=false) {
    // don't do any math if we're already at the destination
    if(this.val == this.targetVal) return;
    if(this.delay > 0) { this.delay--; return; }
    // interpolate
    if(accelerates == false) {
      this.val += (this.targetVal - this.val ) / this.easeFactor;
    } else {
      let increment = (this.targetVal - this.val ) / this.easeFactor;
      if(Math.abs(increment) > Math.abs(this.speed)) {
        this.speed += increment / this.easeFactor;
        increment = this.speed;
      } else {
        this.speed = increment;
      }
      this.val += increment;
    }
    // set the value to the target if we're close enough
    if(Math.abs(this.targetVal - this.val ) < this.completeRange) {
      this.val = this.targetVal;
    }
    return this.val;
  }

  updateRadians(accelerates=false) {
    if(this.val == this.targetVal) return;
    if(this.delay > 0) { this.delay--; return; }

    var angleDifference = this.targetVal - this.val;
    var addToLoop = 0;
    if( angleDifference > Math.PI) {
      addToLoop = -EasingFloat.TWO_PI;
    } else if(angleDifference < -Math.PI ) {
      addToLoop = EasingFloat.TWO_PI;
    }
    if(accelerates == false) {
      this.val += ((this.targetVal - this.val + addToLoop) / this.easeFactor);
    } else {
      let increment = (this.targetVal - this.val + addToLoop) / this.easeFactor;
      if(Math.abs(increment) > Math.abs(this.speed)) {
        this.speed += increment / this.easeFactor;
        increment = this.speed;
      } else {
        this.speed = increment;
      }
      this.val += increment;
    }
    // set the value to the target if we're close enough
    if(Math.abs( this.val - this.targetVal ) < this.completeRange) {
      this.val = this.targetVal;
    }
    return this.val;
  }
}

EasingFloat.TWO_PI = Math.PI * 2;

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

/**
 *  An object that moves a point towards a target, with elastic properties.
 *  @param  x       Starting x coordinate.
 *  @param  y       Starting y coordinate.
 *  @param  fric    Friction value [0-1] - lower numbers mean more friction.
 *  @param  accel   Acceleration value [0-1] - lower numbers mean more slower acceleration.
 *  @return The ElasticFloat public interface.
 *  @use    {@code var _point = new ElasticFloat( 100, 100, 100, 0.75, 0.4 ); }
 */
class ElasticFloat {

  constructor(value = 0, fric = 0.8, accel = 0.2) {
    this.val = value;
    this.fric = fric;
    this.accel = accel;
    this.targetVal = this.val;
    this.speed = 0;
  }

  value() {
    return this.val;
  }

  target() {
    return this.targetVal;
  }

  setCurrent(value) {
    this.val = value;
  }

  setTarget(target) {
    this.targetVal = target;
  }

  setFriction(fric) {
    this.fric = fric;
  }

  setAccel(accel) {
    this.accel = accel;
  }

  update() {
    this.speed = ((this.targetVal - this.val) * this.accel + this.speed) * this.fric;
    this.val += this.speed;
    return this.val;
  }
}

class ErrorUtil {

  static initErrorCatching() {
    window.addEventListener('error', function(error) {
      // get info from error object
      var fileComponents = error.filename.split('/');
      var file = fileComponents[fileComponents.length-1];
      var line = error.lineno;
      var message = error.message;
      var stack = error.error.stack.replace('\n', '<br>');
      // write to error panel
      ErrorUtil.showError(`Message: <b>${message}</b><br>File: ${file}<br>Line: ${line}<br>Stack: ${stack}<br>Error message: ${JSON.stringify(error)}<br>`);
    });
  }

  static showError(message) {
    // lazy-init error element
    var errorContainer = document.querySelector('#inline-error');
    if(!errorContainer) {
      errorContainer = document.createElement('div');
      errorContainer.setAttribute("id", "inline-error");
      errorContainer.setAttribute("style", "box-sizing: border-box; position: absolute; top: 20px; left: 20px; width: calc(100% - 40px); max-height: calc(100% - 40px); background: rgba(0, 0, 0, 0.7); color: #fff; z-index: 99; font-size: 10px; padding: 10px 20px; overflow: auto;");
      document.body.appendChild(errorContainer);

      // click to kill the error alert
      errorContainer.addEventListener('click', function(e) {
        document.body.removeChild(errorContainer);
      });
    }

    // add individual error
    let errorMsgEl = document.createElement('p');
    errorMsgEl.setAttribute("style", "border: 2px solid #ff0000; padding: 20px;");
    errorMsgEl.innerHTML = message;
    errorContainer.appendChild(errorMsgEl);
  }

}

class FloatBuffer {

  constructor(size) {
    this.size = size;
    this.initBuffer();
    this.reset();
  }

  initBuffer() {
    this.sampleIndex = 0;
    this.buffer = [];
    for(let i=0; i < this.size; i++) {
      this.buffer.push(0);
    }
  }

  reset() {
    for(let i=0; i < this.size; i++) {
      this.buffer[i] = 0;
    }
  }

  update(value) {
    this.sampleIndex++;
    if(this.sampleIndex == this.size) this.sampleIndex = 0;
    this.buffer[this.sampleIndex] = value;
  }

  toString() {
    return this.buffer.reduce(function(acc, val) {
      return acc + Math.round(val * 10) / 10 + ', ';
    }, '');
  }

  average() {
    return this.sum() / this.size;
  }

  sum() {
    return this.buffer.reduce(function(acc, val) {
      return acc + val;
    }, 0);
  }

  sumPositive() {
    return this.buffer.reduce(function(acc, val) {
      return (val > 0) ? acc + val : acc;
    }, 0);
  }

  sumNegative() {
    return this.buffer.reduce(function(acc, val) {
      return (val < 0) ? acc + val : acc;
    }, 0);
  }

  sumAbs() {
    return this.buffer.reduce(function(acc, val) {
      return acc + Math.abs(val);
    }, 0);
  }

  max() {
    let max = this.buffer[0];
    for(let i=1; i < this.size; i++) {
      if( this.buffer[i] > max ) max = this.buffer[i];
    }
    return max;
  }

  min() {
    let min = this.buffer[0];
    for(let i=1; i < this.size; i++) {
      if( this.buffer[i] < min ) min = this.buffer[i];
    }
    return min;
  }

  maxAbs() {
    let max = Math.abs(this.buffer[0]);
    for(let i=1; i < this.size; i++) {
      if( Math.abs(this.buffer[i]) > max ) max = Math.abs(this.buffer[i]);
    }
    return max;
  }

}

class FontUtil {

  static printFontInfoOnLoad() {
    document.fonts.onloadingdone = (fontFaceSetEvent) => {
      // log font info
      console.log('`document` loaded ' + fontFaceSetEvent.fontfaces.length + ' font faces:');
      fontFaceSetEvent.fontfaces.forEach((el, i) => {
        console.log('- ', el.family);
      });
    };
  }

}

class GATracking {

  constructor(gaID=null) {
    if(gaID != null) {
      if(gaId.indexOf('UA-') != -1) console.warn('Please only use the numeric GA tracking id');
      // https://developers.google.com/analytics/devguides/collection/analyticsjs/
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
      ga('create', `UA-${gaID}`, 'auto');
      ga('send', 'pageview');
    }
  }

  event(category='test', action='click') {
    // More info: https://developers.google.com/analytics/devguides/collection/analyticsjs/events
    window.ga('send', 'event', category, action);
  }

  page(path=document.location.pathname) {
    // More info: https://developers.google.com/analytics/devguides/collection/analyticsjs/pages
    // More info: https://developers.google.com/analytics/devguides/collection/analyticsjs/single-page-applications
    window.ga('set', 'page', path); // sets the page for a single-page app, so subsequent events are tracked to this page
    window.ga('send', 'pageview');
  }

}


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

class ImprovedNoise {
  // Ported from: http://mrl.nyu.edu/~perlin/noise/
  constructor() {
    this.p = [151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10,
      23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87,
      174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211,
      133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208,
      89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5,
      202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119,
      248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232,
      178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249,
      14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205,
      93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180];

      for (let i = 0; i < 256; i++) {
        this.p[256 + i] = this.p[i];
      }
  }

  fade(t) {
    return t * t * t * (t * (t * 6 - 15) + 10);
  }

  lerp(t, a, b) {
    return a + t * (b - a);
  }

  grad(hash, x, y, z) {
      const h = hash & 15;
      const u = h < 8 ? x : y;
      const v = h < 4 ? y : h == 12 || h == 14 ? x : z;
      return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
  }

  noise(x, y, z) {
    const floorX = ~~x;
    const floorY = ~~y;
    const floorZ = ~~z;
    const X = floorX & 255;
    const Y = floorY & 255;
    const Z = floorZ & 255;

    x -= floorX;
    y -= floorY;
    z -= floorZ;

    const xMinus1 = x - 1;
    const yMinus1 = y - 1;
    const zMinus1 = z - 1;
    const u = this.fade(x);
    const v = this.fade(y);
    const w = this.fade(z);
    const A = this.p[X] + Y;
    const AA = this.p[A] + Z;
    const AB = this.p[A + 1] + Z;
    const B = this.p[X + 1] + Y;
    const BA = this.p[B] + Z;
    const BB = this.p[B + 1] + Z;

    return this.lerp(w, this.lerp(v, this.lerp(u, this.grad(this.p[AA], x, y, z),
           this.grad(this.p[BA], xMinus1, y, z)),
           this.lerp(u, this.grad(this.p[AB], x, yMinus1, z),
           this.grad(this.p[BB], xMinus1, yMinus1, z))),
           this.lerp(v, this.lerp(u, this.grad(this.p[AA + 1], x, y, zMinus1),
           this.grad(this.p[BA + 1], xMinus1, y, z - 1)),
           this.lerp(u, this.grad(this.p[AB + 1], x, yMinus1, zMinus1),
           this.grad(this.p[BB + 1], xMinus1, yMinus1, zMinus1))));
  }
};

class JsonPoller {

  constructor(url, callback, errorCallback) {
    this.url = url;
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.requestCount = 0;
    this.fetchData();
  }

  fetchData() {
    this.requestCount++;
    //  + "&rand="+Math.round(Math.random() * 999999)
    fetch(this.url)
      .then((response) => {
        return response.json();
      }).then((jsonData) => {
        requestAnimationFrame(() => {  // detach from fetch Promise to prevent Error-throwing
          this.callback(jsonData);
          this.fetchData();
        });
      }).catch((error) => {
        this.errorCallback(error);
        this.fetchData();
      });
  }

}

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

// A wrapper for LinearFloat that allows us to consistently move towards a new target in the same amount of linear steps.
// Possibly useful.

class LinearFloatTo {

  constructor(val=0, step=0.025) {
    this.startVal = val;
    this.endVal = val;
    this.progress = new LinearFloat(0, step);
  }

  update() {
    this.progress.update();
  }

  value() {
    return this.map(this.progress.value(), 0, 1, this.startVal, this.endVal)
  }

  valuePenner(equation) {
    return this.map(this.progress.valuePenner(equation), 0, 1, this.startVal, this.endVal)
  }

  setTarget(val) {
    if(val == this.endVal) return;
    this.startVal = this.value();
    this.endVal = val;
    this.progress.setValue(0);
    this.progress.setTarget(1);
  }

  map(val, inputMin, inputMax, outputMin, outputMax) {
    return (outputMax - outputMin) * ((val - inputMin) / (inputMax - inputMin)) + outputMin;
  }

}

class LinearFloat {

  constructor(value = 0, inc = 0.025) {
    this.val = value;
    this.targetVal = value;
    this.inc = inc;
    this.delay = 0;
  }

  setValue( value ) {
  	this.val = value;
    return this;
  }

  setTarget( value ) {
  	this.targetVal = value;
    return this;
  }

  setInc( value ) {
  	this.inc = value;
    return this;
  }

  setDelay(frames) {
		this.delay = frames;
		return this;
	}

  value() {
  	return this.val;
  }

  valuePenner(equation) { // requires an equation from Penner class
  	return equation(this.val, 0, 1, 1);
  }

  valueMapped(min, max) { // requires an equation from Penner class
  	return this.map(this.val, 0, 1, min, max);
  }

  map(val, inputMin, inputMax, outputMin, outputMax) {
    return (outputMax - outputMin) * ((val - inputMin) / (inputMax - inputMin)) + outputMin;
  }

  target() {
  	return this.targetVal;
  }

  isComplete() {
    return this.val == this.targetVal;
  }

  update() {
    if(this.delay > 0) { this.delay--; return; }
  	if( this.val != this.targetVal ) {
  		var reachedTarget = false;
  		if( this.val < this.targetVal ) {
  			this.val += this.inc;
  			if( this.val > this.targetVal ) reachedTarget = true;
  		} else {
  			this.val -= this.inc;
  			if( this.val < this.targetVal ) reachedTarget = true;
  		}
  		if( reachedTarget == true ) {
  			this.val = this.targetVal;
  		}
  	}
    return this.val;
  }

}

class MathUtil {


  /**
   *  Gives you the scale at which to apply to the current value, to reach the target value
   *  @param  cur The original value.
   *  @param  target The target value.
   *  @return The scale to apply to cur to equal target.
   *  @use    {@code let scale = MathUtil.scaleToTarget( 20, 100 );}
   */
  static scaleToTarget(cur, target) {
    return target / cur;
  }

  /**
   *  Gives you the scale at which to apply to the current value, to reach the target value
   *  @param  distance The distance to travel.
   *  @param  friction Friction to apply every frame before moving.
   *  @return The starting speed, after which friction is multplied every frame.
   *  @use    {@code let speed = MathUtil.speedToReachDestinationWithFriction( 20, 0.9 );}
   */
  static speedToReachDestinationWithFriction(distance, friction) {
    return distance / ( ( friction ) * ( 1 / ( 1 - friction ) ) );
  }

  /**
   *  Calculates a random number within a minimum and maximum range.
   *  @param  min the value for the bottom range.
   *  @param  max the value for the upper range.
   *  @return the random number within the range.
   *  @use    {@code let vRandRange = MathUtil.randRange( 0, 999999 );}
   */
  static randRange(min, max) {
    return Math.round( Math.random() * ( max - min ) ) + min;
  }

  /**
   *  Calculates a random number within a minimum and maximum range.
   *  @param  min   the value for the bottom range.
   *  @param  max   the value for the upper range.
   *  @return the random number within the range.
   *  @use    {@code let vRandRange = MathUtil.randRange( 0, 999999 );}
   */
  static randRangeDecimel(min, max) {
    return Math.random() * (max - min) + min;
  }

  static randBoolean() {
    return (Math.random() > 0.5 ) ? true : false;
  }

  static randBooleanWeighted(likeliness) {
    return (Math.random() < likeliness ) ? true : false;
  }

  /**
   *  Returns a percentage of a value in between 2 other numbers.
   *  @param  bottomRange   low end of the range.
   *  @param  topRange      top end of the range.
   *  @param  valueInRange  value to find a range percentage of.
   *  @return The percentage [0-1] of valueInRange in the range.
   *  @use    {@code let vPercent = MathUtil.getPercentWithinRange( 50, 150, 100 );  // displays 50 }
   */
  static getPercentWithinRange( bottomRange, topRange, valueInRange ) {
    // normalize values to work positively from zero
    topRange += -bottomRange;
    valueInRange += -bottomRange;
    bottomRange += -bottomRange;  // last to not break other offsets
    // return percentage or normalized values
    return ( valueInRange / ( topRange - bottomRange ) );
  }

  static lerp(val1, val2, percent) {
      // 0.5 percent is an even mix
      return val1 + (val2 - val1) * percent;
  }

  static map(val, inputMin, inputMax, outputMin, outputMax) {
    return ((outputMax - outputMin) * ((val - inputMin)/(inputMax - inputMin))) + outputMin;
  }

  /**
   *  Returns a percentage of a value in between 2 other numbers.
   *  @param  inputNum   The number to round.
   *  @param  numPoints  Number of decimal points to round to.
   *  @return The rounded number.
   *  @use    {@code let roundedNum = MathUtil.roundToDecimal( 10.3333, 1 );  // displays 10.3 }
   */
  static roundToDecimal( inputNum, numPoints ) {
    let multiplier = Math.pow( 10, numPoints );
    return Math.round( inputNum * multiplier ) / multiplier;
  }

  /**
   *  Ease a number towards a target.
   *  @param  current     number (0)
   *  @param  target      number (100)
   *  @param  easeFactor  number (2)
   *  @return number 50
   *  @use    {@code let vRadians = MathUtil.easeTo( 0, 100, 2 );}
   */
  static easeTo( current, target, easeFactor ) {
    return current -= ( ( current - target ) / easeFactor );
  }

  /**
   *  Convert a number from Degrees to Radians.
   *  @param  d degrees (45°, 90°)
   *  @return radians (3.14..., 1.57...)
   *  @use    {@code let vRadians = MathUtil.degreesToRadians( 180 );}
   */
  static degreesToRadians( d ) {
    return d * ( Math.PI / 180 );
  }

  /**
   *  Convert a number from Radians to Degrees.
   *  @param  r radians (3.14..., 1.57...)
   *  @return degrees (45°, 90°)
   *  @use    {@code let vDegrees = MathUtil.radiansToDegrees( 3.14 );}
   */
  static radiansToDegrees( r ) {
    return r * ( 180 / Math.PI );
  }

  /**
   *  Convert a number from a Percentage to Degrees (based on 360°).
   *  @param  n percentage (1, .5)
   *  @return degrees (360°, 180°)
   *  @use    {@code let vDegreesPercent = MathUtil.percentToDegrees( 50 );}
   */
  static percentToDegrees( n ) {
    return Math.abs( n ) * 360;
  }

  /**
   *  Convert a number from Degrees to a Percentage (based on 360°).
   *  @param  n degrees (360°, 180°)
   *  @return percentage (1, .5)
   *  @use    {@code let vPercentDegrees = MathUtil.degreesToPercent( 180 );}
   */
  static degreesToPercent( n ) {
    return Math.abs( n / 360 );
  }

  static saw( rads ) {
    let val = Math.abs((rads % (Math.PI * 2)) - Math.PI);
    return (val / Math.PI) * 2 - 1;
  }

  /**
   *  Rips through an indexed array of numbers adding the total of all values.
   *  @param  nums  an array of numbers.
   *  @return the sum of all numbers.
   *  @use    {@code let vSums = MathUtil.sums( [ 12, 20, 7 ] );}
   */
  static sums( nums ) {
    // declare locals.
    let sum = 0;
    let numL = nums.length;

    // loop: convert and add.
    for( let i = 0; i < numL; i++ ) {
      sum += nums[ i ];
    }
    return sum;
  }

  /**
   *  Report the average of an array of numbers.
   *  @param  nums  an array of numbers.
   *  @return the average of all numbers.
   *  @use    {@code let vAverage = MathUtil.average( [ 12, 20, 7 ] );}
   */
  static average( nums ) {
    return MathUtil.sums( nums ) / nums.length;
  }

  /**
   *  Linear interpolate between two values.
   *  @param  lower first value (-1.0, 43.6)
   *  @param  upper second value (-100.0, 3.1415)
   *  @param  n     point between values (0.0, 1.0)
   *  @return number (12.3, 44.555)
   *  @use    {@code let value = MathUtil.interp( 10, 20, .5 );  //returns 15}
   */
  static interp( lower, upper, n ) {
    return ((upper-lower)*n)+lower;
  }

  /**
   *  Re-maps a number from one range to another.
   *  @param  value  The incoming value to be converted
   *  @param  lower1 Lower bound of the value's current range
   *  @param  upper1 Upper bound of the value's current range
   *  @param  lower2 Lower bound of the value's target range
   *  @param  upper2 Upper bound of the value's target range
   *  @return number (12.3, 44.555)
   *  @use    {@code let value = MathUtil.remap( 10, 0, 20, 1, 2 );  //returns 1.5}
   */
  static remap( value, lower1, upper1, lower2, upper2 ) {
    return MathUtil.interp(lower2,upper2, MathUtil.getPercentWithinRange(lower1,upper1,value));
  }

  /**
   *  Get distance between 2 points with the pythagorean theorem.
   *  @param  x1  first point's x position
   *  @param  y1  first point's y position
   *  @param  x2  second point's x position
   *  @param  y2  second point's y position
   *  @return The distance between point 1 and 2
   *  @use    {@code let distance = MathUtil.getDistance( 7, 5, 3, 2 );}
   */
  static getDistance ( x1, y1, x2, y2 ) {
    let a = x1 - x2;
    let b = y1 - y2;
    return Math.abs( Math.sqrt(a*a + b*b) );
  }


  static smoothstep(edge0, edge1, x) {
    x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return x * x * (3 - 2 * x);
  }


  /**
   *  Keep a value between a min & max.
   *  @param  val  The value to clamp
   *  @param  min  The minimum value
   *  @param  max  The maximum value
   *  @return The clamped value
   *  @use    {@code let singleDigit = MathUtil.getDistance( value, 0, 9 );}
   */
  static clamp ( val, min, max ) {
    return Math.max(min, Math.min(max, val));
  }


  /**
   *  Keep an angle between 0-360
   *  @param  angle the angle to constrain
   *  @return The normalized angle
   *  @use    {@code let angle = MathUtil.constrainAngle( 540 );}
   */
  static constrainAngle( angle ) {
    if( angle < 0 ) return angle + 360;
    if( angle > 360 ) return angle - 360;
    return angle;
  }

  /**
   *  Keep an angle between 0-360
   *  @param  angle the angle to constrain
   *  @return The normalized angle
   *  @use    {@code let angle = MathUtil.constrainAngle( 540 );}
   */
  static constrainRadians( radians ) {
    if( radians < 0 ) return radians + Math.PI*2;
    if( radians > Math.PI*2 ) return radians - Math.PI*2;
    return radians;
  }

  /**
   *  Get the angle fron current coordinate to target coordinate
   *  @param  x1  first point's x position
   *  @param  y1  first point's y position
   *  @param  x2  second point's x position
   *  @param  y2  second point's y position
   *  @return The angle from point 1 and 2
   *  @use    {@code let angle = MathUtil.getAngleToTarget( 0, 0, 5, 5 );}
   */
  static getAngleToTarget( x1, y1, x2, y2 ) {
    return MathUtil.constrainAngle( -Math.atan2( x1 - x2, y1 - y2 ) * 180 / Math.PI );
  }

  /**
   *  Get the radians fron current coordinate to target coordinate
   *  @param  x1  first point's x position
   *  @param  y1  first point's y position
   *  @param  x2  second point's x position
   *  @param  y2  second point's y position
   *  @return The radians from point 1 and 2
   *  @use    {@code let angle = MathUtil.getRadiansToTarget( 0, 0, 5, 5 );}
   */
  static getRadiansToTarget( x1, y1, x2, y2 ) {
    return (MathUtil.TWO_PI + -Math.PI / 2 + Math.atan2(x2 - x1, y2 - y1)) % MathUtil.TWO_PI;
  }
  static getRadiansToTarget2( x1, y1, x2, y2 ) {
    return -Math.atan2(y2 - y1, x2 - x1) % MathUtil.TWO_PI;
  }

  /**
   *  Figures out which way to rotate for the shortest path from current to target angle
   *  @param  curAngle    starting angle
   *  @param  targetAngle destination angle
   *  @return +1 fo clockwise, -1 for counter-clockwise
   *  @use    {@code let direction = MathUtil.rotationDirectionToTarget( 90, 180 );}
   */
  static getRotationDirectionToTarget( curAngle, targetAngle ) {
    // calculate the difference between the current angle and destination angle
    let angleDifference = Math.abs( curAngle - targetAngle );
    // turn left or right to get to the target
    if( curAngle > targetAngle ){
      return (angleDifference < 180) ? -1 : 1;
    } else {
      return (angleDifference < 180) ? 1 : -1;
    }
  }


  static circleRadiusToEnclosingSquareCorner( squareSize ) {
    return (squareSize/2)*(Math.sqrt(2)-1);
  }

  static rectsIntersect(a, b) {
    return (a.left <= b.right &&
            b.left <= a.right &&
            a.top <= b.bottom &&
            b.top <= a.bottom);
  }

}

  // static saw(rads) {
  //   let val = Math.abs((rads % (Math.PI * 2)) - Math.PI);
  //   return (val / Math.PI) * 2 - 1;
  // };

MathUtil.TWO_PI = Math.PI * 2;

/*
Main.prototype = {
// from: http://codepen.io/RobertMulders/pen/gPYqaR
  init: function() {
    waves.push(new WaveForm(
      'sawtooth',
      i => i % maxValue
    ));

    waves.push(new WaveForm(
      'square',
      i => (i % maxValue * 2 < maxValue) ? maxValue : 0
    ));

    waves.push(new WaveForm(
      'triangle',
      i => Math.abs((i % (maxValue * 2)) - maxValue)
    ));

    waves.push(new WaveForm(
      'sine',
      i => maxValue / 2 * Math.sin(i / 25) + maxValue / 2
    ));

*/

class MicrophoneNode {

  constructor(context, callback, errorCallback) {
    this.context = context || new AudioContext();
    navigator.mediaDevices.getUserMedia({audio: true})
    .then((stream) => {
      this.micNode = this.context.createMediaStreamSource(stream);
      window.micNode = this.micNode; // fix for FF bug: https://stackoverflow.com/questions/22860468/html5-microphone-capture-stops-after-5-seconds-in-firefox
      callback(this.micNode);
    })
    .catch((err) => {
      if(errorCallback) errorCallback(err);
      else console.log('The following getUserMedia error occured: ' + err);
    });
  }

  getNode() {
    return this.micNode;
  }
  getContext() {
    return this.context;
  }

}

class MobileUtil {

  // TOUCHSCREEN HELPERS
  static isFullscreenApp() {
    return window.navigator.standalone;
  }

  static enablePseudoStyles() {
    document.addEventListener("touchstart", function(){}, false);
  }

  static setDeviceInputClass() {
    const deviceClass = (MobileUtil.isMobileBrowser()) ? 'mobile' : 'desktop';
    document.body.classList.add(deviceClass);
  }

  static lockTouchScreen( isLocked ) {
    if( isLocked == false ) {
      document.ontouchmove = null;
    } else {
      document.ontouchmove = function( event ) {
        event.preventDefault();
      };
    }
  }

  static disableZoom() {
    document.addEventListener('touchmove', function(event) {
      event = event.originalEvent || event;
      if(event.scale != 1) {
        event.preventDefault();
      }
    }, false);
  }

  static disableTrackpadZoom() {
    window.addEventListener('mousewheel', function(e) {
      if(e.ctrlKey) {
        e.preventDefault();
      }
    });
  }

  static hideSoftKeyboard() {
    if(document.activeElement && document.activeElement.blur) document.activeElement.blur()
    var inputs = document.querySelectorAll('input');
    for (var i = 0; i < inputs.length; i++) inputs[i].blur();
  }

  static unlockWebAudioOnTouch() {
    window.addEventListener('touchstart', MobileUtil.playEmptyWebAudioSound);
  }

  static playEmptyWebAudioSound() {
    var myContext = (AudioContext != null) ? new AudioContext() : new webkitAudioContext(); // create empty buffer and play it
    var buffer = myContext.createBuffer(1, 1, 44100);
    var source = myContext.createBufferSource();
    source.buffer = buffer;
    source.connect(myContext.destination);                                                  // connect to output (your speakers)
    source.start();                                                                         // play the file
    window.removeEventListener('touchstart', MobileUtil.playEmptyWebAudioSound);            // clean up the event listener
  }

  // PLATFORM DETECTION

  static isMobileBrowser() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/android|iphone|ipad|ipod/i)) return true;
    return false;
  }

  static isIOS() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/iphone/i)) return true;
    if(userAgent.match(/ipad/i)) return true;
    if(userAgent.match(/ipod/i)) return true;
    if(userAgent.match(/crios/i)) return true;
    return false;
  }

  static isIPhone() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/iphone/i)) return true;
    if(userAgent.match(/ipod/i)) return true;
    return false;
  }

  static isAndroid() {
    var userAgent = navigator.userAgent.toLowerCase()
    if(userAgent.match(/android/i)) return true;
    return false;
  }

  static isSafari() {
    var userAgent = navigator.userAgent.toLowerCase()
    var isChrome = (userAgent.match(/chrome/i)) ? true : false;
    var isSafari = (userAgent.match(/safari/i)) ? true : false;
    return (isSafari == true && isChrome == false) ? true : false;
  }

  static isIE11() {
    return !!window.MSInputMethodContext && !!document.documentMode;
  }

  // MOBILE HELPERS

  static alertErrors() {
    // alert errors on mobile to help detect bugs
    if(!window.addEventListener) return;
    window.addEventListener('error', (e) => {
      var fileComponents = e.filename.split('/');
      var file = fileComponents[fileComponents.length-1];
      var line = e.lineno;
      var message = e.message;
      alert('ERROR\n'+'Line '+line+' in '+file+'\n'+message);
    });
  };

  static openNewWindow(href) {
    // gets around native mobile popup blockers
    var link = document.createElement('a');
    link.setAttribute('href', href);
    link.setAttribute('target','_blank');
    var clickevent = document.createEvent('Event');
    clickevent.initEvent('click', true, false);
    link.dispatchEvent(clickevent);
    return false;
  }

}

class ObjectPool {

  // An object pool that grows as needed.
  // Contract:
  // * Object has a public `active` boolean
  // * Init by passing in Class definition: `new ObjectPool(GameSphere)`

  constructor(klass) {
    this.klass = klass;
    this.objects = [];
  }

  pool() {
    return this.objects;
  }

  getObject() {
    // try to find an inactive object
    let freeObject = this.objects.find((el) => {
      return el.active == false;
    });
    if(freeObject) {
      return freeObject;
    } else {
      this.objects.push(new this.klass());
      return this.objects[this.objects.length - 1];
    }
  }

  anyActive() {
    return null != this.objects.find((el) => {
      return el.active == true;
    });
  }

}

class Oscillations {

  // borrowed from: https://soulwire.co.uk/math-for-motion/

  static osc1(t) {
    return Math.pow(Math.sin(t), 3);
  }

  static osc2(t) {
    return Math.pow(Math.sin(t * Math.PI), 12);
  }

  static osc3(t) {
    return Math.sin(Math.tan(Math.cos(t) * 1.2));
  }

}

if(window.p5) {
  class P {}
  P.p = null;
  window.P = P;
  window.p = null;

  class P5Sketch extends p5 {

    constructor(el=document.body, id='p5') {
      super(p => {
        // do any setup in here that needs to happen before the sketch starts
        // (e.g. create event handlers)
        // `p` refers to the instance that becomes `this` after the super() call
      });
      P.p = window.p = this;
      this.setup = this.setup.bind(this);
      this.draw = this.draw.bind(this);
      // this.start = this.start.bind(this);
      // this.stop = this.stop.bind(this);
      this.windowResized = this.windowResized.bind(this);

      // store elements
      this.el = el;
      this.elSize = this.el.getBoundingClientRect();
    }

    setup() {
      this.createCanvas(this.elSize.width, this.elSize.height, p5.prototype.WEBGL);
      this.el.appendChild(this.canvas);
      this.background(0);
      this.setupFirstFrame();
    }

    setupFirstFrame() {
      // override this!
    }

    draw() {
      // override this!
      this.background(127 + 127 * Math.sin(this.frameCount * 0.01));
    }

    windowResized() {
      this.elSize = this.el.getBoundingClientRect();
      this.resizeCanvas(this.elSize.width, this.elSize.height);
    }

    // addFrameListener(fn) {
    //   this.app.ticker.add(fn);
    // }

    width() {
      return this.elSize.width;
    }

    height() {
      return this.elSize.height;
    }

  }

  P5Sketch.defaultVertShader = `
  // vertex shader
  attribute vec3 aPosition;
  attribute vec2 aTexCoord;
  varying vec2 vTexCoord;
  void main() {
    vTexCoord = aTexCoord;
    vec4 positionVec4 = vec4(aPosition, 1.0);
    positionVec4.xy = positionVec4.xy * 2.0 - 1.0;
    // positionVec4.xy = positionVec4.xy - 0.5;
    gl_Position = positionVec4;
  }
  `;

  window.P5Sketch = P5Sketch;
}

class PageVisibility {

  constructor(activeCallback, inactiveCallback) {
    this.activeCallback = activeCallback || null;
    this.inactiveCallback = inactiveCallback || null;
    this.getPrefix();
    this.initPageVisibilityApi();
  }

  // from: http://www.sitepoint.com/introduction-to-page-visibility-api/
  getPrefix() {
    this.prefix = null;
    if (document.hidden !== undefined)
      this.prefix = "";
    else {
      var browserPrefixes = ["webkit","moz","ms","o"];
      // Test all vendor prefixes
      for(var i = 0; i < browserPrefixes.length; i++) {
        if (document[browserPrefixes[i] + "Hidden"] != undefined) {
          this.prefix = browserPrefixes[i];
          break;
        }
      }
    }
  }

  updateState(e) {
    if (document.hidden === false || document[this.prefix + "Hidden"] === false) {
      if(this.activeCallback != null) this.activeCallback();
    } else {
      if(this.inactiveCallback != null) this.inactiveCallback();
    }
  }

  initPageVisibilityApi() {
    if (this.prefix === null)
      console.log( "Your browser does not support Page Visibility API");
    else {
      document.addEventListener(this.prefix + "visibilitychange", (e) => this.updateState(e));
    }
  }

}

// From: https://github.com/danro/jquery-easing/blob/master/jquery.easing.js
// t: current time, b: begInnIng value, c: change In value, d: duration
// Penner.easeInOutCirc(val, 0, 1, 1)
class Penner {

  static easeInQuad(t, b, c, d) {
    return c*(t/=d)*t + b;
  }

  static easeOutQuad(t, b, c, d) {
    return -c *(t/=d)*(t-2) + b;
  }

  static easeInOutQuad(t, b, c, d) {
    if ((t/=d/2) < 1) return c/2*t*t + b;
    return -c/2 * ((--t)*(t-2) - 1) + b;
  }

  static easeInCubic(t, b, c, d) {
    return c*(t/=d)*t*t + b;
  }

  static easeOutCubic(t, b, c, d) {
    return c*((t=t/d-1)*t*t + 1) + b;
  }

  static easeInOutCubic(t, b, c, d) {
    if ((t/=d/2) < 1) return c/2*t*t*t + b;
    return c/2*((t-=2)*t*t + 2) + b;
  }

  static easeInQuart(t, b, c, d) {
    return c*(t/=d)*t*t*t + b;
  }

  static easeOutQuart(t, b, c, d) {
    return -c * ((t=t/d-1)*t*t*t - 1) + b;
  }

  static easeInOutQuart(t, b, c, d) {
    if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
    return -c/2 * ((t-=2)*t*t*t - 2) + b;
  }

  static easeInQuint(t, b, c, d) {
    return c*(t/=d)*t*t*t*t + b;
  }

  static easeOutQuint(t, b, c, d) {
    return c*((t=t/d-1)*t*t*t*t + 1) + b;
  }

  static easeInOutQuint(t, b, c, d) {
    if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
    return c/2*((t-=2)*t*t*t*t + 2) + b;
  }

  static easeInSine(t, b, c, d) {
    return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
  }

  static easeOutSine(t, b, c, d) {
    return c * Math.sin(t/d * (Math.PI/2)) + b;
  }

  static easeInOutSine(t, b, c, d) {
    return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
  }

  static easeInExpo(t, b, c, d) {
    return (t==0) ? b : c * Math.pow(2, 10 * (t/d - 1)) + b;
  }

  static easeOutExpo(t, b, c, d) {
    return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
  }

  static easeInOutExpo(t, b, c, d) {
    if (t==0) return b;
    if (t==d) return b+c;
    if ((t/=d/2) < 1) return c/2 * Math.pow(2, 10 * (t - 1)) + b;
    return c/2 * (-Math.pow(2, -10 * --t) + 2) + b;
  }

  static easeInCirc(t, b, c, d) {
    return -c * (Math.sqrt(1 - (t/=d)*t) - 1) + b;
  }

  static easeOutCirc(t, b, c, d) {
    return c * Math.sqrt(1 - (t=t/d-1)*t) + b;
  }

  static easeInOutCirc(t, b, c, d) {
    if ((t/=d/2) < 1) return -c/2 * (Math.sqrt(1 - t*t) - 1) + b;
    return c/2 * (Math.sqrt(1 - (t-=2)*t) + 1) + b;
  }

  static easeInElastic(t, b, c, d) {
    var s=1.70158;var p=0;var a=c;
    if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
    if (a < Math.abs(c)) { a=c; var s=p/4; }
    else var s = p/(2*Math.PI) * Math.asin (c/a);
    return -(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
  }

  static easeOutElastic(t, b, c, d) {
    var s=1.70158;var p=0;var a=c;
    if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
    if (a < Math.abs(c)) { a=c; var s=p/4; }
    else var s = p/(2*Math.PI) * Math.asin (c/a);
    return a*Math.pow(2,-10*t) * Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
  }

  static easeInOutElastic(t, b, c, d) {
    var s=1.70158;var p=0;var a=c;
    if (t==0) return b;  if ((t/=d/2)==2) return b+c;  if (!p) p=d*(.3*1.5);
    if (a < Math.abs(c)) { a=c; var s=p/4; }
    else var s = p/(2*Math.PI) * Math.asin (c/a);
    if (t < 1) return -.5*(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
    return a*Math.pow(2,-10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )*.5 + c + b;
  }

  static easeInBack(t, b, c, d, s) {
    if (s == undefined) s = 1.70158;
    return c*(t/=d)*t*((s+1)*t - s) + b;
  }

  static easeOutBack(t, b, c, d, s) {
    if (s == undefined) s = 1.70158;
    return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
  }

  static easeInOutBack(t, b, c, d, s) {
    if (s == undefined) s = 1.70158;
    if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525))+1)*t - s)) + b;
    return c/2*((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2) + b;
  }

  static easeOutBounce(t, b, c, d) {
    if ((t/=d) < (1/2.75)) {
      return c*(7.5625*t*t) + b;
    } else if (t < (2/2.75)) {
      return c*(7.5625*(t-=(1.5/2.75))*t + .75) + b;
    } else if (t < (2.5/2.75)) {
      return c*(7.5625*(t-=(2.25/2.75))*t + .9375) + b;
    } else {
      return c*(7.5625*(t-=(2.625/2.75))*t + .984375) + b;
    }
  }
}

class PixiSpriteScale {

  static scaleToFillContainer(sprite, width, height) {
    const ratioX = width / sprite.texture.width;
    const ratioY = height / sprite.texture.height;
    const scale = (ratioX > ratioY) ? ratioX : ratioY;
    sprite.scale.set(scale, scale);
    return scale;
  }

  static scaleToFillContainerMult(sprite, width, height, scaleMult) {
    const ratioX = width / sprite.texture.width;
    const ratioY = height / sprite.texture.height;
    let scale = (ratioX > ratioY) ? ratioX : ratioY;
    scale *= scaleMult;
    sprite.scale.set(scale, scale);
    return scale;
  }

  static scaleToLetterboxContainer(sprite, width, height) {
    const ratioX = width / sprite.texture.width;
    const ratioY = height / sprite.texture.height;
    const scale = (ratioX < ratioY) ? ratioX : ratioY;
    sprite.scale.set(scale, scale);
    return scale;
  }

  static scaleToHeight(sprite, height) {
    const scale = height / sprite.texture.height;
    sprite.scale.set(scale, scale);
    return scale;
  }

  static scaleToWidth(sprite, width) {
    const scale = width / sprite.texture.width;
    sprite.scale.set(scale, scale);
    return scale;
  }

}

class PixiStageV4 {

  constructor(el, bgColor, id) {
    this.el = el;
    this.elSize = this.el.getBoundingClientRect();
    this.devicePixelRatio = window.devicePixelRatio;
    PIXI.settings.PRECISION_FRAGMENT = 'highp'; //this makes text looks better
    this.renderer = PIXI.autoDetectRenderer(this.elSize.width, this.elSize.height, {
      backgroundColor: bgColor,
      transparent: false,
      resolution: this.devicePixelRatio
    });
    // this.renderer.roundPixels = true; //and this too
    // this.pixiApp = new PIXI.Application(); // alternate/new PIXI app/renderer. info: http://pixijs.download/dev/docs/PIXI.Application.html
    // this.el.appendChild(app.view);
    this.renderer.view.classList.add(id);
    this.el.appendChild(this.renderer.view);
    this.stage = new PIXI.Container();
    this.stage.interactive = true;
    this.resize();
  }

  container() {
    return this.stage;
  }

  width() {
    return this.renderer.width / this.devicePixelRatio;
  }

  height() {
    return this.renderer.height / this.devicePixelRatio;
  }

  render() {
    this.renderer.render(this.stage);
  }

  resize() {
    this.elSize = this.el.getBoundingClientRect();
    this.renderer.resize(this.elSize.width, this.elSize.height);
  }

}

class PixiStage {

  constructor(el=document.body, bgColor=0x000000, id='pixi') {
    // store elements
    this.el = el;
    this.elSize = this.el.getBoundingClientRect();
    this.devicePixelRatio = window.devicePixelRatio || 1;
    // PIXI.settings.PRECISION_FRAGMENT = 'highp'; //this makes text looks better

    // create app
    this.app = new PIXI.Application({
        width: this.elSize.width,
        height: this.elSize.height,
        backgroundColor: bgColor,
        transparent: false,
        resizeTo: this.el,
        autoDensity: true,
        antialias: true,
        resolution: this.devicePixelRatio,
    });

    el.appendChild(this.app.view);
    this.rootContainer = new PIXI.Container();
    this.app.stage.addChild(this.rootContainer);
    // this.app.stage.interactive = true;
  }

  addFrameListener(fn) {
    this.app.ticker.add(fn);
  }

  removeFrameListener(fn) {
    this.app.ticker.remove(fn);
  }

  stage() {
    return this.app.stage;
  }

  container() {
    return this.rootContainer;
  }

  width() {
    return this.app.renderer.width / this.devicePixelRatio;
  }

  widthRenderer() {
    return this.app.renderer.width;
  }

  height() {
    return this.app.renderer.height / this.devicePixelRatio;
  }

  heightRenderer() {
    return this.app.renderer.height;
  }

  fps() {
    return this.app.ticker.FPS;
  }
}

class PixiTextureLoader {

  constructor(assetsLoadedCallback, keepFinalCallback=false) {
    this.assetsLoadedCallback = assetsLoadedCallback;
    this.keepFinalCallback = keepFinalCallback;
    this.numTextures = 0;
    this.numLoaded = 0;
  }

  loadTexture(filePath, callback) {
    const texture = PIXI.Texture.fromImage(filePath);
    if(texture.width >= 2) {
      texture.baseTexture.scaleMode = PIXI.settings.SCALE_MODE;
      callback(new PIXI.Sprite(texture));
    } else {
      this.numTextures++;
      texture.baseTexture.on('loaded', () => {
        texture.baseTexture.scaleMode = PIXI.settings.SCALE_MODE; // NEAREST;
        callback(new PIXI.Sprite(texture));
        this.numLoaded++
        if(this.numLoaded == this.numTextures) {
          if(this.assetsLoadedCallback) this.assetsLoadedCallback();
          if(this.keepFinalCallback == false) this.assetsLoadedCallback = null;
        }
      });
    }
  }

  loadTextures(filePathsArr, callback) {
    let numLoaded = 0;
    const spritesArr = new Array(filePathsArr.length);
    for(let i=0; i < filePathsArr.length; i++) {
      (() => {
        const index = i;
        const file = filePathsArr[index];
        this.loadTexture(file, (sprite) => {
          spritesArr[index] = sprite;
          numLoaded++;
          if(numLoaded >= filePathsArr.length) callback(spritesArr);
        });
      })();
    }
  }

  static svgElToPixiTexture(el, callback) {
    const base64Img = SVGUtil.svgElToBase64(el);
    const texture = PIXI.Texture.fromImage(base64Img);
    texture.baseTexture.on('loaded', () => {
      callback(new PIXI.Sprite(texture));
    });
  }

}

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

class ShareOut {

  constructor() {

  }

  closest(element, tagname) {
    tagname = tagname.toLowerCase();
    while (true) {
      if (element.nodeName.toLowerCase() === tagname) return element;
      if (!(element = element.parentNode)) break;
    }
    return null;
  }

  clickedShareContainer(e) {
    e.preventDefault();
    let clickedEl = this.closest(e.target, 'a') || this.closest(e.target, 'button');
    if(clickedEl) {
      this.sharePostToService(clickedEl.getAttribute('data-network'), clickedEl.getAttribute('href'));
    }
  }

  sharePostToService(network, url) {
    let height, width;
    if (network === 'email') {
      return this.sharePostToEmail(url);
    }
    width = ShareOut.networkShareDimensions[network].width || 700;
    height = ShareOut.networkShareDimensions[network].height || 450;
    // document.getElementById('share-out-frame').src = url;
    return window.open(url, "_blank", "width=" + width + ", height=" + height + ", left=" + (window.innerWidth / 2 - width / 2) + ", top=" + (window.innerHeight / 2 - height / 2) + ", toolbar=0, location=0, menubar=0, directories=0, scrollbars=0");
  }

  sharePostToEmail(url) {
    document.location.href = url;
  }

  setShareLinks(container, url, summary, img) {
    var emailBody, emailSummary, emailSummarySafe, encodedLinebreak, summaryArr, summarySafe, tweetSummary, tweetSummarySafe, urlSafe;
    encodedLinebreak = "%0D%0A";
    summarySafe = window.encodeURIComponent(summary.trim());
    urlSafe = (url && url.length > 0) ? window.encodeURIComponent(url) : "";

    emailSummary = summary;
    emailSummarySafe = window.encodeURIComponent(emailSummary);
    emailBody = "" + summarySafe + encodedLinebreak + encodedLinebreak + urlSafe;
    tweetSummary = summary;
    if (tweetSummary.length + urlSafe.length > 139) {
      tweetSummary = tweetSummary.substr(0, 139 - 24);
      summaryArr = tweetSummary.split(' ');
      summaryArr.pop();
      tweetSummary = summaryArr.join(' ');
    }
    tweetSummarySafe = window.encodeURIComponent(tweetSummary);

    if(container.querySelector('[data-network="email"]'))
      container.querySelector('[data-network="email"]').setAttribute('href', "mailto:?subject=" + emailSummarySafe + "&body=" + emailBody);
    if(container.querySelector('[data-network="facebook"]')) {
      var fbEl = container.querySelector('[data-network="facebook"]');
      if(fbEl.getAttribute('data-facebook-quote') != null) {
        fbEl.setAttribute('href', "https://www.facebook.com/sharer/sharer.php?quote="+summarySafe+"&u=" + urlSafe);
      } else {
        fbEl.setAttribute('href', "https://www.facebook.com/sharer/sharer.php?u=" + urlSafe);
      }
    }
    // container.querySelector('[data-network="facebook"]').setAttribute('href', "http://www.facebook.com/sharer.php?s=100&p[title]" + summarySafe + "&p[url]=" + urlSafe + "&p[images][0]=" + img);
    // container.querySelector('[data-network="facebook"]').setAttribute('href', "https://www.facebook.com/dialog/feed?display=popup&caption="+summarySafe+"&link="+urlSafe);
    if(container.querySelector('[data-network="twitter"]'))
      container.querySelector('[data-network="twitter"]').setAttribute('href', "https://twitter.com/intent/tweet?url=" + urlSafe + "&text=" + tweetSummarySafe);
    if(container.querySelector('[data-network="pinterest"]'))
      container.querySelector('[data-network="pinterest"]').setAttribute('href', "http://pinterest.com/pin/create/button/?url=" + urlSafe + "&description=" + summarySafe + "&media=" + img);
    if(container.querySelector('[data-network="googleplus"]'))
      container.querySelector('[data-network="googleplus"]').setAttribute('href', "https://plus.google.com/share?url=" + urlSafe);
    if(container.querySelector('[data-network="tumblr"]'))
      container.querySelector('[data-network="tumblr"]').setAttribute('href', "http://www.tumblr.com/share/link?posttype=link&url=" + urlSafe + "&title=" + summarySafe + "&content=" + summarySafe + "&caption=" + summarySafe);
       // container.querySelector('[data-network="reddit"]').setAttribute('href', "http://reddit.com/submit?url=" + urlSafe + "&title=" + summarySafe);
    if(container.querySelector('[data-network="linkedin"]'))
      container.querySelector('[data-network="linkedin"]').setAttribute('href', "http://www.linkedin.com/shareArticle?mini=true&url=" + urlSafe + "&title=" + summarySafe);
    if(container.querySelector('[data-network="yammer"]'))
      container.querySelector('[data-network="yammer"]').setAttribute('href', "https://www.yammer.com/messages/new?login=true&status=" + summarySafe + ": " + urlSafe);

    // bind for listener removal
    this.clickHandler = this.clickedShareContainer.bind(this);
    container.addEventListener('click', this.clickHandler);
  }

  disposeShareLinks(container) {
    container.removeEventListener('click', this.clickHandler);
    this.clickHandler = null;
  }
}

ShareOut.networkShareDimensions = {
  facebook:   { width: 480, height: 210 },
  twitter:    { width: 550, height: 420 },
  pinterest:  { width: 750, height: 320 },
  googleplus: { width: 500, height: 385 },
  tumblr:     { width: 450, height: 430 },
  reddit:     { width: 540, height: 420 },
  linkedin:   { width: 550, height: 460 },
  yammer:     { width: 600, height: 350 }
};

class SolidSocket {

  constructor(wsAddress) {
    this.wsAddress = wsAddress;
    this.socket = new WebSocket(wsAddress);
    this.addSocketListeners();
    this.lastConnectAttemptTime = Date.now();
    this.startMonitoringConnection();
  }

  // WebSocket LISTENERS

  addSocketListeners() {
    this.openHandler = this.onOpen.bind(this);
    this.socket.addEventListener('open', this.openHandler);
    this.messageHandler = this.onMessage.bind(this);
    this.socket.addEventListener('message', this.messageHandler);
    this.errorHandler = this.onError.bind(this);
    this.socket.addEventListener('error', this.errorHandler);
    this.closeHandler = this.onClose.bind(this);
    this.socket.addEventListener('close', this.closeHandler);
  }

  removeSocketListeners() {
    this.socket.removeEventListener('open', this.openHandler);
    this.socket.removeEventListener('message', this.messageHandler);
    this.socket.removeEventListener('error', this.errorHandler);
    this.socket.removeEventListener('close', this.closeHandler);
    this.socket.close();
  }

  // CALLBACKS

  onOpen(e) {
    if(this.openCallback) this.openCallback(e);
  }

  setOpenCallback(callback) {
    this.openCallback = callback;
  }

  onMessage(e) {
    if(this.messageCallback) this.messageCallback(e);
  }

  setMessageCallback(callback) {
    this.messageCallback = callback;
  }

  onError(e) {
    if(this.errorCallback) this.errorCallback(e);
  }

  setErrorCallback(callback) {
    this.errorCallback = callback;
  }

  onClose(e) {
    if(this.closeCallback) this.closeCallback(e);
  }

  setCloseCallback(callback) {
    this.closeCallback = callback;
  }

  // SEND

  sendMessage(message) {
    this.socket.send(message);
  }

  // MONITORING & RECONNECTION

  startMonitoringConnection() {
    this.checkConnection();
  }

  checkConnection() {
    let socketOpen = this.socket.readyState == WebSocket.OPEN;
    let socketConnecting = this.socket.readyState == WebSocket.CONNECTING;
    let timeForReconnect = Date.now() > this.lastConnectAttemptTime + SolidSocket.RECONNECT_INTERVAL;
    if(timeForReconnect) {
      this.lastConnectAttemptTime = Date.now();

      // check for disconnected socket
      if(!socketOpen && !socketConnecting) {
        // clean up failed socket object
        this.removeSocketListeners();
        // initialize a new socket object
        try{
          this.socket = new WebSocket(this.wsAddress);
          this.addSocketListeners();
        } catch(err) {
          console.log('Websocket couldn\'t connect: ', err);
        }
      }

      // add body class depending on state
      if(socketOpen) {
        document.body.classList.add('has-socket');
        document.body.classList.remove('no-socket');
      } else {
        document.body.classList.add('no-socket');
        document.body.classList.remove('has-socket');
      }
    }
    // keep checking connection
    requestAnimationFrame(() => this.checkConnection());
  }

}

SolidSocket.RECONNECT_INTERVAL = 2000;

// dependencies:
// - ArrayUtil.crossfadeEnds()
// - FloatBuffer

class SoundFFT {

  constructor(context, audioNode) { // Howler.ctx, sound._sounds[0]._node
    this.context = context;
    this.audioNode = audioNode;
    this.debug = false;

    // send sound node to analyser. the main destination output remains intact
    this.analyser = this.context.createAnalyser();
    this.analyser.fftSize = 512;
    this.analyser.smoothingTimeConstant = 0.1;
    this.audioNode.connect(this.analyser);

    // build audio data array
    this.binCount = this.analyser.frequencyBinCount;
    this.spectrumData = new Uint8Array(this.binCount);
    this.spectrum = new Array(this.binCount);
    this.spectrumCrossfaded = new Array(this.binCount);
    for(var i=0; i < this.binCount; i++) this.spectrum[i] = 0; // reset to zero

    this.waveformData = new Uint8Array(this.binCount);
    this.waveform = new Array(this.binCount);
    for(var i=0; i < this.binCount; i++) this.waveform[i] = 0; // reset to zero

    // spectrum decay
    this.freqDecay = 0.98;

    // beat detect
    this.detectedBeat = false;
    this.beatCutOff = 0;
    this.beatLastTime = 0;
    this.beatHoldTime = 300;     // num frames to hold a beat
    this.beatDecayRate = 0.97;
    this.beatMin = 0.15; //level less than this is no beat
    this.avgAmp = 0;
    this.ampDir = new FloatBuffer(5);
  }

  setDebug(isDebug) {
    this.debug = isDebug;
  }

  getSpectrum() {
    return this.spectrum;
  }

  getWaveform() {
    return this.waveform;
  }

  getDetectedBeat() {
    return this.detectedBeat;
  }

  update() {
    if(this.analyser) {
      // get raw data
      this.analyser.getByteFrequencyData(this.spectrumData);
      this.analyser.getByteTimeDomainData(this.waveformData);
      // turn it into usable floats: 0-1 for spectrum and -1 to 1 for waveform data
      this.normalizeSpectrumFloats(false);
      this.normalizeWaveformFloats();
      this.calcAverageLevel();
      this.detectBeats();
      ArrayUtil.crossfadeEnds(this.waveform, 0.05);
      if(this.debug) this.drawDebug();
    }
  }

  normalizeSpectrumFloats(crossfadeEnds) {
    if(crossfadeEnds == false) {
      for(var i = 0; i < this.spectrumData.length; i++) {
        let curFloat = this.spectrumData[i] / 255;
        this.spectrum[i] = Math.max(curFloat, this.spectrum[i] * this.freqDecay); // lerp decay
      }
    } else {
      // create temp crossfaded array without decay
      for(var i = 0; i < this.spectrumData.length; i++) {
        let curFloat = this.spectrumData[i] / 255;
        this.spectrumCrossfaded[i] = curFloat;
      }
      ArrayUtil.crossfadeEnds(this.spectrumCrossfaded, 0.5);
      // use crossfaded array to decay
      for(var i = 0; i < this.spectrumCrossfaded.length; i++) {
        let curFloat = this.spectrumCrossfaded[i];
        this.spectrum[i] = Math.max(curFloat, this.spectrum[i] * this.freqDecay); // lerp decay
      }
    }
  }

  normalizeWaveformFloats() {
    for(var i = 0; i < this.waveformData.length; i++) {
      this.waveform[i] = 2 * (this.waveformData[i] / 255 - 0.5);
    }
  }

  calcAverageLevel() {
    let lastAmp = this.avgAmp;
    let ampSum = 0;
    for(var i = 0; i < this.spectrum.length; i++) {
      ampSum += this.spectrum[i];
    }
    this.avgAmp = ampSum / this.spectrum.length;
    this.ampDir.update(this.avgAmp - lastAmp);
  }

  detectBeats() {
    if(this.avgAmp > this.beatCutOff && this.avgAmp > this.beatMin && this.beatDetectAvailable() && this.ampDir.average() > 0.01) {
      this.detectedBeat = true;
      this.beatCutOff = this.avgAmp * 1.1;
      this.beatLastTime = Date.now();
    } else {
      this.detectedBeat = false;
      if(this.beatDetectAvailable()){
        this.beatCutOff *= this.beatDecayRate;
        this.beatCutOff = Math.max(this.beatCutOff, this.beatMin);
      }
    }
  }

  beatDetectAvailable() {
    return Date.now() > this.beatLastTime + this.beatHoldTime;
  }

  resetSpectrum() {
    // bring spectrum back down to zero after song ends
    if(this.spectrum) {
      for (var i = 0; i < this.spectrum.length; i++) {
        if(this.spectrum[i] > 0) {
          this.spectrum[i] = this.spectrum[i] - 1;
        }
      }
    }
  }

  buildDebugCanvas() {
    // debug params
    this.debugW = 200;
    this.debugH = 140;
    this.fftH = this.debugH * 2/5;
    this.debugWhite = '#fff';
    this.debugGreen = 'rgba(0, 255, 0, 1)';
    this.debugBlack = '#000';
    this.clearColor = 'rgba(0, 0, 0, 0)';

    // build canvas
    this.canvas = document.createElement('canvas');
    this.canvas.width = this.debugW;
    this.canvas.height = this.debugH;
    this.canvas.setAttribute('style', 'position:absolute;bottom:0;right:0;z-index:9999');
    document.body.appendChild(this.canvas);

    // setup
    this.ctx = this.canvas.getContext('2d');
    this.ctx.fillStyle = this.debugBlack;
    this.ctx.strokeStyle = this.debugWhite;
    this.ctx.lineWidth = 2;
  }

  getDebugCanvas() {
    return this.canvas;
  }

  drawDebug() {
    if(this.ctx == null) this.buildDebugCanvas();

    // background
    this.ctx.fillStyle = this.debugBlack;
    this.ctx.strokeStyle = this.debugWhite;
    this.ctx.fillRect(0, 0, this.debugW, this.debugH);
    this.ctx.strokeRect(0, 0, this.debugW, this.fftH);
    this.ctx.strokeRect(0, this.fftH, this.debugW, this.fftH);
    this.ctx.strokeRect(0, this.fftH * 2, this.debugW, this.fftH/2);

    // draw spectrum bars
    var barWidth = this.debugW / this.binCount;
    this.ctx.fillStyle = this.debugWhite;
    this.ctx.lineWidth = barWidth;
    for(var i = 0; i < this.binCount; i++) {
      this.ctx.fillRect(i * barWidth, this.fftH, barWidth, -this.spectrum[i] * this.fftH);
    }

    // draw waveform
    this.ctx.strokeStyle = this.debugWhite;
    this.ctx.beginPath();
    for(var i = 0; i < this.binCount; i++) {
      this.ctx.lineTo(i * barWidth, this.fftH * 1.5 + this.waveform[i] * this.fftH / 2);
    }
    this.ctx.stroke();

    // beat detection
    this.ctx.fillStyle = this.debugWhite;
    if (this.beatDetectAvailable() == false && Date.now() < this.beatLastTime + 200){
      this.ctx.fillStyle = this.debugGreen;
    }
    this.ctx.fillRect(0, this.fftH * 2, this.debugW * this.avgAmp, this.fftH / 2);

    // beat detect cutoff
    this.ctx.fillRect(this.debugW * this.beatCutOff, this.fftH * 2, 2, this.fftH / 2);
  }

}

SoundFFT.FREQUENCIES = 'SoundFFT.FREQUENCIES';
SoundFFT.WAVEFORM = 'SoundFFT.WAVEFORM';
SoundFFT.BEAT = 'SoundFFT.BEAT';

class Sounds {

  constructor() {
    _store.addListener(this);
    this.sounds = {};
    // this.buildAudioToggleButton();
    this.loadSounds();
    this.initMute();
  }

  // buildAudioToggleButton() {
  //   this.toggleButton = document.getElementById('audio-toggle');
  //   this.toggleButton.addEventListener('click', (e) => this.toggleMute(e));
  // }

  loadSounds() {
    for(var key in Sounds.audioFiles) {
      let fileName = Sounds.audioFiles[key];
      this.sounds[fileName] = new Howl({src:[fileName]});
    }
  }

  initMute() {
    this.muted = false;
    if(window.localStorage.getItem("audio-mute") == "true") {
      this.toggleMute();
    }
  }

  toggleMute(e) {
    this.muted = !this.muted;
    Howler.mute(this.muted);

    if(this.muted == false) {
      window.localStorage.setItem("audio-mute", "false");
      document.body.classList.remove('audio-muted');
    } else {
      window.localStorage.setItem("audio-mute", "true");
      document.body.classList.add('audio-muted');
    }
  }

  getSound(soundId) {
    return this.sounds[soundId];
  }

  playSound(soundId) {
    this.sounds[soundId].stop().play();
  }

  storeUpdated(key, value) {
    // request specific sound
    if(key == Sounds.PLAY_SOUND) this.playSound(value);
  }

  playSoundtrack() {
    if(this.menuSoundtrackAdded) return;
    this.menuSoundtrackAdded = true;

    if(MobileUtil.isMobileBrowser()) {
      document.body.addEventListener('touchstart', this.startIntroLoop());
    } else {
      this.startIntroLoop();
    }
  }

  startIntroLoop() {
    if(this.introLoopStarted) return;
    this.introLoopStarted = true;

    let fileName = Sounds.audioFiles.MENU_LOOP;
    this.sounds[fileName] = new Howl({src:[fileName], volume:0, loop:true});
    this.playSound(fileName);
    this.getSound(fileName).fade(0, 0.75, 750);
  }

}

Sounds.PLAY_SOUND = 'PLAY_SOUND';
Sounds.audioFiles = {
  MENU_LOOP:        './audio/loop.mp3',
}

class StringFormatter {

  /**
   *  Returns a standardized phone number string.
   *  @param  str An unformatted phone number.
   *  @return A standardized phone number string.
   *  @use    {@code var phone = StringFormatter.formatPhone('3035558888');}
   */
  static formatPhone(str) {
    return (str + '').replace(/[() -]*(?:\d?)[() -.]*(\d{3})[() -.]*(\d{3})[() -.]*(\d{4})[() -]*/, '($1) $2-$3');
  }

  /**
   *  Returns a standardized social security number string.
   *  @param  str An unformatted social security number.
   *  @return A standardized social security number string.
   *  @use    {@code var ssn = StringFormatter.formatSSN('333002222');}
   */
  static formatSSN(str) {
    return (str + '').replace(/(\d{3})[ -]*(\d{2})[ -]*(\d{4})/, '$1-$2-$3');
  }

  /**
   *  Returns a standardized credit card number string.
   *  @param  str An unformatted credit card number.
   *  @return A standardized credit card number string.
   *  @use    {@code var cc = StringFormatter.formatCreditCard('1111-2222-3333-4444');}
   */
  static formatCreditCard(str) {
    return (str + '').replace(/(\d{4})[ -]*(\d{4})[ -]*(\d{4})[ -]*(\d{4})/, '$1 $2 $3 $4');
  }

  /**
   *  Returns a number, removing non-numeric characters.
   *  @param  str A number, without too much extra non-numeric junk in there.
   *  @return A number (in string format), stripped of non-numeric characters.
   *  @use    {@code var number = StringFormatter.formatNumber('$303.33');}
   */
  static formatNumber(str) {
    let float = str.match(/\d+\.?\d+/);
    if (float && float.length > 0) {
      return float[0];
    } else {
      return str;
    }
  }

  /**
   *  Returns a number with the traditional US currency format.
   *  @param  str A numberic monetary value.
   *  @return A number (in string format), with traditional US currency formatting.
   *  @use    {@code var moneyVal = StringFormatter.formatDollarsCents('303.333333');}
   */
  static formatDollarsCents(str) {
    var numParts;
    numParts = (str + '').split('.');
    if (numParts.length === 1) {
      numParts.push('00');
    } else {
      while (numParts[1].length < 2) {
        numParts[1] += '0';
      }
      numParts[1] = numParts[1].substr(0, 2);
    }
    return '$' + numParts.join('.');
  }

  /**
   *  Returns a string, formatted with commas in between every 3 numbers.
   *  @param  str A number.
   *  @return A formatted number (in string format).
   *  @use    {@code var formattedNumber = StringFormatter.addCommasToNumber('3000000');}
   */
  static addCommasToNumber(str) {
    let x = (str + '').split('.');
    let x1 = x[0];
    let x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
      x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
  }

  /**
   *  Returns a time as a string, with or without hours.
   *  @param  seconds   A number of seconds.
   *  @param  showHours Boolean flag for showing hours or not.
   *  @return A formatted time.
   *  @use    {@code var time = StringFormatter.timeFromSeconds(30000, true);}
   */
  static timeFromSeconds(seconds, showHours=false, showMs=false) {
    var h = Math.floor(seconds / 60 / 60);
    var m = Math.floor(seconds % 3600 / 60);
    var ms = Math.floor((seconds % 1) * 100);// * 0.001;
    var s = Math.floor(seconds % 60);
    var hStr = (h < 10 ? "0" : "") + h;
    var mStr = (m < 10 ? "0" : "") + m;
    var sStr = (s < 10 ? "0" : "") + s;
    var msStr = (ms < 10 ? "0" : "") + ms;
    return (
      ((showHours) ? hStr + ':' : '') +
      mStr + ':' +
      sStr +
      ((showMs) ? ':' + msStr : '')
    );
  }

  static replaceLineBreaksWithString(inputStr, lineBreakReplacement) {
    return inputStr.replace(/(\r\n|\n|\r)/gm, lineBreakReplacement);
  }

  static removeCharacterAtIndex(str, index) {
    part1 = str.substring(0, index);
    part2 = str.substring(index + 1, str.length);
    return (part1 + part2);
  }

}

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

class ThreeScene {

  constructor(el, bgColor = 0xffffff) {
    this.el = el;
    this.bgColor = bgColor;
    this.buildScene();
    this.buildCamera();
    this.buildRenderer();
    this.buildLights();
    this.addToDOM();
  }

  buildScene() {
    this.scene = new THREE.Scene();
  }

  buildCamera() {
    this.getScreenSize();
    this.VIEW_ANGLE = 45;
    this.NEAR = 0.1;
    this.FAR = 20000;
    this.camera = new THREE.PerspectiveCamera(this.VIEW_ANGLE, this.ASPECT, this.NEAR, this.FAR);
    this.scene.add(this.camera);
    this.camera.position.set(0,0,400);
    this.camera.lookAt(this.scene.position);
  }

  buildRenderer() {
    this.renderer = new THREE.WebGLRenderer( {antialias:true} );
    this.renderer.setClearColor(this.bgColor);
    this.renderer.setSize(this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
    this.renderer.shadowMap.enabled = true;
    this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
  }

  buildLights() {
    var ambientLight = new THREE.AmbientLight(0x444444);
    this.scene.add(ambientLight);
  }

  addToDOM() {
    this.container = document.createElement('div');
    this.el.appendChild(this.container);
    this.container.appendChild(this.renderer.domElement);
  }

  update() {
    this.renderer.render(this.scene, this.camera);
  }

  getScreenSize() {
    this.SCREEN_WIDTH = window.innerWidth;
    this.SCREEN_HEIGHT = window.innerHeight;
    this.ASPECT = this.SCREEN_WIDTH / this.SCREEN_HEIGHT;
  }

  resize() {
    this.getScreenSize();
    this.container.style.width = this.SCREEN_WIDTH + 'px';
    this.container.style.height = this.SCREEN_HEIGHT + 'px';
    this.camera.aspect = this.ASPECT;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
  }

}

class URLUtil {

  static getHashQueryVariable(variable) {
    var query = decodeURIComponent(window.location.hash.substring(1)); // decode in case of it being encoded
    var vars = query.split('&');
    for (var i = 0; i < vars.length; i++) {
      var pair = vars[i].split('=');
      if (decodeURIComponent(pair[0]) == variable) {
        return decodeURIComponent(pair[1]);
      }
    }
    return null;
  }

}

class UserInteractionTimeout {

  constructor(el=document.body, millis=1000, completeCallback, interactedCallback) {
    // setup
    this.el = el;
    this.millis = millis;
    this.completeCallback = completeCallback;
    this.interactedCallback = interactedCallback;

    // add listeners all to a single callback
    this.events = ['mousedown', 'touchstart', 'mousemove', 'touchmove', 'mouseup', 'touchend', 'scroll'];
    this.events.forEach((eventName) => {
      this.el.addEventListener(eventName, (e) => this.interacted());
    });
  }

  interacted(e) {
    if(this.interactedCallback) this.interactedCallback();
    window.clearTimeout(this.timeout);
    this.timeout = window.setTimeout(() => {
      if(this.completeCallback) this.completeCallback();
    }, this.millis);
  }

}

class VideoToBlob {

  constructor(videoURL, callback) {
    this.callback = callback;
    this.loadVideo(videoURL);
  }

  loadFile(url, fileLoadCallback) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'arraybuffer';
    xhr.send();
    xhr.onload = function() {
      if (xhr.status !== 200) {
        console.warn('VideoToBlob.loadFile() :: Unexpected status code ' + xhr.status + ' for ' + url);
      }
      fileLoadCallback(new Uint8Array(xhr.response));
    };
  }

  loadVideo(videoURL) {
    let videoType = (videoURL.match(/mp4/gi)) ? 'video/mp4' : 'video/webm';
    this.loadFile(videoURL, (uInt8Array) => {
      var blob = new Blob([uInt8Array], {
        type: videoType
      });

      let videoEl = document.createElement('video');
      videoEl.defaultMuted = true;
      videoEl.setAttribute('muted', "true");
      videoEl.setAttribute('preload', "auto");
      videoEl.setAttribute('playsinline', "true");
      videoEl.setAttribute('crossOrigin', "anonymous");
      videoEl.setAttribute('loop', "true");
      // videoEl.setAttribute('autoplay', "true");

      videoEl.src = URL.createObjectURL(blob);
      videoEl.setAttribute('muted', "true");
      videoEl.muted = true;
      videoEl.volume = 0;

      this.callback(videoEl);
    });
  }

  // buildVideoTexture() {
  //   var texture = PIXI.Texture.from(this.videoEl);
  //   texture.baseTexture.resource.updateFPS = 30;
  //   this.sprite = new PIXI.Sprite(texture);
  //   this.container.addChild(this.sprite);
  //   this.sprite.mask = this.maskGraphics;
  // }

}
