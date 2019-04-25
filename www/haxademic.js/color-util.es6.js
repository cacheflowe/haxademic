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
