// from: http://transitions.glsl.io/transition/9b7cce648a1cd777c6a3206bce7cd814

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

void main() {
  float x = progress;
  vec2 p = vertTexCoord.xy;
  // p = vec2(p.x, 1.0-p.y);	// flipV while processing has issues with PImage
  x=smoothstep(.0,1.0,(x*2.0+p.x-1.0));
  gl_FragColor = mix(texture2D(from, (p-.5)*(1.-x)+.5), texture2D(to, (p-.5)*x+.5), progress);
}