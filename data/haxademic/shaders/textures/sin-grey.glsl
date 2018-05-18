// from: http://glsl.heroku.com/e#12132.1
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
uniform vec2 mouse;
uniform vec2 resolution;

#define PI 3.14159

void main( void ) {

	vec2 p = vertTexCoord.xy - vec2(.5,.5);
  p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	float sx = 0.2 * (p.x + 0.5) * sin( 25.0 * p.x - 5. * time);
	float dy = 1./ ( 100. * abs(p.y - sx));
	//dy += 1./ (20. * length(p - vec2(p.x, 0.)));
	gl_FragColor = vec4( (p.x + 0.5) * (1.-dy), (p.x + 0.5) * (1.-dy), (p.x + 0.5) * (1.-dy), 1.0 );
}
