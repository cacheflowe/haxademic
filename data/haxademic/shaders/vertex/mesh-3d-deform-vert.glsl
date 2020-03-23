
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform sampler2D displacementMap;
uniform float displaceAmp = 1.;
uniform int sheet = 0;
uniform int yAxisOnly = 0;
uniform int time = 0;
uniform mat4 modelviewInv;

attribute vec4 vertex;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

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

void main() {
	// Calculating texture coordinates, with r and q set both to one
	// And pass values along to fragment shader
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color; // vec4(0., 1., 0., 1.);
	vertNormal = normalize(normalMatrix * normal);
  vert = vertex.xyz;
  norm = normal;

	// get displacement map color and map to displace x/y coords
	// use x/y attributes as normalized uv coords
	vec2 texSize = textureSize(displacementMap, 0);
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
	// rotate
	// set displaced postition
	vec3 newPos = vec3(
		vertex.x + displaceAmp * offsetX,
		vertex.y + displaceAmp * offsetY,
		vertex.z + displaceAmp * offsetZ
	);
	vec3 centerPos = vec3(0.);
	// attempt to set unique rotation for object
	mat4 rotateMat = rotationMatrix(vec3(vertex.x/1., 0.1, 0.1), vertex.x/1.);
	// working/default displacement technique: multiply with original transform matrix
	vec4 finalPosition = transform * vec4(newPos, 1.0);
	gl_Position = finalPosition;//transform * vec4(vec3(vertex.x + displaceAmp * offsetX, vertex.y + displaceAmp * offsetY, vertex.z + displaceAmp * offsetZ), 1.0);



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
}
