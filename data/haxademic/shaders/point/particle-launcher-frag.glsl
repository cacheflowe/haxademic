#ifdef GL_ES
precision lowp float;
precision lowp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D ampMap;
uniform sampler2D directionMap;
uniform float progressSpeed = 1./255.;

float TWO_PI = radians(360);

void main() {
	vec2 p = vertTexCoord.xy;

  // get cur color/position
	vec4 texelColor = texture2D(texture, p);
  float distAmp = texelColor.r;
  float size = 1. + texelColor.g * 10.;
  float rotation = texelColor.b * TWO_PI;
  float progress = texelColor.a;

  // move progress
  progress -= progressSpeed;
	if(progress < 0.) progress = 0.;
  // posOffset.g += 1./255.;   // fall

  // get map color -> speedation
	// float ampCol = texture2D(ampMap, p).r;
  // float ampFromMap = (0.25 + 0.75 * ampCol) * 0.025;
  // vec4 targetDir = texture2D(directionMap, posOffset); // texture2D(directionMap, p)			// now getting direction from current posOffsetition in direction map
  // float speedEased = mix(speed, targetDir.r, 0.2);
	// float speedation = speedEased * TWO_PI * 2.;

  // move. speed should be half of progress, because overall amplitude in any direction is 0.5
  // float speed = 1./255.;
  // posOffset.x = posOffset.x + speed * cos(rotation);
  // posOffset.y = posOffset.y + speed * sin(rotation);

	// write back to texture
  gl_FragColor = vec4(distAmp, texelColor.g, texelColor.b, progress);
}
