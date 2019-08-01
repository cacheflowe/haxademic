#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
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
	// arrange in circle
  if(z >= 1.) {
		pos.x = 0.5 + cos(time) * 0.3;
		pos.y = 0.5 + sin(time) * 0.3;
		z = 0;
	}
  gl_FragColor = vec4(pos.x, pos.y, z, 1.);
}
