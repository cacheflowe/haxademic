#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float lifespanStep = 0.003;
uniform float baseSpeed = 0.00075;
uniform vec2 gravity = vec2(0.);

uniform sampler2D flowMap;
uniform int flowMode = 0;
uniform float flowAmp = 0.1;
uniform float flowXoffset = 0.; //0.25;

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
	progress -= lifespanStep;
	if(progress < 0.) progress = 0.;	// things get weird if alpha goes negative...

  // move particles in fragment shader simulation. slow down over time, via progress
  float initialSpeedRandomness = 0.01;
	float speed = baseSpeed * (1. + initialSpeedRandomness * rand(p)); // randomize speed a bit for variance
	x += cos(rads) * speed * progress + gravity.x;
	y += sin(rads) * speed * progress + gravity.y;

  // add optical flow if set
  if(flowMode == 1) {
    vec2 posFlipY = vec2(x + flowXoffset, 1. - y); // adjust for flipped y and for centered x!
    vec2 opFlowDisplace = texture2D(flowMap, posFlipY).xy - (0.5 - 2./255.);
    opFlowDisplace *= flowAmp;
    // opFlowDisplace *= 1. + 0.75 * sin(p.x * 10.);  // use original UV.x for variance in displacement per particle. shouldn't be too uniform
    // opFlowDisplace = normalize(opFlowDisplace) * 0.001;
		x -= opFlowDisplace.x;
		y += opFlowDisplace.y * 4.;
  }

	// add a bit of randomness to the launch point so we don't have perfect circles
	if(progressPre == 1.) {
		// x += 0.02 * cos(rand(p * 10. * speed * 40. - speed * 20.));
		// y += 0.02 * sin(rand(p * 80. * speed * 10. - speed * 5.));
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
