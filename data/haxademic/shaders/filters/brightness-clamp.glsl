// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float low = 0.;
uniform float high = 1.;

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
//   color.r = min(color.r, high);
//   color.r = max(color.r, low);
//   color.g = min(color.g, high);
//   color.g = max(color.g, low);
//   color.b = min(color.b, high);
//   color.b = max(color.b, low);
  color.r = max(min(color.r, high), low);
  color.g = max(min(color.g, high), low);
  color.b = max(min(color.b, high), low);
  gl_FragColor = color;
}
