#ifdef GL_ES
precision highp float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec2 divider;

void main()
{
	vec2 uv = vertTexCoord.xy;
	uv = floor(uv * divider) / divider;
	vec4 color = texture2D( texture, uv );
	color.a = 1.;
	gl_FragColor = color;
}
