// ported from: http://glslsandbox.com/e#25706.0
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


vec3 pin(vec3 v)
{
	vec3 q = vec3(0.0);

	q.x = sin(v.x)*0.5+0.5;
	q.y = sin(v.y+1.0471975511965977461542144610932)*0.5+0.5;
	q.z = sin(v.z/4.1887902047863909846168473723972)*0.5+0.5;

	return normalize(q);
}

vec3 spin(vec3 v)
{
	for(int i = 0; i <2; i++)
	{
		v=pin(v.yzx*6.283185307179586476925286766559);
	}
	return v.zxy;
}

float map(vec3 p) {
	p=2.5*pin(p)-p;;
	return (cos(p.x) + cos(p.y*0.01) + sin(p.z)*0.99);
}


vec2 rot(vec2 r, float a) {
	return vec2(
		cos(a) * r.x - sin(a) * r.y,
		sin(a) * r.x + cos(a) * r.y);
}

void main( void ) {
	// vec2 uv  = ( gl_FragCoord.xy / resolution.xy ) * 2.0 - 1.0;
	vec2 uv = vertTexCoord.xy * 1.0 - 0.5;
	uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	vec3 dir = normalize(vec3(uv, 0.2));
	dir.zy = rot(dir.zy, time * 0.2);
	dir.xz = rot(dir.xz, time * 0.15); dir = dir.yzx;

	vec3 pos = vec3(0, 0, time * 3.0);
	float t = 0.0;
	for(int i = 0 ; i < 120; i++) {
		float temp = map(pos + dir * t) * 0.75;
		if(temp < 0.001) break;
		t += temp;
		dir.xy=rot(dir.xy,temp*0.015);
		dir.yz=rot(dir.yz,temp*0.035);
		dir.zx=rot(dir.zx,temp*0.05);
	}
	vec3 ip = pos + dir * t;
	vec3 tmp = vec3(max(0.01, map(ip + 0.2)) + t * 0.02) + (dir*spin(ip));
	// gl_FragColor = vec4(tmp.r, tmp.g, tmp.b, 1.0); // color version
	gl_FragColor = vec4(tmp.g, tmp.g, tmp.g, 1.0);
}
