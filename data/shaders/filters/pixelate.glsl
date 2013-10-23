#ifdef GL_ES
precision mediump float;
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
	uv = floor(uv * divider)/ divider;
	gl_FragColor = texture2D(texture, uv);
}