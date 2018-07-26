// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float colorSteps = 0.0;

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  float luma = rgbToGray(color);
  luma += 0.075; // fudge the number to stay within the same color palette as needed by camo-filter.glsl
  float adjustedColorSteps = colorSteps + 1.; // fuge this number too
  if(adjustedColorSteps > 0) luma = floor(luma * adjustedColorSteps) / adjustedColorSteps;
  float lumaIndex = floor(luma * adjustedColorSteps);
  float lumaFloor = lumaIndex / adjustedColorSteps;
  gl_FragColor = vec4(vec3(lumaFloor), 1.0);
}
