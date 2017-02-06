// from: https://www.shadertoy.com/view/Msl3Dj
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
uniform int mode;

float pattern0(vec2 p, float time) { return (sin((abs(p.x)+abs(p.y))*50.0+time*10.0)+1.0)/2.0; }
float pattern1(vec2 p, float time) { return (sin(length(p)*50.0+abs(sin(atan(p.y,p.x)*10.0+time*4.0)*length(p)*5.0)+time*10.0)+1.0)/2.0; }
float pattern2(vec2 p, float time) { return sin(atan(p.y,p.x)*20.0+time*20.0); }

int getPosition(vec2 p) {
	if (p.y < -0.8 && abs(p.x) < 0.3) {
		if (p.x < -0.1) return 0;
		else if (p.x > 0.1) return 2;
	}
	return 1;
}

void main(void) {
	vec2 p = vertTexCoord.xy - vec2(.5,.5);
	p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	int pp = getPosition(p);

	float p0,p1;

	if (mode == 0) {
		p0 = pattern0(p,-time);
		p1 = pattern0(p, time);
	} else if (mode == 1) {
		p0 = pattern1(p,-time);
		p1 = pattern1(p, time);
	} else {
		p0 = pattern2(p,-time);
		p1 = pattern2(p, time);
	}

	float s = mix(p0,p1,smoothstep(0.25,0.27,length(p)));
	gl_FragColor = vec4(vec3(s),1.0);
}
