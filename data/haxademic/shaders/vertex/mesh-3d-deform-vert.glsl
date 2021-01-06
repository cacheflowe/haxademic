uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;
uniform mat4 modelviewMatrix;

uniform mat4 projection;
uniform mat4 modelview;

attribute vec4 vertex;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

uniform sampler2D displacementMap;
uniform float displaceAmp = 1.;
uniform int sheet = 0;
uniform int yAxisOnly = 0;
uniform int time = 0;
uniform mat4 modelviewInv;

attribute float x;
attribute float y;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;

varying vec3 vert;
varying vec3 norm;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

#define PROCESSING_POLYGON_SHADER

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

mat4 translate( float x, float y, float z ) {
	return mat4(	1.0,		0,			0,		x,
			 					0, 			1.0,		0,		y,
								0, 			0,	 		1.0,	z,
								0, 			0,			0, 		1);
}

mat4 rotationX( in float angle ) {
	return mat4(	1.0,		0,			0,			0,
			 		0, 	cos(angle),	-sin(angle),		0,
					0, 	sin(angle),	 cos(angle),		0,
					0, 			0,			  0, 		1);
}

mat4 rotationY( in float angle ) {
	return mat4(	cos(angle),		0,		sin(angle),	0,
			 				0,		1.0,			 0,	0,
					-sin(angle),	0,		cos(angle),	0,
							0, 		0,				0,	1);
}

mat4 rotationZ( in float angle ) {
	return mat4(	cos(angle),		-sin(angle),	0,	0,
			 		sin(angle),		cos(angle),		0,	0,
							0,				0,		1,	0,
							0,				0,		0,	1);
}

mat4 rotationMatrix(vec3 axis, float angle)
{
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;

    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  1.,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  1.,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           1.,
                0.0,                                0.0,                                0.0,                                1.0);
} 

////////////////////////////
// Rotation functions from: 
// https://www.geeks3d.com/20141201/how-to-rotate-a-vertex-by-a-quaternion-in-glsl/
// More to look at here:
// https://stackoverflow.com/questions/27215854/rotate-a-sphere-in-an-opengl-shader
////////////////////////////

vec4 quat_from_axis_angle(vec3 axis, float angle)
{ 
  vec4 qr;
  float half_angle = (angle * 0.5) * 3.14159 / 180.0;
  qr.x = axis.x * sin(half_angle);
  qr.y = axis.y * sin(half_angle);
  qr.z = axis.z * sin(half_angle);
  qr.w = cos(half_angle);
  return qr;
}

vec4 quat_conj(vec4 q)
{ 
  return vec4(-q.x, -q.y, -q.z, q.w); 
}

vec4 quat_mult(vec4 q1, vec4 q2)
{ 
  vec4 qr;
  qr.x = (q1.w * q2.x) + (q1.x * q2.w) + (q1.y * q2.z) - (q1.z * q2.y);
  qr.y = (q1.w * q2.y) - (q1.x * q2.z) + (q1.y * q2.w) + (q1.z * q2.x);
  qr.z = (q1.w * q2.z) + (q1.x * q2.y) - (q1.y * q2.x) + (q1.z * q2.w);
  qr.w = (q1.w * q2.w) - (q1.x * q2.x) - (q1.y * q2.y) - (q1.z * q2.z);
  return qr;
}

vec3 rotate_vertex_position(vec3 position, vec3 axis, float angle)
{ 
  vec4 q = quat_from_axis_angle(axis, angle);
  vec3 v = position.xyz;
  return v + 2.0 * cross(q.xyz, cross(q.xyz, v) + q.w * v);
}

////////////////////////////
// End Rotation functions
////////////////////////////

