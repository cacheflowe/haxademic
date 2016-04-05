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


#define ITERATIONS 128

float sdHexPrism( vec3 p, vec2 h ) {
    vec3 q = abs(p);
    return max(q.z-h.y,max((q.x*0.866025+q.y*0.5),q.y)-h.x);
}

float opRep( vec3 p, vec3 spacing ) {
    vec3 q = mod(p, spacing) - 0.5 * spacing;
    return sdHexPrism( q, vec2( 0.2 + 0.1 * cos(time/2.), 1.4 + 0.9 * sin(3.14 + time/2.) ) );
}

void main( void ) 
{
	// 1 : retrieve the fragment's coordinates
    vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);

	// 2 : camera position and ray direction
	// vec3 pos = vec3( sin(iGlobalTime), 0, sin(iGlobalTime) * 5. );
	vec3 pos = vec3( sin(time/1.), 0, sin(time/1.) * 5.);
	vec3 dir = normalize( vec3( uv, 1. ) );
 
	// 3 : ray march loop
    // ip will store where the ray hits the surface
	vec3 ip;
 
	// variable step size
	float t = 0.0;
	float findThresh = 0.001;
	int found = 0;
    int last_i = 0;
    
	for(int i = 0; i < ITERATIONS; i++) {
		last_i = i;
        
        //update position along path
        ip = pos + dir * t;
 
        //gets the shortest distance to the scene
        //break the loop if the distance was too small
        //this means that we are close enough to the surface
 		float temp;

		// make a repeating SDF shape
		temp = opRep( ip, vec3(2.5 + 2.0 * sin(time/2.) ) );
		if( temp < findThresh ) {
			float r = 0.7 + 0.3 * sin(ip.z/8. + ip.x/2.);
			float g = 0.6 + 0.3 * cos(ip.z/8. + ip.y/2.);
			float b = 0.5 + 0.4 * sin(ip.z/8. + ip.x);
			ip = vec3(r, g, b);
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
    // subtract from color as distance increases
	gl_FragColor = vec4(ip - float(last_i) / float(ITERATIONS), 1.0 );
}