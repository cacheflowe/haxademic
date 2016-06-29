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

uniform float smoothness = 0.3;
uniform bool opening = true;

const vec2 center = vec2(0.5, 0.5);
const float SQRT_2 = 1.414213562373;
 
void main() {
  vec2 p = vertTexCoord.xy;
  float x = opening ? progress : 1.-progress;
  float m = smoothstep(-smoothness, 0.0, SQRT_2*distance(center, p) - x*(1.+smoothness));
  gl_FragColor = mix(texture2D(from, p), texture2D(to, p), opening ? 1.-m : m);
} 