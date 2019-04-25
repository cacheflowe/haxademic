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
