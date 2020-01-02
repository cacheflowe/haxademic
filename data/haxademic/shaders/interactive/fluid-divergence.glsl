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

  float L = texture2D(uVelocity, vL).x;  // left
  float R = texture2D(uVelocity, vR).x;  // right
  float T = texture2D(uVelocity, vT).y;  // up
  float B = texture2D(uVelocity, vB).y;  // down
  vec2 C = texture2D(uVelocity, uv).xy;
  if (vL.x < 0.0) { L = -C.x; }
  if (vR.x > 1.0) { R = -C.x; }
  if (vT.y > 1.0) { T = -C.y; }
  if (vB.y < 0.0) { B = -C.y; }
  float div = 0.5 * (R - L + T - B);
  gl_FragColor = vec4(div, 0.0, 0.0, 1.0);
}
