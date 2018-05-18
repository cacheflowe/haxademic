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


//the signed distance field function
//used in the ray march loop
float sdfSphere(vec3 p) {
    //a sphere of radius 1.
    return length( p ) - 1.;
}

float sphere( vec3 pos, vec3 center, float radius )
{
    return length( pos - center ) - radius;
}

float box( vec3 pos, vec3 center, vec3 size, float corner )
{
    return length( max( abs( pos-center )-size, 0.0 ) )-corner;
}

float sdTorus( vec3 p, vec2 t )
{
  vec2 q = vec2(length(p.xz)-t.x,p.y);
  return length(q)-t.y;
}

float sdBox( vec3 p, vec3 b )
{
  vec3 d = abs(p) - b;
  return min(max(d.x,max(d.y,d.z)),0.0) +
         length(max(d,0.0));
}


float unite( float a, float b){return min(a, b);}
float subtract( float a, float b ){ return max(-a, b); }
float intersect( float a, float b ){ return max(a, b); }

float sdf(vec3 p) {

    //we build a sphere
    float s = sphere( p, vec3( 0. ), 1.25 );

    //we build a box
    float b = box( p, vec3( 0. ), vec3( 1. ), .0 );

    //we return the combination of both:
    // subtracting the sphere from the box
    return intersect( s,b  );
}

float sdEllipsoid( vec3 p, vec3 r )
{
    return (length( p/r ) - 1.0) * min(min(r.x,r.y),r.z);
}

float sdHexPrism( vec3 p, vec2 h )
{
    vec3 q = abs(p);
    return max(q.z-h.y,max((q.x*0.866025+q.y*0.5),q.y)-h.x);
}

float opRep( vec3 p, vec3 c )
{
    vec3 q = mod(p,c)-0.5*c;
    // return sdEllipsoid( q, vec3( 0.2,0.2, 0.9 ) );
    return sdHexPrism( q, vec2( 0.2 + 0.1 * cos(time/20.), 1.0 + 0.9 * sin(3.14 + time/20.) ) );
    // return sdCylinder( q, vec3( 0. ) );
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

float opTwist( vec3 p, vec3 dimensions, float twist )
{
    float c = cos(twist*p.y);
    float s = sin(twist*p.y);
    mat2  m = mat2(c,-s,s,c);
    vec3  q = vec3(m*p.xz,p.y);
    return sdBox(q, dimensions);
}
 
void main( void ) {
 
	// 1 : retrieve the fragment's coordinates
    vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);

	//vec2 uv = ( gl_FragCoord.xy / resolution.xy ) * 2.0 - 1.0;
	////preserve aspect ratio
	//uv.x *= resolution.x / resolution.y;
 
 
	// 2 : camera position and ray direction
	vec3 pos = vec3( sin(time/10.), 0,sin(time/10.) * 5.);
	vec3 dir = normalize( vec3( uv, 1. ) );
 
 
	// 3 : ray march loop
    // ip will store where the ray hits the surface
	vec3 ip;
 
	// variable step size
	float t = 0.0;
	float findThresh = 0.001;
	int found = 0;
	for( int i = 0; i < 256; i++) {
 
        //update position along path
        ip = pos + dir * t;
 
        //gets the shortest distance to the scene
        //break the loop if the distance was too small
        //this means that we are close enough to the surface
 		float temp;
 		
		/*
		temp = sdf( ip ); 
		if( temp < 0.01 ) break;
		
		// do it again for another SDF shape
		temp = sdfSphere( ip );
		if( temp < 0.01 ) break;
		*/

		
				
		temp = sdBox( ip + vec3(0,0,-300.) , vec3( 10., 10., 2. ) );
		if( temp < findThresh ) {
			ip = vec3(0,0,0);
			found = 1;
			break;
		}
		
		
		// do it again for a repeating SDF shape
		temp = opRep( ip, vec3(2.5 + 2.0 * sin(time/20.) ) );
		if( temp < findThresh ) {
			float r = 0.5 + 0.3 * sin(ip.z/2. + ip.x/2.);
			float g = 0.3 + 0.3 * cos(ip.z/2. + ip.y/2.);
			float b = 0.5 + 0.4 * sin(ip.z/2. + ip.x);
			ip = vec3(g, r, b);
			found = 1;
			break;
		}
		
		/*
		temp = sdTorus( ip, vec2( 1.0 + cos(time/10.0), 0.5 ) );
		if( temp < findThresh ) {
			// re-color
			// ip = vec3(0.5*ip.r,0.7*ip.g,0.5*ip.b);
			found = 1;
			break;
		}
		
		
		temp = opTwist(ip, vec3( 1., 1., 1. ), sin(time/10.0));
		// temp = sdTorus( ip, vec2( 1.0 + 0.1 * sin(time/10.0), 0.5 ) );
		if( temp < findThresh ) {
			ip = vec3(sin(ip.z), cos(ip.z), sin(ip.z));
			found = 1;
			break; 
		}
		*/
 
		//increment the step along the ray path
		t += temp;
 
	}
	if(found == 0) {
		ip = vec3(0,0,0);
	} else {
		
	}
 
	// 4 : apply color to this fragment
    // we use the result position as the color
    /// gl_FragColor = vec4( ip, 1.0);
	gl_FragColor = vec4( ip, 1.0);
 
}