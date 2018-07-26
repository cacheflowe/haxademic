// from: https://www.shadertoy.com/view/ldBGRW

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 1.;
uniform float stripes = 10.;
uniform vec2 resolution;

void main(void) {
	vec2 uv = vertTexCoord.xy - 0.5;
	uv.x *= texOffset.y / texOffset.x;		// Correct for base aspect ratio
	vec4 color = vec4( .0, .0, .0, 1.0 );
	if ( sin( atan( uv.y, uv.x ) * stripes + time * stripes + length(uv) / stripes ) > 0.0 ) {
		color = vec4( 1., 1., 1., 1.0 );
	}
	gl_FragColor = color;
}
