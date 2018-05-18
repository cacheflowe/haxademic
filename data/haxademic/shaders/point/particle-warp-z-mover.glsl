#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D ampMap;
uniform sampler2D directionMap;
uniform float amp = 0.05;

void main() {
	vec2 p = vertTexCoord.xy;

  // get cur color/position
	vec4 texelColor = texture2D(texture, p);
  vec2 pos = texelColor.rg;
  float z = texelColor.b;
  z += 1./256.;
  if(z > 1.) z = 0.;

	// wrap position and write back to texture
  gl_FragColor = vec4(pos.x, pos.y, z, 1.);
}
