// converted from: http://glsl.heroku.com/e#11163.1
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
uniform vec2 resolution;

int N = 6;

void main() {
	vec2 v =(vertTexCoord.xy-(resolution*0.5))/min(resolution.y,resolution.x)*10.0;
	float t=time * 0.4,r=2.0;
	for (int i=1;i<N;i++){
		float d=(3.14159265 / float(N))*(float(i)*14.0);
		r+=length(vec2(v.y,v.x))+1.21;
		v = vec2(v.x+cos(r+sin(r)-d)+cos(t),v.y-cos(r+sin(r)+d)+sin(t));
	}
    r = (sin(r*0.05)*0.5)+0.5;
	r = pow(r, 30.0);
	gl_FragColor = vec4(r,pow(max(r-0.75,0.0)*4.0,2.0),pow(max(r-1.875,0.1)*5.0,4.0), 1.0 );
}