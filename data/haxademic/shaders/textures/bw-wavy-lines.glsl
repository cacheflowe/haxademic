// from: http://glsl.heroku.com/e#15083.0

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

#define pi 3.141592652

void main( void ) {
  vec2 uv = vertTexCoord.xy - 0.5;
  uv.x *= texOffset.y / texOffset.x;		// Correct for base aspect ratio
	gl_FragColor = vec4(vec3(abs(sin(uv.x*50.0+time+cos(uv.y*pi*2.0-pi+time*2.0)*2.0))),1.0);
}
