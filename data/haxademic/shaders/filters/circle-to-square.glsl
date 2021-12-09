// special split-pole map re-projection by @cacheflowe
// help from: https://www.xarg.org/2017/07/how-to-map-a-square-to-a-circle/
// and: http://squircular.blogspot.com/2015/09/mapping-circle-to-square.html

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float uDistortAmp = 1.0;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

float remap(float value, float low1, float high1, float low2, float high2) {
	return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

vec2 squareToCircleMap(vec2 uv, float amp) {
	uv += -0.5;
	return vec2(
		uv.x * sqrt(1. - uv.y * uv.y / amp), 
		uv.y * sqrt(1. - uv.x * uv.x / amp)
	) + 0.5;
}

void main() {
	vec2 uv = vertTexCoord.xy;
	float distortAmp = remap(uDistortAmp, 0.9, 1., 100., 0.5);
	gl_FragColor = texture2D(texture, squareToCircleMap(uv, distortAmp));

	// debug original texture
	// gl_FragColor = texture2D(texture, vertTexCoord.xy);
	// gl_FragColor = vec4(uv.x, uv.y, 0., 1.);
	// gl_FragColor = vec4(offset.x, offset.x, offset.x, 1.);
}


/*
	// single disk
	vec2 uv = vertTexCoord.xy - 0.5;
	vec2 uvCenter = vec2(0.);
	gl_FragColor = texture2D(texture, squareToCircleMap(uv) + 0.5);
*/

/*
	// unused logic
	float radius = 0.5;
	float radsToCenter = atan(uv.x - uvCenter.x, uv.y - uvCenter.y) + PI;
	float distanceToCenter = distance(uv, uvCenter);

	// debug rads
	// gl_FragColor = vec4(vec3(radsToCenter / TWO_PI), 1.);
	// debug distance
	// gl_FragColor = vec4(vec3(distanceToCenter), 1.);
*/