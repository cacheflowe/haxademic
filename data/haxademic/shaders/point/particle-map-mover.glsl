#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D mapRandom;
uniform sampler2D mapTexture;
uniform sampler2D mapNoise;
uniform float speed = 1.;
uniform float width = 256.;
uniform float height = 256.;

#define PI 3.141592653589793
#define TAU 6.283185307179586

float easeOutQuart(float x) {
	return 1. - pow(1. - x, 4.);
}

float remap(float value, float low1, float high1, float low2, float high2) {
	return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
	// get cur color/position
	vec2 uv = vertTexCoord.xy;
	vec4 texelColor = texture2D(texture, uv);
	vec4 randomColor = texture2D(mapRandom, uv);

	// get position and rotation from textures
  vec2 pos = texelColor.rg;
	float rot = randomColor.b * TAU;
	// rot += sin(randomColor.g * 0.1) * 0.1;

	// get particle position and translate into texture UV
	vec2 posToUV = vec2((pos.x + 0.5) / width, (pos.y + 0.5) / height);
	vec4 mapColor = texture2D(mapTexture, posToUV);
	vec4 noiseColor = texture2D(mapNoise, posToUV);

	// move particle and wrap around
	rot = mix(rot, noiseColor.r * TAU * 2f, 0.1 + 0.45 * randomColor.g); // mix between random direction and move toward following noise texture
	float mapAccel = 1. - clamp(easeOutQuart(mapColor.r * 3.), 0., 0.9);
	float noiseSpeed = (0.5 + 1.5 * noiseColor.g);
	float randomSpeed = (0.9 + 0.2 * randomColor.r);
	float customSpeed = speed * randomSpeed * noiseSpeed * mapAccel;
	pos += vec2(cos(rot), sin(rot)) * customSpeed;

	// recycle when out of view
	if(pos.x < 0. || pos.x > width || pos.y < 0. || pos.y > height) {
		pos = vec2(width * randomColor.x, height * randomColor.y); // random
	}

	// write position back to texture
  gl_FragColor = vec4(pos.x, pos.y, customSpeed, 1.);
}
