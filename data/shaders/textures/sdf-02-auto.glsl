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


#define ITERATIONS 256


float smin( float a, float b, float k )
{
    float res = exp( -k*a ) + exp( -k*b );
    return -log( res )/k;
}

float smin( float a, float b )
{
    return smin(a, b, 12. + 4. * sin(time/20.));
}


float sdCylinder( vec3 p, vec3 c )
{
  return length(p.xz-c.xy)-c.z;
}

float udRoundBox( vec3 p, vec3 b, float r ) {
  return length(max(abs(p)-b,0.0))-r;
}


float opBlend( vec3 p ) {
    vec3 boxSize = vec3(0.02 + 0.1 * sin(p.z/10.), 0.03 + 0.2 * sin(p.z/20.), 0.25);
    float d1 = udRoundBox( p, boxSize, 0.1);
    vec3 cylinderSize = vec3(0.01 + 0.005 * sin(p.z/10.), 0.01 + 0.02 * sin(p.z/20.), 0.01);
    float d2 = sdCylinder(p, cylinderSize);
    //return smin( d1, d2 );
    return smin( d1, d2, 12. + 4. * sin(time/2.) );
}

float opRep( vec3 p, vec3 spacing ) {
    vec3 q = mod(p, spacing) - 0.5 * spacing;
    return opBlend(q);
}

void main( void ) 
{
    // 1 : retrieve the fragment's coordinates
    vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);

	// 2 : camera position and ray direction
	vec3 pos = vec3( 0, time/2., time/1. );
	vec3 dir = vec3( uv.x, uv.y, 1.0 + 0.9 * sin(time/1.) );
 
	// 3 : ray march loop
    // ip will store where the ray hits the surface
	vec3 ip;
 
	// variable step size
	float t = 0.0;
	float findThresh = 0.0001;
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
        vec3 spacings = vec3(0.7 + 0.4 * sin(time/4.), 0.5, 0.5);
		temp = opRep( ip, spacings );
		if( temp < findThresh ) {
			float r = 0.5 + 0.2 * sin(ip.z/2. + time/2. + ip.y/4.);
			float g = 0.3 + 0.2 * sin(ip.z/4. + time/2. - ip.y/2.);
			float b = 0.6 + 0.3 * sin(ip.z/3. + time/2. + ip.y/1.);
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
	gl_FragColor = vec4(ip - (float(last_i)/0.5) / float(ITERATIONS), 1.0 );
}
