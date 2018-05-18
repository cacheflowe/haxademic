// converted from: https://www.shadertoy.com/view/4slSR8
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
float particleSpread = 1.3;
float timeFactor = 0.5;

vec2 cart_polar(vec2);
bool run(vec2, float);
vec2 polar_cart(vec2);
vec2 move(vec2, float);
vec2 cart_polar(vec2 p) {
	return vec2(atan(((p).y) / ((p).x)), length(p));
}

void main() {
  vec2 position = vertTexCoord.xy - vec2(.5,.5);
	position.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	bool hit = run( position / particleSpread, time * timeFactor);
	if(hit) {
		gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
	} else {
		gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
	}
}

bool run(vec2 p, float t) {
	t = (t) - 100.0;
	bool hit = false;
	vec2 start = cart_polar(vec2(0.0, 0.5));
	for(int temp_1 = 0; temp_1 < 50; ++temp_1) {
		t = (t) + 1.0;
		vec2 cp = move(start, t);
		float dist = length((polar_cart(cp)) - (p));
		if((dist) < 0.005) {
			hit = true;
		}
	}
	return hit;
}

vec2 polar_cart(vec2 p) {
	return (vec2(cos((p).x), sin((p).x))) * ((p).y);
}

vec2 move(vec2 pos, float t) {
	return vec2((((pos).x) + (t)) * (sin((t) / 1000.0)) + ((pos).x), ((pos).y) * (cos(t)));
}
