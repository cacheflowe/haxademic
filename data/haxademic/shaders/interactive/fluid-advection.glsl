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
uniform float dissipation = 0.005;

void main () {
  vec2 uv = vertTexCoord.xy;
  vec2 coord = uv - (texture2D(uVelocity, uv).rg) * texOffset;
  vec4 result = texture2D(texture, coord);
  float decay = 1.0 + dissipation;
  gl_FragColor = result / decay;
}