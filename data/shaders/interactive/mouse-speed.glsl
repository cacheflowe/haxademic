#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec2 mouse = vec2(0.5);
uniform float mouseDir = 0.;
uniform float mouseSpeed = 0.;

float TWO_PI = radians(360);
float radialSamples = 6.;
float sampleDistance = 0.025;

float lerpLooped(float val, float target, float easeFactor) {
  if(target > val + 0.5) target -= 1.;
  else if(target < val - 0.5) target += 1.;
  val = mix(val, target, 0.1);
  if(val > 1.) val -= 1.;
  if(val < 0.) val += 1.;
  return val;
}

void main (void) {
	// vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);
	// uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
  vec2 uv = vertTexCoord.xy;

  float distFromMouse = distance(uv, mouse);
  float mouseThresh = 0.1;
  float mouseAmp = (1. / mouseThresh) * (mouseThresh - distFromMouse);

  // set speed/amp on green channel
  vec4 curColor = texture2D(texture, uv);
  float oldSpeed = curColor.g;
  float speed = max(0., oldSpeed - 0.002);
  if (mouseSpeed > oldSpeed && distFromMouse < mouseThresh) speed = mouseAmp * mouseSpeed;

  // set target radians on blue channel
  float mouseDirTarget = curColor.b;
  if (mouseSpeed > 0 && distFromMouse < mouseThresh) {
    mouseDirTarget = mouseDir / TWO_PI;
  }

  // lerp radians on red channel towards blue channel
  float mouseDirCur = lerpLooped(curColor.r, mouseDirTarget, 0.2);

  // grab surrounding direction vectors and mix in with their average
  // sample neighbor colors in a circle
  float sampleRadians = TWO_PI / radialSamples;
  float totalDir = 0.;
  float totalAmp = 0.;
  for (float angle = 0.; angle < TWO_PI; angle += sampleRadians) {
    vec2 sampleLoc = uv + vec2(cos(angle), sin(angle)) * sampleDistance;
    vec4 curColor = texture2D(texture, sampleLoc);
    totalDir += curColor.b;
    totalAmp += curColor.g;
  }
  float avgSurrounginDir = totalDir / radialSamples;
  mouseDirTarget = lerpLooped(mouseDirTarget, avgSurrounginDir, 0.0025);
  float avgSurrounginSpeed = totalAmp / radialSamples;
  speed = lerpLooped(speed, avgSurrounginSpeed, 0.1);


	gl_FragColor = vec4(vec3(mouseDirCur, speed, mouseDirTarget), 1.0 );
	// gl_FragColor = vec4(vec3(speed, speed, 0), 1.0 );
}
