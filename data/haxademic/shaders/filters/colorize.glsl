// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float targetR = 1.0;
uniform float targetG = 1.0;
uniform float targetB = 1.0;
uniform float posterSteps = 0.0;

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  float luma = rgbToGray(color);
  if(posterSteps > 0) luma = floor(luma * posterSteps) / posterSteps;
  gl_FragColor = vec4(vec3(targetR, targetG, targetB) * luma, 1.0);
}
