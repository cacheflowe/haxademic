// from: https://www.shadertoy.com/view/3tlfR7
// A modification of David Hoskins's caustics shader: https://www.shadertoy.com/view/MdKXDm
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// Ported from a TouchDesigner GLSL TOP to Processing/haxademic conventions -
// iTime -> time, vUV -> vertTexCoord, uTDOutputInfo-based aspect correction -> texOffset,
// TDOutputSwizzle(color)/fragColor -> gl_FragColor.

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;
// Scale of the caustics pattern - higher zoom = smaller/denser caustic cells
uniform float zoom = 1.;
// Overall brightness multiplier - handy for fading the effect in/out when compositing
uniform float amp = 1.;

float h12(vec2 p) {
	return fract(sin(dot(p, vec2(32.52554, 45.5634))) * 12432.2355);
}

float n12(vec2 p) {
	vec2 i = floor(p);
	vec2 f = fract(p);
	f *= f * (3. - 2. * f);
	return mix(
		mix(h12(i + vec2(0., 0.)), h12(i + vec2(1., 0.)), f.x),
		mix(h12(i + vec2(0., 1.)), h12(i + vec2(1., 1.)), f.x),
		f.y
	);
}

float caustics(vec2 p, float t) {
	vec3 k = vec3(p * zoom, t);
	float l;
	mat3 m = mat3(-2, -1, 2, 3, -2, 1, 1, 2, 2);
	float n = n12(p / zoom);
	k = k * m * .5;
	l = length(.5 - fract(k + n));
	k = k * m * .4;
	l = min(l, length(.5 - fract(k + n)));
	k = k * m * .3;
	l = min(l, length(.5 - fract(k + n)));
	return pow(l, 7.) * 25.;
}

void main() {
	// correct aspect ratio - same technique used in filters/godrays.glsl (texOffset.y/texOffset.x
	// = width/height, no extra uniform needed). Note this multiplies uv.x rather than dividing
	// by it like the original TouchDesigner "aspect = width/height; p = uv/aspect" formula did -
	// that TD version actually produces non-isotropic (stretched) scaling once you work through
	// the math, so this is a deliberate correction rather than a literal port.
	vec2 p = vertTexCoord.xy;
	p.x *= texOffset.y / texOffset.x;

	vec3 col = vec3(caustics(p, time * .5));
	gl_FragColor = vec4(col * amp, 1.0);
}
