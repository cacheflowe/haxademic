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
uniform float advectionAmp = 5.;

void main () {
  vec2 uv = vertTexCoord.xy;
  vec2 advecDisplace = (texture2D(uVelocity, uv).rg - 0.5) * texOffset * advectionAmp;
  // advecDisplace -= 0.5;
  vec2 coord = uv - advecDisplace;
  vec4 result = texture2D(texture, coord);
  float decay = 1.0 + dissipation;
  gl_FragColor = result / decay;
}