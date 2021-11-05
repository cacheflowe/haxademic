#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec2 offset = vec2(0.);
uniform float zoom = 1.;
uniform float rotation = 0.;

// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

// Simplex Noise (http://en.wikipedia.org/wiki/Simplex_noise), a type of gradient noise
// that uses N+1 vertices for random gradient interpolation instead of 2^N as in regular
// latice based Gradient Noise.

vec2 hash( vec2 p ) {
	p = vec2(
		dot(p,vec2(127.1,311.7)),
		dot(p,vec2(269.5,183.3)) 
	);
	return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

float noise( in vec2 p ) {
		const float K1 = 0.366025404; // (sqrt(3)-1)/2;
		const float K2 = 0.211324865; // (3-sqrt(3))/6;
		vec2 i = floor( p + (p.x+p.y)*K1 );
		vec2 a = p - i + (i.x+i.y)*K2;
		vec2 o = (a.x>a.y) ? vec2(1.0,0.0) : vec2(0.0,1.0); //vec2 of = 0.5 + 0.5*vec2(sign(a.x-a.y), sign(a.y-a.x));
		vec2 b = a - o + K2;
		vec2 c = a - 1.0 + 2.0*K2;
		vec3 h = max( 0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );
		vec3 n = h*h*h*h*vec3( dot(a,hash(i+0.0)), dot(b,hash(i+o)), dot(c,hash(i+1.0)));
		return dot( n, vec3(70.0) );
}

vec2 rotateCoord(vec2 uv, float rads) {
	uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
	return uv;
}

float noiseValForCoord(vec2 p, float zoomMult, vec2 offset) {
	p *= zoomMult;
	p = rotateCoord(p, rotation);
	p += offset;

	float f = 0.0;
	f = noise( 1.0 * p );
	f = 0.5 + 0.5 * f;
	return f;
}

// -----------------------------------------------

void main() {
	vec2 p = vertTexCoord.xy - 0.5;
	p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	gl_FragColor = vec4(
		noiseValForCoord(p, zoom, offset + vec2(0.)), 
		noiseValForCoord(p, zoom * 2, offset + vec2(20.)), 
		noiseValForCoord(p, zoom * 4, offset + vec2(40.)), 
		1.0 
	);
}
