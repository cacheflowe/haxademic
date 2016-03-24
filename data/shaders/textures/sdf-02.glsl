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


float sdHexPrism( vec3 p, vec2 h ) {
    vec3 q = abs(p);
    return max(q.z-h.y,max((q.x*0.866025+q.y*0.5),q.y)-h.x);
}

float sdSphere( vec3 p, float s ) {
  return length(p)-s;
}

float sdBox( vec3 p, vec3 b )
{
  vec3 d = abs(p) - b;
  return min(max(d.x,max(d.y,d.z)),0.0) +
         length(max(d,0.0));
}

float displacement(vec3 p, float amp, float frequency) {
	return sin(amp * p.x * 2. * frequency) * sin(amp * p.y * frequency) * sin(amp * p.z * frequency);
}

float opDisplace( vec3 p ) {
    // float d1 = sdBox(p, vec3( 0.8 + 0.2 * sin(time/10.), 0.4 + 0.2 * sin(time/10.), 0.3 + 0.2 * sin(time/10.) ) );
    float d1 = sdSphere(p, 0.8 + 0.2 * sin(time/10.));
    float d2 = displacement(p, 1. * sin(time/10.), 1.);
    return d1+d2;
}

 
void main( void ) {
	// 1 : retrieve the fragment's coordinates
    vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);

	// 2 : camera position and ray direction
	vec3 pos = vec3( 0, 0, -4.);
	vec3 dir = normalize( vec3( uv.x, uv.y, 1. ) );
 
	// 3 : ray march loop
    // ip will store where the ray hits the surface
	vec3 ip;
 
	// variable step size
	float t = 0.0;
	float findThresh = 0.01;
	int found = 0;
	int iterations = 128;
	int i;
	for( i = 0; i < iterations; i++) {

        //update position along path
        ip = pos + dir * t;
 
        //gets the shortest distance to the scene
        //break the loop if the distance was too small
        //this means that we are close enough to the surface
 		float temp;

		// make a repeating SDF shape
		temp = opDisplace( ip );
		if( temp < findThresh ) {
			float r = 0.5 + 0.3 * sin(ip.z/5. + ip.x/2.);
			float g = 0.4 + 0.4 * cos(ip.z/5. + ip.y/2.);
			float b = 0.5 + 0.5 * sin(ip.z/5. + ip.x);
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
	gl_FragColor = vec4(ip, 1.0 - float(i) / float(iterations)/2. );
 
}