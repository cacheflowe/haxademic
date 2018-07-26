//adapted from http://glslsandbox.com/e#45218.0
//--------------


// A simple, if a little square, water caustic effect.
// David Hoskins.
// htthttps://www.shadertoy.com/view/MdKXDmps://www.shadertoy.com/view/MdKXDm
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

// Inspired by akohdr's "Fluid Fields"
// https://www.shadertoy.com/view/XsVSDm

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

vec2 R = resolution;
vec2 Offset;
vec2 Scale=vec2(0.002,0.002);
float Saturation = 0.8; // 0 - 1;


vec3 lungth(vec2 x,vec3 c){
       return vec3(length(x+c.r),length(x+c.g),length(c.b));
}

#define f length(fract(q*=m*=.6+.1*d++)-.5)
void main( void ) {

	//fixed res for now
	vec2 res = vec2(1024.,720.);

	float d = 0.;
	vec3 q = vec3(gl_FragCoord.xy / resolution.yy-13., time*.2);
	// Yes, I realise this mat3 looks a little gay in a homo way ... :)teehee
	mat3 m = mat3(-2,-1,2, 3,-2,1, -1,1,3);
	vec3 col = vec3(pow(min(min(f,f),f), 7.)*40.);
	gl_FragColor = vec4(clamp(col + vec3(0., 0.35, 0.5), 0.0, 1.0), 1.0);
}
