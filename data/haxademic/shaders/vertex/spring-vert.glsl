
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform float amp = 0.;
uniform float time = 0.;

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

void main() {
	// Calculating texture coordinates, with r and q set both to one
	// And pass values along to fragment shader
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color;

  // fatten
  float oscAmp = sin(vertex.y * 0.01 + time) * 0.5 + 0.5;
	// vec3 offsett = normal * (amp * oscAmp);
	vec3 offset = vec3(1., 1. + 0.25 * sin(time), 1.);
  vec3 pos = vertex.xyz * offset;// + offsett;
  vert = pos;
  norm = normal;
	// gl_Position = transform * vec4(vertex.x * offset, vertex.y * offset, vertex.z * offset, 1.0);
	gl_Position = transform * vec4(pos, 1.0);
}
