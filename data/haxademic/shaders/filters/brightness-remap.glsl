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

float remap(float value, float low1, float high1, float low2, float high2) {
   return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  color.r = remap(color.r, 0., 1., low, high);
  color.g = remap(color.g, 0., 1., low, high);
  color.b = remap(color.b, 0., 1., low, high);
  gl_FragColor = color;
}
