#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float cutoff = 0.5;
uniform float crossfade = 1.0;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  // better version from gene kogan: https://github.com/genekogan/Processing-Shader-Examples/blob/master/TextureShaders/data/threshold.glsl
  vec3 col = texture2D(texture, vertTexCoord.st).rgb;
  float bright = 0.33333 * (col.r + col.g + col.b);
  float b = mix(0.0, 1.0, step(cutoff, bright));
  gl_FragColor = vec4(mix(col, vec3(b), crossfade), 1.0);
}
