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
// uniform float progressSpeed = 1./255.;

float TWO_PI = radians(360);

void main() {
	// RGBA = X, Y, Rads, Speed
	vec2 p = vertTexCoord.xy;

  // get cur color/position
	vec4 texelColor = texture2D(texture, p);
  float x = texelColor.r;
  float y = texelColor.g;
  float rads = texelColor.b * TWO_PI * 2;
  float progress = texelColor.a;
	// if(progress > 0.9) progress = 0.6 + 0.4 * sin(rads * 10.);// 0.75 + 0.25 * sin(x + y * 10.); // pick a "random" initial speed divisor
	// else progress *= 0.93; // always decelerate
	// progress *= 0.99; // always decelerate
	// progress -= 0.007; // always decelerate
  // move progress
	x += cos(rads) / 2000.;
	y += sin(rads) / 2000.;
	// progress -= 0.01;
  // progress -= progressSpeed;
	// if(progress < 0.) progress = 0.;
  // posOffset.g += 1./255.;   // fall

  // get map color -> progressation
	// float ampCol = texture2D(ampMap, p).r;
  // float ampFromMap = (0.25 + 0.75 * ampCol) * 0.025;
  // vec4 targetDir = texture2D(directionMap, posOffset); // texture2D(directionMap, p)			// now getting direction from current posOffsetition in direction map
  // float progressEased = mix(progress, targetDir.r, 0.2);
	// float progressation = progressEased * TWO_PI * 2.;

  // move. progress should be half of progress, because overall amplitude in any direction is 0.5
  // float progress = 1./255.;
  // posOffset.x = posOffset.x + progress * cos(rotation);
  // posOffset.y = posOffset.y + progress * sin(rotation);

	// write back to texture
  gl_FragColor = vec4(x, y, texelColor.b, texelColor.a *= 0.95);
}
