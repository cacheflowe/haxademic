#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main () {
  vec2 uv = vertTexCoord.xy;
  vec4 sum = texture2D(texture, uv) * 0.29411764;
  sum += texture2D(texture, uv - vec2(texOffset.x, 0.)) * 0.35294117;  // pixel to the left
  sum += texture2D(texture, uv + vec2(texOffset.x, 0.)) * 0.35294117;  // pixel to the right
  gl_FragColor = sum;
}