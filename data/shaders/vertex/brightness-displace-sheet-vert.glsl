
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform sampler2D displacementMap;
uniform float displaceStrength;

attribute vec4 vertex;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;

void main() {
	// Calculating texture coordinates, with r and q set both to one
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	
	vec4 dv = texture2D( displacementMap, vertTexCoord.xy ); // rgba color of displacement map
	float df = 0.30*dv.r + 0.59*dv.g + 0.11*dv.b; // brightness calculation to create displacement float from rgb values
	vec4 newVertexPos = vertex + vec4(normal * df * 2.0, 0.0); // regular vertex position + direction * displacementMap * displaceStrength
	
	
	// Vertex in clip coordinates
	// general displacement:
		// gl_Position = transform * newVertexPos;
	// for a flat sheet:
	// gl_Position = transform * vec4(newVertexPos.x, newVertexPos.y, displaceStrength * df, 1.0);
	gl_Position = transform * vec4(vertex.x, vertex.y, displaceStrength * df, 1.0);
	    
	// pass values along to fragment shader
	vertColor = color;
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}