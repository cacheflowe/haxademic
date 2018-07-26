//adapted from http://glslsandbox.com/e#44352.0


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

// 3 hacked in mix levels
uniform float _mix1 = 10.0;
uniform float _mix2 = 1.0;
uniform float _mix3 = 1.0;

uniform float _a = 1.0;


vec3 lungth(vec2 x,vec3 c){
       return vec3(length(x+c.r),length(x+c.g),length(c.b));
}

void main( void ) {

	//fixed res for now
	vec2 res = vec2(1300.,720.);

	//vec2 position = (gl_FragCoord.xy - resolution * 0.5) / resolution.yy;
	vec2 position = (gl_FragCoord.xy - res * 0.5) / res.yy;
	float th = atan(position.y, position.x) / (0.2 * 3.1415926);
	float dd = length(position) + 0.1 + 0.1*sin(time);
	float d = 0.5 / dd + time;

    	vec2 x = gl_FragCoord.xy;
    	vec3 c2=vec3(0,0,0);
    	x=x*Scale*R/R.x;
    	x+tan(x.yx*sqrt(vec2(1,9)))/1.;
    	c2=lungth(sin(x*sqrt(vec2(3,43))),vec3(5,6,7)*Saturation * d);
    	x+=sin(x.yx*sqrt(vec2(73,5)))/5.;
    	c2=2.*lungth(sin(time+x*sqrt(vec2(33.,23.))),c2/9.);
    	x+=sin(x.yx*sqrt(vec2(99,7)))/3.;
    	c2=lungth(sin(x*sqrt(vec2(3.,1.))),c2/2.0);
    	c2=.5+.5*sin(c2*8.);

	vec3 uv = vec3(th + d, th - d, th + sin(d) * 0.45);
	float a = 0.5 + cos(uv.x * 3.1415926 * 4.0) * 0.5;
	float b = 0.5 + cos(uv.y * 3.1415926 * 2.0) * 0.5;
	float c = 0.5 + cos(uv.z * 3.1415926 * 6.0) * 0.5;
	vec3 color = 	mix(vec3(0.5, 0.01, 0.5), 	vec3(0.1, 0.05, 0.2),  pow(a, 0.2)) * _mix1;//3.;
	color += 		mix(vec3(0.8, 0.01, 1.0), 	vec3(0.1, 0.005, 0.2),  pow(b, 0.1)) * _mix2;//0.75;
	color += 		mix(c2, 					vec3(0.1, 0.01, 0.2),  pow(c, 0.1)) * _mix3;//0.75;

	gl_FragColor = vec4( (color * dd), _a);
}
