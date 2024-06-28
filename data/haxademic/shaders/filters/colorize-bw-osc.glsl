// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform float offset = 0.;
uniform float freq = 6.;

#define TWO_PI 6.28318530718

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  float luma = rgbToGray(color);
  float colorized = 0.5 + 0.5 * sin(offset + (luma * freq * TWO_PI));
  colorized = smoothstep(0.1, 0.9, colorized);
  gl_FragColor = vec4(colorized, colorized, colorized, color.a);
}
