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

float TWO_PI = radians(360);

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
	vec2 p = vertTexCoord.xy;
	if(mode == 0) {
		// https://www.shadertoy.com/view/lss3D4
		gl_FragColor = texture2D(texture, p+(texture2D(map, p).rb)*.1);
	} else if (mode == 1) {
		// https://www.shadertoy.com/view/MdyXRy
		gl_FragColor = texture2D(texture, p+(texture2D(map, p).rb-vec2(0.0471, 0.1451))*.1);
	} else if(mode == 2) {
		// https://www.shadertoy.com/view/XdfGzl
		vec3 obump = texture2D(map, p).rgb;
		float displace = dot(obump, vec3(0.3, 0.6, 0.1));
		displace = (displace - 0.5)*0.1;
		gl_FragColor = texture2D(texture, p + vec2(displace));
	} else if(mode == 3) {
		// cacheflowe origina, based on feedback-map.glsl
		float amp = 0.05;
		float rotate = rgbToGray(texture2D(map, p)) * TWO_PI;// * 3.;
		vec2 displace = p + vec2(amp * cos(rotate), amp * sin(rotate));
		gl_FragColor = texture2D(texture, displace);
	}
}
