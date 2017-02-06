//MG - raymarching
//distance function(s) provided by
//http://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
// converted from: http://glsl.heroku.com/e#11082.0
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

float MIN = 0.0;
float MAX = 100.0;
float DELTA = 0.01;
int ITER = 1000;

float sphere(vec3 p, float r) {
	p = mod(p,2.0)-0.5*2.0;
	return length(p)-r;
}

float sdBox( vec3 p, vec3 b )
{
	p = mod(p,2.0)-0.5*2.0;
	vec3 d = abs(p) - b;
	return min(max(d.x,max(d.y,d.z)),0.0) + length(max(d,0.0));
}

float castRay(vec3 o,vec3 d) {
	float delta = MAX;
	float t = MIN;
	for (int i = 0;i <= ITER;i += 1) {
		vec3 p = o+d*t;
		delta = sdBox(p,vec3((sin(time)+1.0)/16.0, (sin(time)+1.0)/2.0, 0.5));
		t += delta;
		if (t > MAX) {return MAX;}
		if (delta-DELTA <= 0.0) {return float(i);}
	}
	return MAX;
}

void main() {
	vec2 p=(vertTexCoord.xy/resolution.y)*1.0;
	p.x-=resolution.x/resolution.y*0.5;
	p.y-=0.5;
	vec3 o = vec3(sin(time/2.0)*2.0,0.0, time*8.0);
	vec3 d = normalize(vec3(p.x,p.y,1.0));

	float t = castRay(o,d);
	vec3 rp = o+d*t;

	if (t < MAX) {
		t = 1.0-t/float(MAX);
		gl_FragColor = vec4(t,t,t,1.0);
	}
	else {
		gl_FragColor = vec4(0.0,0.0,0.0,1.0);
	}
}
