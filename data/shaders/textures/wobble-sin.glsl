// Warped Sin Wave
// By: Brandon Fogerty
// xdpixel.com
// ported from: http://glslsandbox.com/e#26939.0

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time;



void main (void) {
	vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);
	uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	float t = abs( 1.0 / (sin( uv.y + sin( time + uv.x * 10.0 ) * uv.x ) * 10.0) );
	vec3 finalColor = vec3( t * 0.8, t * 0.8, t * 0.8 );

	gl_FragColor = vec4( finalColor, 1.0 );
}
