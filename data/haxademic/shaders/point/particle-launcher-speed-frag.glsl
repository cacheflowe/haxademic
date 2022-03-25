#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float decelInc = 0.0001;
uniform float launchSpeedMult = 0.01;
uniform float flowSpeedAddMult = 0.05;
uniform vec2 gravity = vec2(0.);

uniform sampler2D flowMap;
uniform int flowMode = 0;
uniform float flowAmp = 0.1;
uniform float flowXoffset = 0.;

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
  float speedX = texelColor.b - 0.5;
  float speedY = texelColor.a - 0.5;


  // check optical flow for launchability
  vec2 posFlipY = vec2(x + flowXoffset, 1. - y); // adjust for flipped y and for centered x!
  vec2 opFlowDisplace = texture2D(flowMap, posFlipY).xy - (0.5 - 2./255.);


  // check for launch
  // alpha being set to 1 means it's ready to launch - this is based on origina GPU Particles code
  // let's also check the flow map - only launch if we're on some good flow too
  if(texelColor.a > 0.9) {
    if(length(opFlowDisplace) > 0.0001) {
      // speedX = -0.0001 + (0.0002 * rand(p));
      // speedY = -0.0001 + (0.0002 * rand(p));
      speedX = 0.;
      speedY = 0.;
      speedX -= opFlowDisplace.x * launchSpeedMult;
      speedY += opFlowDisplace.y * launchSpeedMult;

    } else {
      x = -10.;
      y = -10.;
      speedX = 0.;
      speedY = 0.;
    }
  }

	// decelerate speed
	if(speedX > decelInc) speedX -= decelInc;
	if(speedX < -decelInc) speedX += decelInc;
	if(speedY > decelInc) speedY -= decelInc;
	if(speedY < -decelInc) speedY += decelInc;

  // move particles. slow down over time, via progress
	// float speed = decelInc * (1. + 0.2 * rand(p)); // randomize speed a bit for variance
	x += speedX;
	y += speedY;

  // add optical flow if set
  if(flowMode == 1) {
    opFlowDisplace *= flowAmp;
    // opFlowDisplace *= 1. + 0.75 * sin(p.x * 10.);  // use original UV.x for variance in displacement per particle. shouldn't be too uniform
    // opFlowDisplace = normalize(opFlowDisplace) * 0.001;
    if(length(opFlowDisplace) > 0.001) {
      x -= opFlowDisplace.x;
      y += opFlowDisplace.y * 2.;
      speedX -= opFlowDisplace.x * flowSpeedAddMult + gravity.x;
      speedY += opFlowDisplace.y * flowSpeedAddMult + gravity.y;
    }
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
  gl_FragColor = vec4(x, y, speedX + 0.5, speedY + 0.5);
}
