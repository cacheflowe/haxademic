// from: http://glsl.heroku.com/e#15058.0
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
uniform int mode;

void main(void)
{
	vec2 p = vertTexCoord.xy - vec2(.5,.5);
	p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	p *= 0.35; // zoom in
	float r = length(p);
	float a = atan(p.y, p.x);
	if(r > 0.2)
		gl_FragColor = vec4(1, 1, 1, 1) * float(sin(a * 40.0 + r * 300.0 - time * 24.0) > 0.0);
	else if(r > 0.1)
		gl_FragColor = vec4(1, 1, 1, 1) * float(sin(a * 40.0 - r * 350.0 - time * 22.0) > 0.0);
	else
		gl_FragColor = vec4(1, 1, 1, 1) * float(sin(a * 10.0 + r * 400.0 + time * 20.0) > 0.0);
}
