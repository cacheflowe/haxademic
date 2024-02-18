#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D mapPositions;
uniform sampler2D mapRandom;
uniform sampler2D mapTexture;
uniform float speed = 1.;
uniform float width = 256.;
uniform float height = 256.;
uniform float decelCurve = 3.;
uniform bool resetPositionsDirty = false;

#define PI 3.141592653589793
#define TAU 6.283185307179586

float easeOutQuart(float x) {
	return 1. - pow(1. - x, 4.);
}

float remap(float value, float low1, float high1, float low2, float high2) {
	return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
	// get cur color/position from textures
	// prevColor
	// - rg = xy
	vec2 uv = vertTexCoord.xy;
	vec4 prevColor = texture2D(texture, uv);
	vec4 randomColor = texture2D(mapRandom, uv);
	vec4 positionColor = texture2D(mapPositions, uv);

	// get position from texture
  vec2 pos = positionColor.rg;

	// get particle position and translate into texture UV
	vec2 posToUV = vec2((pos.x + 0.5) / width, (pos.y + 0.5) / height);
	vec4 mapColor = texture2D(mapTexture, posToUV);

	// lerp rom prev color to cur mapColor
	vec4 curColor = mix(prevColor, mapColor, 0.09);

	// write position back to texture
  gl_FragColor = curColor;
}
