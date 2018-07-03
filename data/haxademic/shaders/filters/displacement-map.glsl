#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D map;
uniform int mode = 0;
uniform float amp = 0.1;

float TWO_PI = radians(360);

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
	vec2 p = vertTexCoord.xy;
	vec4 colorOrig = texture2D(texture, p);
	vec4 colorDisplaced = vec4(0.);
	if(mode == 0) {
		// https://www.shadertoy.com/view/lss3D4
		colorDisplaced = texture2D(texture, p+(texture2D(map, p).rb) * amp);
	} else if (mode == 1) {
		// https://www.shadertoy.com/view/MdyXRy
		colorDisplaced = texture2D(texture, p+(texture2D(map, p).rb-vec2(0.0471, 0.1451)) * amp);
	} else if(mode == 2) {
		// https://www.shadertoy.com/view/XdfGzl
		vec3 obump = texture2D(map, p).rgb;
		float displace = dot(obump, vec3(0.3, 0.6, 0.1));
		displace = (displace - 0.5) * amp;
		colorDisplaced = texture2D(texture, p + vec2(displace));
	// } else if(mode == 3) {
	// 	// cacheflowe original, based on feedback-map.glsl
	// 	float radialAmp = amp * 0.2;
	// 	float rotate = rgbToGray(texture2D(map, p)) * TWO_PI;
	// 	vec2 displace1 = p + vec2(radialAmp * cos(rotate), radialAmp * sin(rotate));
	// 	vec2 displace2 = p + vec2(radialAmp * 0.66 * cos(rotate), radialAmp * 0.66 * sin(rotate));
	// 	vec2 displace3 = p + vec2(radialAmp * 0.33 * cos(rotate), radialAmp * 0.33 * sin(rotate));
	//
	// 	colorDisplaced = texture2D(texture, displace1);
	// 	colorDisplaced += texture2D(texture, displace2);
	// 	colorDisplaced += texture2D(texture, displace3);
	// 	colorDisplaced /= 3.;
	} else if(mode == 3) {
		// cacheflowe original, based on feedback-map.glsl
		float radialAmp = amp * 0.2;
		float rotate = rgbToGray(texture2D(map, p)) * TWO_PI;
		vec2 displace = p + vec2(radialAmp * cos(rotate), radialAmp * sin(rotate));
		colorDisplaced = texture2D(texture, displace);
	}
	gl_FragColor = mix(colorOrig, colorDisplaced, colorDisplaced.a);
}
