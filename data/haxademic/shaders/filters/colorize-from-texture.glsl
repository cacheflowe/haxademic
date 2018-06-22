// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D colorMap;
uniform int lumaMult = 0;
uniform float crossfade = 1.;

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  float luma = rgbToGray(color);
  vec4 colorizedColor = texture2D(colorMap, vec2(luma, 0.5));
  if(lumaMult == 0) {
    gl_FragColor = mix(color, colorizedColor, crossfade);
  } else {
    gl_FragColor = mix(color, vec4(colorizedColor.r * luma, colorizedColor.g * luma, colorizedColor.b * luma, colorizedColor.a), crossfade);
  }
}
