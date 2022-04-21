uniform mat4 modelviewMatrix;
uniform mat4 transformMatrix;
uniform mat3 normalMatrix;
uniform mat4 texMatrix;

uniform vec4 lightPosition;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;
attribute vec2 texCoord;
attribute vec4 specular;
attribute float shininess;

varying vec3 ecPosition;
varying vec3 ecNormal;
varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec4 vertSpecular;
varying float vertShininess;

varying vec3 lightDir;

void main() {
  gl_Position = transformMatrix * position;    
  ecPosition = vec3(modelviewMatrix * position);  
  ecNormal = normalize(normalMatrix * normal);
  vertSpecular = specular;
  vertShininess = shininess;
  vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
  lightDir = normalize(lightPosition.xyz - ecPosition);  
  vertColor = color;
}