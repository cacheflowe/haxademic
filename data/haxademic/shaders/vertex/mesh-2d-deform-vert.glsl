
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

void main() {

	// Calculating texture coordinates, with r and q set both to one
	// And pass values along to fragment shader
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color; // vec4(0., 1., 0., 1.);
  vert = vertex.xyz;
  norm = normal;

	// get displacement map color and map to displace x/y coords
	vec4 dv = texture2D(displacementMap, vertex.xy * 0.00001);
  float luma = rgbToGray(dv);
	float offsetX = cos(luma * TWO_PI);
	float offsetY = sin(luma * TWO_PI);
	// vertColor = luma; // vec4(0., 1., 0., 1.);
	gl_Position = transform * vec4(vertex.x + displaceAmp * offsetX, vertex.y + displaceAmp * offsetY, vertex.z, 1.0);
}
