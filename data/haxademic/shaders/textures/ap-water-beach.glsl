//adapted from http://glslsandbox.com/e#45218.0 && https://www.shadertoy.com/view/XtGSDz

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;


float sigmoid(float x) {
    return 1.0 / (1.0 + pow(2.71828, -x));
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec3 lungth(vec2 x,vec3 c){
       return vec3(length(x+c.r),length(x+c.g),length(c.b));
}

#define f length(fract(q*=m*=.6+.1*d++)-.5)
void main( void ) {

	vec2 uv = gl_FragCoord.xy / resolution.xy;

	// wavy water
	float d = 0.;
	vec3 q = vec3((gl_FragCoord.xy / resolution.yy-13.) * 2.2, time*.28);
	mat3 m = mat3(-2,-1,2, 3,-2,1, -1,1,3);
	vec3 col = vec3(pow(min(min(f,f),f), 7.)*40.);
    vec4 wC = vec4(clamp(col + vec3(0., 0.35, 0.5), 0.0, 1.0), 1.0);

    // push coast to side
    vec2 uv2 = uv;
    uv2.x = .104 - uv2.x;
    uv2.x = uv2.x * 10.;
    uv2.x = max(uv2.x, 0.);

    // beach
    vec4 water = vec4(0.2, 0.3, 0.8, 1.0);
    vec4 sand = vec4(.949, .686, .607, 1.0); // * 1.4 + rand(uv2)/14.0;
    sand = mix(sand, sand/3.2, 1.0 - sigmoid(uv2.x*10.0 - 6.0));
    water += sin(uv2.x) * cos(uv2.y) / 1.0;
    water += sin(uv2.x) * cos(uv2.y);
    uv2.x += sin(uv2.y * 26.0) / 20.0 * sin(time*3.2);
    uv2.x += sin(uv2.y * 18.0) / 10.0 * cos(time*2.5);
    vec4 wet_sand = mix(sand, vec4(0.8, 0.9, 0.8, 1.0), 1.0 - sigmoid(uv2.x*15.0 - 8.0));
    vec4 wC2 = mix(water, wet_sand, pow(sigmoid(uv2.x*10.0 - 3.0), 1.0));

    // mix water and beach across x
    float wX = uv.x * 2.8;
    wC2 = mix(wC2, wC, wX);

    gl_FragColor = vec4(wC2.r, wC2.g, wC2.b, 1.0);
}
