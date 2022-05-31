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

void main () {
  vec2 uv = vertTexCoord.xy;
  vec2 vL = uv - vec2(texOffset.x, 0.);
  vec2 vR = uv + vec2(texOffset.x, 0.);
  vec2 vT = uv - vec2(0., texOffset.y);
  vec2 vB = uv + vec2(0., texOffset.y);

  float L = texture2D(uVelocity, vL).y;  // left
  float R = texture2D(uVelocity, vR).y;  // right
  float T = texture2D(uVelocity, vT).x;  // up
  float B = texture2D(uVelocity, vB).x;  // down
  float vorticity = R - L - T + B;
  gl_FragColor = vec4(vorticity * 5., 0.0, 0.0, 1.0);
}