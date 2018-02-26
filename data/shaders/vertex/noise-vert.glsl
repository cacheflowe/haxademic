
uniform mat4 modelview;
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform vec4 lightPosition;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;
attribute vec4 vertex;
attribute vec2 texCoord;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 ecNormal;
varying vec3 lightDir;

varying vec3 vert;
varying vec3 norm;

void main() {
  gl_Position = transform * position;
  vec3 ecPosition = vec3(modelview * position);
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color;
  ecNormal = normalize(normalMatrix * normal);
  lightDir = normalize(lightPosition.xyz - ecPosition);
  norm = normal;
  vert = ecPosition;
}