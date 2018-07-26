// from: https://www.shadertoy.com/view/MdfXRs


#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float time;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float amplitude = 1.0; 

#define pi 3.141592653589793238462643383279


void main() {
	vec2 uv = vertTexCoord.xy;
	vec2 med = vec2(0.5,0.5);//(uv*0.5) - 1.0;
	vec2 disVec = med-uv;
	float l = length(disVec) * amplitude;
	float ll = 1.0 - l*l;
	vec2 dist = med - disVec*ll;
	gl_FragColor = texture2D(texture, dist);
}