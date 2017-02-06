// ported from: http://glslsandbox.com/e#26700.0
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


float evalZ(float fx, float fy){
	return sin(0.5*time+0.3*fx)*cos(0.5*time+0.8*fy);
}

const int Nx = 9;
const int Ny = 6;

void main (void) {
	vec2 pos = vertTexCoord.xy - vec2(0.5, 0.5);
	pos.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	vec3 rgb = vec3(0.0);

	for(int x = -Nx; x < Nx; ++x){
		for(int y = -Ny; y < Ny; ++y){
			float fx = float(x);
			float fy = float(y);
			float z = evalZ(fx,fy);
			vec3 p = vec3(0.1*fx, 0.1*float(y), z);
			p = p*(1.+0.1*z);

			float s = 0.01*abs(p.z)+0.002;
			float R0 = 0.01+0.01*z;

			float d = distance(pos, p.xy);
			float i = 1.-smoothstep(R0-2.*s,R0+2.*s,d);
			i*=(1.)/(0.1+300.*s);

			rgb+=i;
		}
	}

	gl_FragColor = vec4(rgb,1.);
}
