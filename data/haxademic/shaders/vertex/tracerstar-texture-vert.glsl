
uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 normalMatrix;
uniform mat4 texMatrix;

attribute vec4 position;
attribute vec4 color;
attribute vec2 texCoord;

varying vec4 vertColor;
varying vec4 vertTexCoord;

varying vec3 v_texCoord3D;

uniform mat4 modelviewInv;
uniform float time;


void main() {
	// apply inverse matrix to use models original position as uv coords
	vec4 tmp = position * modelviewInv;
	v_texCoord3D = tmp.xyz;
	gl_Position = transform * position;

	vertColor = color;
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}
