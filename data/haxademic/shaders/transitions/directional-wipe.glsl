// from: http://transitions.glsl.io/transition/35e8c18557995c77278e

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

uniform vec2 direction = vec2(1.0, -1.0);
uniform float smoothness = 0.5;
 
const vec2 center = vec2(0.5, 0.5);
 
void main() {
  vec2 p = vertTexCoord.xy;
  vec2 v = normalize(direction);
  v /= abs(v.x)+abs(v.y);
  float d = v.x * center.x + v.y * center.y;
  float m = smoothstep(-smoothness, 0.0, v.x * p.x + v.y * p.y - (d-0.5+progress*(1.+smoothness)));
  gl_FragColor = mix(texture2D(to, p), texture2D(from, p), m);
}