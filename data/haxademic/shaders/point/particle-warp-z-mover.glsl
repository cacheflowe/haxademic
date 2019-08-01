#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

// uniform sampler2D ampMap;
// uniform sampler2D directionMap;
// uniform float amp = 0.05;

void main() {
	// get cur color/position
	vec2 uv = vertTexCoord.xy;
	vec4 texelColor = texture2D(texture, uv);
  vec2 pos = texelColor.rg;
  float z = texelColor.b;

	// wrap position and write back to texture
  z += 1./255.;
  // z += 1./255. * (1. + mod(pos.x * 20., 3.));	// pixels move at different speeds
	z = mod(z, 1.);
  gl_FragColor = vec4(pos.x, pos.y, z, 1.);
}
