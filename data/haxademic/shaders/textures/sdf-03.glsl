// ported from: http://barradeau.com/blog/?p=575
// o.g. SDF: http://iquilezles.org/www/articles/distfunctions/distfunctions.htm
// also: https://www.shadertoy.com/view/Xds3zN
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

const float twoPi = 6.283185307179586;

vec3 invert(vec3 vec) {
	return 1.0 - vec;
}

float opU( float d1, float d2 ) {
    return min(d1,d2);
}

float sdTriPrism( vec3 p, vec2 h ) {
    vec3 q = abs(p);
    return max(q.z-h.y,max(q.x*0.866025+p.y*0.5,-p.y)-h.x*0.5);
}

float udBox( vec3 p, vec3 b ) {
  return length(max(abs(p)-b,0.0));
}
 
float opRep( vec3 p, vec3 c ) {
    vec3 q = mod(p,c)-0.5*c;
    return opU( sdTriPrism( q, vec2(0.9, 0.6) ), udBox( q + vec3(0,0.9,0), vec3(0.2, 0.6, 0.6) ) );
    // return sdTriPrism( q, vec2(0.9, 0.6) );
}

mat4 rotationMatrix(vec3 axis, float angle)
{
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;
    
    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  0.0,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  0.0,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           0.0,
                0.0,                                0.0,                                0.0,                                1.0);
}

void main( void ) {
	// 1 : retrieve the fragment's coordinates
    vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);

	// 2 : camera position and ray direction
	vec3 pos = vec3( 0, -5.0 * time/twoPi, 2.3 + 0.3 * -sin(time));
	vec3 dir = normalize( vec3( uv.x, uv.y, 1.) );
 
	// 3 : ray march loop
    // ip will store where the ray hits the surface
	vec3 ip;
 
	// variable step size
	float t = 0.0;
	float findThresh = 0.01;
	int found = 0;
	int iterations = 64;
	int i;
	for( i = 0; i < iterations; i++) {

        //update position along path
        ip = pos + dir * t;
 
        //gets the shortest distance to the scene
        //break the loop if the distance was too small
        //this means that we are close enough to the surface
 		float temp;

		// make a repeating SDF shape
		temp = opRep( ip, vec3(5.0 + 0.8 * sin(time), 5.0 + 0.1 * sin(time), 9.0 + 2 * sin(time)) );
		if( temp < findThresh ) {
			float r = 0.5 + 0.3 * sin(2 + sin(time) + ip.z/6. + ip.x/2.);
			float g = 0.6 + 0.4 * cos(1 + sin(time) + ip.x/6. + ip.z/2.);
			float b = 0.6 + 0.3 * sin(1 + sin(time) + ip.z/6. + ip.x);
			ip = vec3(g, r, b);
			found = 1;
			break;
		}
		
		//increment the step along the ray path
		t += temp;
	}
	
	// make background black if no shape was hit
	if(found == 0) {
		ip = vec3(0,0,0);
	}
 
	// 4 : apply color to this fragment
	gl_FragColor = vec4(ip - float(i) / float(iterations)/2., 1.0 );
 
}