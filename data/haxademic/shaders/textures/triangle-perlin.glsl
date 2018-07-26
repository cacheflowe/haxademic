// ported from: http://glslsandbox.com/e#28465.0
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


float effect(vec2 uv, vec2 aspect)
{
	return sin((sin(uv.x*4.0 - sin(uv.y*7.0-time)) * sin(uv.y*5.0 + sin(uv.x*10.0+time))) * 4.0 + time);
}

void main( void )
{
	float tile_size = 30.0; // resolution.y / 12.0;
	vec2 aspect = vec2(1.0, 1.0); // resolution / resolution.y
	vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);
	uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	vec2 tuv = floor(uv * tile_size) / tile_size;
	vec2 tdo = mod(uv * tile_size, 1.0);

	float color = 0.0;

	float diag = dot(tdo,vec2(0.5));

	color = step(-effect(tuv, aspect),-diag);

	gl_FragColor = vec4( vec3( color ), 1.0 );

}
