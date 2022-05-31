#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D uVelocity;
uniform sampler2D uCurl;
uniform float curlAmp = 0.05;

void main () {
  vec2 uv = vertTexCoord.xy;
  vec2 vL = uv - vec2(texOffset.x, 0.);
  vec2 vR = uv + vec2(texOffset.x, 0.);
  vec2 vT = uv - vec2(0., texOffset.y);
  vec2 vB = uv + vec2(0., texOffset.y);
  float L = texture2D(uCurl, vL).x;  // left
  float R = texture2D(uCurl, vR).x;  // right
  float T = texture2D(uCurl, vT).x;  // up
  float B = texture2D(uCurl, vB).x;  // down
  float C = texture2D(uCurl, uv).x;
  vec2 force = 0.5 * vec2(abs(T) - abs(B), abs(R) - abs(L));
  force /= length(force) + 0.0001;
  force *= curlAmp * C;
  force.y *= -1.0;
  vec2 vel = texture2D(uVelocity, uv).xy;
  gl_FragColor = vec4(vel + force, 0.0, 1.0);
}