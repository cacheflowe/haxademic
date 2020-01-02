#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D uDivergence;

void main () {
  vec2 uv = vertTexCoord.xy;
  vec2 vL = uv - vec2(texOffset.x, 0.);
  vec2 vR = uv + vec2(texOffset.x, 0.);
  vec2 vT = uv - vec2(0., texOffset.y);
  vec2 vB = uv + vec2(0., texOffset.y);

  float L = texture2D(texture, vL).x;
  float R = texture2D(texture, vR).x;
  float T = texture2D(texture, vT).x;
  float B = texture2D(texture, vB).x;
  float C = texture2D(texture, uv).x;
  float divergence = texture2D(uDivergence, uv).x;
  float pressure = (L + R + B + T - divergence) * 0.25;
  gl_FragColor = vec4(pressure, 0.0, 0.0, 1.0);
}