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
uniform float userX;
uniform float userZ;


float sdHexPrism( vec3 p, vec2 h ) {
    vec3 q = abs(p);
    return max(q.z-h.y,max((q.x*0.866025+q.y*0.5),q.y)-h.x);
}

float opRep( vec3 p, vec3 spacing ) {
    vec3 q = mod(p, spacing) - 0.5 * spacing;
    return sdHexPrism( q, vec2( 0.2 + 0.1 * cos(time/20.), 1.4 + 0.9 * sin(3.14 + time/20.) ) );
}
 
void main( void ) {
	// 1 : retrieve the fragment's coordinates
    vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);

	// 2 : camera position and ray direction
	// vec3 pos = vec3( sin(time/10.), 0, sin(time/10.) * 5.); // auto
	vec3 pos = vec3( userX, 0, userZ);
	vec3 dir = normalize( vec3( uv, 1. ) );
 
	// 3 : ray march loop
    // ip will store where the ray hits the surface
	vec3 ip;
 
	// variable step size
	float t = 0.0;
	float findThresh = 0.001;
	int found = 0;
	int iterations = 256;
	int i;
	for( i = 0; i < iterations; i++) {

        //update position along path
        ip = pos + dir * t;
 
        //gets the shortest distance to the scene
        //break the loop if the distance was too small
        //this means that we are close enough to the surface
 		float temp;

		// make a repeating SDF shape
		// temp = opRep( ip, vec3(2.5 + 2.0 * sin(time/20.) ) ); // auto
		temp = opRep( ip, vec3(2.5 + 2.0) );
		if( temp < findThresh ) {
			float r = 0.5 + 0.3 * sin(ip.z/5. + ip.x/2.);
			float g = 0.3 + 0.3 * cos(ip.z/5. + ip.y/2.);
			float b = 0.5 + 0.4 * sin(ip.z/5. + ip.x);
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
	gl_FragColor = vec4(ip - float(i) / float(iterations), 1.0 );
 
}