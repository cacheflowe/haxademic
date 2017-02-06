// from https://www.shadertoy.com/view/XsXXDH
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
uniform vec2 resolution;
uniform vec2 mouse;

const float PI = 3.14159265;

vec2 rotate(vec2 v, float a) {
	float sinA = sin(a);
	float cosA = cos(a);
	return vec2(v.x * cosA - v.y * sinA, v.y * cosA + v.x * sinA);
}

float square(vec2 uv, float d) {
	return max(abs(uv.x), abs(uv.y)) - d;
}

float smootheststep(float edge0, float edge1, float x)
{
    x = clamp((x - edge0)/(edge1 - edge0), 0.0, 1.0) * 3.14159265;
    return 0.5 - (cos(x) * 0.5);
}


void main(void)
{
    vec2 uv = vertTexCoord.xy - vec2(.5,.5);
		uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

//	uv.x *= iResolution.x / iResolution.y;
	uv *= 1.5;

	float period = 10.0;
	float timer = time / period;
	timer = mod(timer, 1.0);
	timer = smootheststep(0.0, 1.0, timer);

	gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
	for (int i = 0; i < 9; i++) {
		float n = float(i);
		float size = 1.0 - n / 9.0;
		float rotateAmount = (n * 0.5 + 0.25) * PI * 2.0;
		if (square(rotate(uv, -rotateAmount * timer), size) < 0.0) gl_FragColor.rgb = vec3(1.0);
		float blackOffset = mix(1.0 / 4.0, 1.0 / 2.0, n / 9.0) / 9.0;
		if (square(rotate(uv, -(rotateAmount + PI / 2.0) * timer), size - blackOffset) < 0.0) gl_FragColor.rgb = vec3(0.0);
	}
}