void main() {
	vec4 origin = vec4(x, y, 0., 1.);
	vec4 v = vertex + origin; // vertex * modelviewInv;

	// Calculating texture coordinates, with r and q set both to one
	// And pass values along to fragment shader
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color; // vec4(0., 1., 0., 1.);
	vertNormal = normalize(normalMatrix * normal);
  vert = v.xyz;
  norm = normal;

	// get displacement map color and map to displace x/y coords
	// use x/y attributes as normalized uv coords
	ivec2 texSize = textureSize(displacementMap, 0); 
	vec2 displaceUV = vec2(
		x / float(texSize.x),
		y / float(texSize.y)
	);
	vec4 dv = texture2D(displacementMap, displaceUV);
  float luma = rgbToGray(dv);
	float offsetX = cos(luma * TWO_PI) * 1.5;
	float offsetY = sin(luma * TWO_PI) * 1.5;
	float offsetZ = sin(luma * TWO_PI) * 1.;
	// generative colors
	// Overwriting the `vertColor` attribute breaks things. instead, overwrite its properties
	float gray = (offsetX + offsetY + offsetZ) / 3.;
	vertColor.r = cos(x/100. + gray);
	vertColor.g = sin(y/100. + gray);
	vertColor.b = sin(x/100. + gray);
	
	// set displaced postition
	vec3 newPos = vec3(
		v.x + displaceAmp * offsetX,
		v.y + displaceAmp * offsetY,
		v.z + displaceAmp * offsetZ
	);
	

	// attempt to set unique rotation for object
	// try rotating
	if(mod(x, 10.) == 0. && mod(y, 10.) == 0.) { 
	vec4 worldPos = vec4(newPos.xyz, 1.) * modelviewMatrix; 
	vec3 centerPos = vec3(0.);
	vec3 newPosInv = vec4(newPos.xyz * -1, 1.);
	// newPos.xyz = rotate_vertex_position(newPos.xyz, vec3(0., 0., 1.), float(time)/5.); 
	// vec3 rotated_point = vec3(x, y, 0.) + (worldPos * (newPos-centerPos));
	// newPos.xyz += tmpRot; 
	// newPos.xyz += rotPos; 
	}

	// rotate everything
	vec3 rotatedPos = rotate_vertex_position(newPos, vec3(0., 0., 1.), time/4.); 
	// rotated_point = origin + (orientation_quaternion * (point-origin));
	// mat4 rotateMat = rotationMatrix(vec3(vertex.x/1., 0.1, 0.1), vertex.x/1.);

	// working/default displacement technique: multiply with original transform matrix
	vec4 finalPosition = transform * vec4(rotatedPos, 1.0);

	// write final position
	gl_Position = finalPosition;

	// alternate way of setting finalPosition
	// gl_Position = projection * modelview * vec4(newPos, 1);


	// attempts to rotate
	// transform * rotationY(x/100.) * rotationX(y/20000.) * rotationZ(y/20000.) * vertex;
	// vec4 tmpVert = vec4(newPos, 1.);
	// vertex.x = 0.;
	// vertex.y = 0.;
	// vertex.z = 0.;
	// vec4 finalPosition = transform * rotateMat * vec4(0., 0., 0., 1.); // (vertex - vertex * 0.99); // vec4(newPos, 1.0);
	// vec4 finalPosition = transform * translate(newPos.x, newPos.y, newPos.z) * vec4(newPos, 1.0);
	// finalPosition.x = finalPosition.x + newPos.x;
	// finalPosition.y = finalPosition.y - newPos.y;
	// finalPosition.z = finalPosition.z - newPos.z;
	// vec4 finalPosition = (transform * rotationMatrix(vec3(0.1, 0.9, 0.9), vertex.x/100., newPos * 0.00000000001)) * vec4(newPos, 1.0);
	// vec4 finalPosition = transform * vec4(vec3(vertex.x + displaceAmp * offsetX, vertex.y + displaceAmp * offsetY, vertex.z + displaceAmp * offsetZ), 1.0);;
	// vec4 finalPosition = transform * vertex + vec4(1., 1., 1., 1.) * translate(displaceAmp * offsetX, displaceAmp * offsetY, displaceAmp * offsetZ);
	// gl_Position.x = finalPosition.x * cos(x) - finalPosition.y * sin(x);
	// gl_Position.y = finalPosition.y * sin(x) + finalPosition.y * cos(x)
	// set final position
	// gl_Position += tmpVert;
	// gl_Position.xyz += tmpVert.xyz;
	// gl_Position.y += tmpVert.y;
	// gl_Position.z += tmpVert.z;

	// failed attempt - code suggested elsewhere
	// vec4 q = vec4(vec3(0., 0.3, 0.).xyz, 1.);
	// vec3 temp = cross(q.xyz, v) + q.w * v;
	// vec3 rotated = newPos + 2.0*cross(q.xyz, temp);
}
