#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main()
{
	vec2 uv = vertTexCoord.xy;
  uv.y = 1.0 - uv.y;
	gl_FragColor = texture2D(texture, uv);
}
