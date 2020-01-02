#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D uPressure;

void main () {
  vec2 uv = vertTexCoord.xy;
  vec2 vL = uv - vec2(texOffset.x, 0.);
  vec2 vR = uv + vec2(texOffset.x, 0.);
  vec2 vT = uv - vec2(0., texOffset.y);
  vec2 vB = uv + vec2(0., texOffset.y);

  float L = texture2D(uPressure, vL).x;
  float R = texture2D(uPressure, vR).x;
  float T = texture2D(uPressure, vT).x;
  float B = texture2D(uPressure, vB).x;
  vec2 velocity = texture2D(texture, uv).xy;
  velocity.xy -= vec2(R - L, T - B);
  gl_FragColor = vec4(velocity, 0.0, 1.0);
}
