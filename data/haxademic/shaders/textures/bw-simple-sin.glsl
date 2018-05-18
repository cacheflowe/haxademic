// from: http://glsl.heroku.com/e#12347.3

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

void main()
{
    vec2 uv = vertTexCoord.xy - vec2(.5,.5);
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    float theDistance = distance(uv, texOffset.xy);
	gl_FragColor = vec4(sin(theDistance * time * 100.0) );
}
