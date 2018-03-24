// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec4 colorBot = vec4(0.0, 1.0, 1.0, 1.0);
uniform vec4 colorTop = vec4(1.0, 1.0, 0.0, 1.0);
uniform float progress = 0;

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  vec4 overlayColor = mix(colorBot, colorTop, vertTexCoord.y);

  // clear -> color bot -> color top -> clear
  float curProgress = -1. * vertTexCoord.y + progress * 4.;
  if(curProgress < 1.) {
    gl_FragColor = mix(color, colorBot, clamp(curProgress, 0., 1.));
  } else if(curProgress < 2.) {
    gl_FragColor = mix(colorBot, colorTop, clamp(curProgress - 1., 0., 1.));
  } else if(curProgress < 3.) {
    gl_FragColor = mix(colorTop, color, clamp(curProgress - 2., 0., 1.));
  } else {
    gl_FragColor = color;
  }
}
