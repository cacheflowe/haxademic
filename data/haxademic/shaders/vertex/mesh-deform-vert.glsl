
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform sampler2D displacementMap;
uniform float displaceAmp = 1.;
uniform int sheet = 0;
uniform int yAxisOnly = 0;

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

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
	// Calculating texture coordinates, with r and q set both to one
	// And pass values along to fragment shader
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color;
  vert = vertex.xyz;
  norm = normal;

	vec4 dv = texture2D( displacementMap, vertTexCoord.xy ); // rgba color of displacement map
  float luma = rgbToGray(dv);
  if(sheet == 1) {
		if(yAxisOnly == 0) {
  		gl_Position = transform * vec4(vertex.x, vertex.y, displaceAmp * luma, 1.0);
		} else {
	  	gl_Position = transform * vec4(vertex.x, vertex.y + displaceAmp * (-0.5 + luma), vertex.z, 1.0);
		}
  } else {
    float offset = 1.0 + displaceAmp * luma;
	  gl_Position = transform * vec4(vertex.x * offset, vertex.y * offset, vertex.z * offset, 1.0);
  }
}


/*
	// Calculating texture coordinates, with r and q set both to one
	// And pass values along to fragment shader
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color;
	
	vec4 dv = texture2D( displacementMap, vertTexCoord.xy ); // rgba color of displacement map
	float df = 0.30*dv.r + 0.59*dv.g + 0.11*dv.b; // brightness calculation to create displacement float from rgb values
	float offset = 1.0 + displaceStrength * df;
	gl_Position = transform * vec4(vertex.x * offset, vertex.y * offset, vertex.z * offset, 1.0);

*/