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
	// particleColor
	// - rg = xy
	// - b = speed
	// randomColor
	// - b = random rot
	vec2 uv = vertTexCoord.xy;
	vec4 particleColor = texture2D(texture, uv);
	vec4 randomColor = texture2D(mapRandom, uv);

	// get position and rotation from textures
  vec2 pos = particleColor.rg;
	float prevSpeed = particleColor.b;
	float rot = randomColor.b * TAU;
	// rot += sin(randomColor.g * 0.1) * 0.1;

	// get particle position and translate into texture UV
	vec2 posToUV = vec2((pos.x + 0.5) / width, (pos.y + 0.5) / height);
	vec4 mapColor = texture2D(mapTexture, posToUV);
	vec4 noiseColor = texture2D(mapNoise, posToUV);

	// move particle and wrap around
	// rotation --
	float rotMix = 0.1 + 0.45 * randomColor.g; // noise cohesion
	float rotNoise = noiseColor.r * TAU * 2.;
	float rotMap = mapColor.r * TAU * 2.;
	rot = mix(rot, rotNoise, rotMix); // mix between random direction and move toward following noise texture
	// rot = mix(rot, rotMap, rotMix/2.); // mix between random/noise direction and move toward following map texture
	// speed --
	float curDecelCurve = mapColor.r;
	if(decelCurve > 0.) curDecelCurve = easeOutQuart(mapColor.r * decelCurve);
	float mapDecel = 1. - clamp(curDecelCurve, 0., 0.9);
	float noiseSpeed = (0.7 + 1.3 * noiseColor.g); // speed variation based on noise map. do we need this?
	float randomSpeed = (0.7 + 1.3 * randomColor.r); // faster dissipation when leaving bright areas
	// calculate current expected speed, but lerp toward it from previous speed
	float customSpeed = speed * randomSpeed * noiseSpeed * mapDecel;
	float curSpeed = mix(prevSpeed, customSpeed, 0.2);

	// set position
	pos += vec2(cos(rot), sin(rot)) * curSpeed;

	// recycle to random location when out of view
	// or when we've flipped the reset flag
	if(pos.x < 0. || pos.x > width || pos.y < 0. || pos.y > height || resetPositionsDirty) {
		pos = vec2(width * randomColor.x, height * randomColor.y); // random
	}

	// write position back to texture
  gl_FragColor = vec4(pos.x, pos.y, curSpeed, 1.);
}
