// from: https://www.shadertoy.com/view/ldyXRR


#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float time;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float amplitude = 0.02;
uniform float frequency = 6.0;

void main() {
    vec2 uv = vertTexCoord.xy;
    uv += sin( (time + uv) * frequency ) * amplitude;
	gl_FragColor = texture2D(texture, uv);
}