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

float TWO_PI = radians(360);

vec2 wrappedPos(vec2 pos) {
  if(pos.x > 1.) pos.x = pos.x - 1.;
  if(pos.x < 0.) pos.x = pos.x + 1.;
  if(pos.y > 1.) pos.y = pos.y - 1.;
  if(pos.y < 0.) pos.y = pos.y + 1.;
  return pos;
}
vec2 resetPos(vec2 pos) {
  if(pos.x >= 1. || pos.x <= 0. || pos.y >= 1. || pos.y <= 0.) pos = vec2(0.5);
  return pos;
}

void main() {
	vec2 p = vertTexCoord.xy;

  // get cur color/position
	vec4 texelColor = texture2D(texture, p);
  vec2 pos = texelColor.rg;
  float rot = texelColor.b;

  // get map color -> rotation
	float ampCol = texture2D(ampMap, p).r;
  float ampFromMap = (0.25 + 0.75 * ampCol) * 0.025;
  vec4 targetDir = texture2D(directionMap, pos); // texture2D(directionMap, p)			// now getting direction from current position in direction map
  float rotEased = mix(rot, targetDir.r, 0.2);
	float rotation = rotEased * TWO_PI * 2.;

  // move
  pos.x = pos.x + ampFromMap * cos(rotation);
  pos.y = pos.y + ampFromMap * sin(rotation);
	float z = 1.; // 0.5 + 0.5 * sin(ampCol * TWO_PI * 4.);

	// wrap position and write back to texture
	pos = wrappedPos(pos);
	// pos = resetPos(pos);
  gl_FragColor = vec4(pos.x, pos.y, rotEased, z);
}
