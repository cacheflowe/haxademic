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
uniform float gradientEdge = 1.;
uniform float progress = 0.;

float map(float value, float low1, float high1, float low2, float high2) {
   return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  vec4 overlayColor = mix(colorBot, colorTop, vertTexCoord.y);

  // clear -> color bot -> color top -> clear
  float curProgress = -1. * vertTexCoord.y + progress * 4.;
  if(curProgress < 1.) {
    gl_FragColor = mix(color, colorBot, clamp(map(curProgress, 1. - gradientEdge, 1., 0., 1.), 0., 1.));
  } else if(curProgress < 2.) {
    gl_FragColor = mix(colorBot, colorTop, clamp(curProgress - 1., 0., 1.));
  } else if(curProgress < 3.) {
    gl_FragColor = mix(colorTop, color, clamp(map(curProgress - 2., 0., gradientEdge, 0., 1.), 0., 1.));
  } else {
    gl_FragColor = color;
  }
}
