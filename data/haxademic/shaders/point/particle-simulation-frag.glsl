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

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
	// RGBA = X, Y, Rads, Speed
	vec2 p = vertTexCoord.xy;

  // get cur color/position
	vec4 texelColor = texture2D(texture, p);
  float x = texelColor.r;
  float y = texelColor.g;
  float rads = texelColor.b * TWO_PI * 2;
  float progress = texelColor.a;
  float progressPre = texelColor.a;

	// update progress (alpha)
	progress -= 0.006;
	if(progress < 0.) progress = 0.;	// things get weird if alpha goes negative...

  // move particles. slow down over time, via progress
	float speed = 0.0025;
	x += cos(rads) * speed * progress;
	y += sin(rads) * speed * progress;

	// add a bit of randomness to the launch point so we don't have perfect circles
	if(progressPre == 1.) {
		x += 0.02 * cos(rand(p * 10. * speed * 40. - speed * 20.));
		y += 0.02 * sin(rand(p * 80. * speed * 10. - speed * 5.));
	}

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
  gl_FragColor = vec4(x, y, texelColor.b, progress);
}
