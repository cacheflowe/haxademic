//check board by uggway - from http://glsl.heroku.com/e#12194.0
// yay faux perspective -jz
// from: https://www.shadertoy.com/view/MdBGRm
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

//#define CM

vec3 check(vec2 p, float y, float s)
{
	float c = clamp(floor(mod(p.x/s+floor(p.y/s),2.0))*s,0.1,0.9)*2.0;
	c *= c;
	return vec3(c);
}

void main( void ) {

	vec2 p = vertTexCoord.xy;
	p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	vec3 col = vec3(1.0);

	float y = p.y + (p.y + (cos((cos(time*0.2+p.y)-time+p.x))*0.5));// + sin(p.x*20.)*0.05;
	vec2 uv;
	uv.x = p.x/y;
	uv.y = 1.0/abs(y)+time/3.0;
	col = check(uv, y, 0.50)*length(y);
	float t = pow(abs(y),0.0);

	gl_FragColor = vec4( col*t, 1.0 );

}
