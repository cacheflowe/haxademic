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

#define ITERATIONS 67
#define SDF_THRESHOLD 0.0001
#define CUBE_SIZE 0.6
#define BG_COLOR vec3(1,1,1)
#define PI 3.141592653589793238462643383


// --------------------------------------------------------
// http://www.neilmendoza.com/glsl-rotation-about-an-arbitrary-axis/
// updated by @stduhpf to be 3x3 - thank you!
// also thanks to @FabriceNeyret2 for code optimizations.
// --------------------------------------------------------

mat3 rotationMatrix(vec3 m,float a) {
    m = normalize(m);
    float c = cos(a),s=sin(a);
    return mat3(c+(1.-c)*m.x*m.x,
                (1.-c)*m.x*m.y-s*m.z,
                (1.-c)*m.x*m.z+s*m.y,
                (1.-c)*m.x*m.y+s*m.z,
                c+(1.-c)*m.y*m.y,
                (1.-c)*m.y*m.z-s*m.x,
                (1.-c)*m.x*m.z-s*m.y,
                (1.-c)*m.y*m.z+s*m.x,
                c+(1.-c)*m.z*m.z);
}

// --------------------------------------------------------
// http://iquilezles.org/www/articles/distfunctions/distfunctions.htm
// --------------------------------------------------------

float udBox( vec3 p, vec3 b ) {
  return length(max(abs(p)-b,0.0));
}

float udBoxTwisted( vec3 p, vec3 b, float twist )
{
    float c = cos(twist*p.y);
    float s = sin(twist*p.y);
    mat2  m = mat2(c,-s,s,c);
    vec3  q = vec3(m*p.xz,p.y);
    return udBox(q, b);
}

void main( void ) {
    // basic raymarching template from @nicoptere: https://www.shadertoy.com/view/ldtGD4
    // 1 : get fragment's coordinates
    vec2 uv = vertTexCoord.xy;
    uv -= 0.5;									// Move to center
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

    // 2 : camera position and ray direction
    float cubeZ = -3.;
	vec3 pos = vec3( 0, 0, cubeZ);
	vec3 dir = normalize( vec3( uv.x, uv.y, 1.) );

	// 3 : ray march loop. ip will store where the ray hits the surface
	vec3 ip;

	// variable step size
	float t = 0.0;
	int found = 0;
    int last_i = 0;
    float timeSlow = time/1.;

	for(int i=0; i < ITERATIONS; i++) {
		last_i = i;

        //update position along path
        ip = pos + dir * t;

        // gets the shortest distance to the sdf shape. break the loop if the distance was too small. this means that we are close enough to the surface
    	vec3 ipRotated = ip * rotationMatrix(vec3(0.,-3.,0.7), 3.3 * sin(timeSlow));
        // float temp = udBox( ipRotated, vec3(CUBE_SIZE) );
        float temp = udBoxTwisted( ipRotated, vec3(CUBE_SIZE), -sin(PI*0.5 + timeSlow) * 1.2 );
		if( temp < SDF_THRESHOLD ) {
			ip = vec3(
                0.3 + 0.3 * sin(1. + timeSlow + ip.x),
                0.8 + 0.2 * sin(2. + timeSlow + ip.y),
                0.7 + 0.3 * sin(3. + timeSlow + ip.z)
            );
			found = 1;
			break;
		}

		//increment the step along the ray path
		t += temp;
	}

	// make background black if no shape was hit
	if(found == 0) ip = BG_COLOR;

	// 4 : apply color to this fragment
	gl_FragColor = vec4(ip, 1.0);
}
