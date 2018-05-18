// from: http://glsl.heroku.com/e#17417.0
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

void main( void ) {
	mat2 rot = mat2(cos(time), -sin(time), sin(time), cos(time));
	vec2 pos = vertTexCoord.xy - vec2(.5,.5);
	pos.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	pos *= rot;
	float color = abs(pos.x) + abs(pos.y);
	gl_FragColor = vec4(vec3(color / 1.0), 1.0);
}
