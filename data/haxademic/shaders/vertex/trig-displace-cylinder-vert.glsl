
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform float scale;

attribute float displacement;

attribute vec4 vertex;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;

#define M_PI 3.1415926535897932384626433832795

void main() {
	// normal position
	vertNormal = normalize(normalMatrix * normal);
	vertColor = color;
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);

	// offset x * y	
	vec4 position = transform * vertex;
	position.x *= 2.0 + (0.5 * sin(position.y / 80.0));
	position.y *= 2.0 + (0.5 * cos(position.x / 100.0));
    gl_Position = position;
}