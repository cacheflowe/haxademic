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

	vec2 pos = vertTexCoord.xy - vec2(.5,.5);
  pos.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

//	vec2 pos = -1.0+2.0*( gl_FragCoord.xy / resolution.xy );
//	pos.x *= resolution.x/resolution.y;
	vec2 p = pos;
	float color = 0.0;
	float time = time/3.0;
	for (int i = 0; i < 10; i++) {
		p = vec2(sin(time)*p.x - cos(time)*p.y, sin(time)*p.y + cos(time)*p.x);
		p = abs(p);
		p -= color;
		color += sin(float(i)+length(pos))*length(p);
	}

	gl_FragColor = vec4( sin(color*8.0)*0.5+0.5 );

}
