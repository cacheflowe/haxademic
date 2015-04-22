
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
  	// gl_Position = transform * vertex;

	
	vec4 position = transform * vertex;
	position.x *= 2.0 + (0.5 * sin(position.y / 80.0));
	position.y *= 2.0 + (0.5 * cos(position.x / 100.0));
    gl_Position = position;
  	
  	
  	// weird deforms:
  	/*
  	gl_Position.x *= scale * sin(gl_Position.x/100.0);
  	gl_Position.y *= scale * sin(gl_Position.y/100.0);
  	*/
  	
  		
  		
  		// gl_Position.x += scale * 100.0 * sin(0.0001 * (gl_Position.x * gl_Position.y));
  		// gl_Position.y += scale * 100.0 * cos(0.0001 * (gl_Position.x * gl_Position.y));
  		
  		
  	//if(scale > 1.0) {
// 			gl_Position = transform * vertex * vec4(scale, scale, scale, scale);
//  		gl_Position.x = gl_Position.x * scale;
//  		gl_Position.y = gl_Position.y * scale;
// 			gl_Position.z = gl_Position.z * scale;
//  		gl_Position.z = scale * vertNormal.z;

		//gl_Position = (transform * vec4(vertex.x * scale, vertex.y * scale, vertex.z, 1));
		//vec4 newVertexPos;
   		///newVertexPos = vec4(normal * scale * 200.0, 0.0) + vertex;
   		//gl_Position = (transform * vertex) + scale * vec4(normal, 1);
   		// gl_Position.x = gl_Position.x + scale * noise(x);
		// gl_Position.y = gl_Position.y + scale * noise(y);
  	//}
    
  vertColor = color;
  vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}