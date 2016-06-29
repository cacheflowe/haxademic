// from: http://transitions.glsl.io/transition/5a4d1fb6711076d17e2e

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D from;
uniform sampler2D to;
uniform float progress;

const float strength=0.03;

void main() {
  vec2 p = vertTexCoord.xy;
  // p = vec2(p.x, 1.0-p.y);	// flipV while processing has issues with PImage
  vec4 ca = texture2D(from, p);
  vec4 cb = texture2D(to, p);
  
  vec2 oa = (((ca.rg+ca.b)*0.5)*2.0-1.0);
  vec2 ob = (((cb.rg+cb.b)*0.5)*2.0-1.0);
  vec2 oc = mix(oa,ob,0.5)*strength;
  
  float w0 = progress;
  float w1 = 1.0-w0;
  gl_FragColor = mix(texture2D(from, p+oc*w0), texture2D(to, p-oc*w1), progress);
}