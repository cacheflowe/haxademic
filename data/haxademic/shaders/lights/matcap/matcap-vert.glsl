#define PROCESSING_COLOR_SHADER

uniform mat4 transformMatrix;
uniform mat3 normalMatrix;

attribute vec4 position;
attribute vec3 normal;

varying vec3 eyeNormal;

void main() { 
  eyeNormal= normalMatrix*normal;
  gl_Position = transformMatrix * position;
}

