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

float udBox( vec3 p, vec3 b ) {
  return length(max(abs(p)-b,0.0));
}
 
// --------------------------------------------------------
// http://www.neilmendoza.com/glsl-rotation-about-an-arbitrary-axis/
// --------------------------------------------------------

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


// --------------------------------------------------------
// https://github.com/stackgl/glsl-inverse/blob/master/index.glsl
// --------------------------------------------------------

mat4 inversee(mat4 m) {
  float a00 = m[0][0];
  float a01 = m[0][1];
  float a02 = m[0][2];
  float a03 = m[0][3];
  float a10 = m[1][0];
  float a11 = m[1][1];
  float a12 = m[1][2];
  float a13 = m[1][3];
  float a20 = m[2][0];
  float a21 = m[2][1];
  float a22 = m[2][2];
  float a23 = m[2][3];
  float a30 = m[3][0];
  float a31 = m[3][1];
  float a32 = m[3][2];
  float a33 = m[3][3];

  float b00 = a00 * a11 - a01 * a10;
  float b01 = a00 * a12 - a02 * a10;
  float b02 = a00 * a13 - a03 * a10;
  float b03 = a01 * a12 - a02 * a11;
  float b04 = a01 * a13 - a03 * a11;
  float b05 = a02 * a13 - a03 * a12;
  float b06 = a20 * a31 - a21 * a30;
  float b07 = a20 * a32 - a22 * a30;
  float b08 = a20 * a33 - a23 * a30;
  float b09 = a21 * a32 - a22 * a31;
  float b10 = a21 * a33 - a23 * a31;
  float b11 = a22 * a33 - a23 * a32;

  float det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06;

  return mat4(
      a11 * b11 - a12 * b10 + a13 * b09,
      a02 * b10 - a01 * b11 - a03 * b09,
      a31 * b05 - a32 * b04 + a33 * b03,
      a22 * b04 - a21 * b05 - a23 * b03,
      a12 * b08 - a10 * b11 - a13 * b07,
      a00 * b11 - a02 * b08 + a03 * b07,
      a32 * b02 - a30 * b05 - a33 * b01,
      a20 * b05 - a22 * b02 + a23 * b01,
      a10 * b10 - a11 * b08 + a13 * b06,
      a01 * b08 - a00 * b10 - a03 * b06,
      a30 * b04 - a31 * b02 + a33 * b00,
      a21 * b02 - a20 * b04 - a23 * b00,
      a11 * b07 - a10 * b09 - a12 * b06,
      a00 * b09 - a01 * b07 + a02 * b06,
      a31 * b01 - a30 * b03 - a32 * b00,
      a20 * b03 - a21 * b01 + a22 * b00) / det;
}

// --------------------------------------------------------
// http://iquilezles.org/www/articles/distfunctions/distfunctions.htm
// --------------------------------------------------------

float opTx( vec3 p, mat4 m )
{
    vec4 q = inversee(m)*vec4(p,0);
    return udBox( q.xyz, vec3(0.6, 0.6, 0.6) );
}

vec3 invert(vec3 vec) {
	return 1.0 - vec;
}


void main( void ) {
	// 1 : retrieve the fragment's coordinates
    vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);

	// 2 : camera position and ray direction
	vec3 pos = vec3( 0, 0, -3);
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
		temp = opTx(ip, rotationMatrix(vec3(0,2.,0.6), 3. * sin(time/5.)));
		// temp = udBox( ip + vec3(0,0,-5.0), vec3(0.2, 0.6, 0.6) );
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