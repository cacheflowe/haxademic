// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec3 color1 = vec3(1.0);
uniform vec3 color2 = vec3(0.0);
uniform int crossfadeMode = 0;

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  float luma = rgbToGray(color);
	if(crossfadeMode == 0) {
		// crossfade between 2 colors directly
		gl_FragColor = vec4(mix(color2, color1, luma), 1.0);
	} else if(crossfadeMode == 1) {
		// crossfade between 2 colors with smoothstep for a sharper (high contrast) crossfade
		float mixLevel = smoothstep(0.3, 0.7, luma);
		gl_FragColor = vec4(mix(color2, color1, mixLevel), 1.0);
	} else {
		// both colors fade to black at 0.5, and become full color at 0 & 1
		if(luma < 0.5) {
		  gl_FragColor = vec4(color2 * (1. - luma * 2.), 1.);
		} else {
		  gl_FragColor = vec4(color1 * (-0.5 + luma * 2.), 1.0);
		}
	}
}
