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

uniform float size = 0.04;
uniform float zoom = 30.;
uniform float colorSeparation = 0.3;
 
void main() {
  vec2 p = vertTexCoord.xy;
  // p = vec2(p.x, 1.0-p.y);	// flipV while processing has issues with PImage
  float inv = 1. - progress;
  vec2 disp = size*vec2(cos(zoom*p.x), sin(zoom*p.y));
  vec4 texTo = texture2D(to, p + inv*disp);
  vec4 texFrom = vec4(
    texture2D(from, p + progress*disp*(1.0 - colorSeparation)).r,
    texture2D(from, p + progress*disp).g,
    texture2D(from, p + progress*disp*(1.0 + colorSeparation)).b,
    1.0);
  gl_FragColor = texTo*progress + texFrom*inv;
}