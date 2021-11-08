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
uniform sampler2D flowMap;
uniform float amp = 0.005;
uniform float flowAmp = 0.5;
uniform int flowMode = 1;

float TWO_PI = radians(360);

float random2d(vec2 n) {
	return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

vec2 wrappedPos(vec2 pos) {
  // if(pos.x > 1.) pos.x = pos.x - 1.;
  // if(pos.x < 0.) pos.x = pos.x + 1.;
  if(pos.y > 1.) {
    pos.x = random2d(vertTexCoord.xy * 1000.);
    pos.y = pos.y - 1.;
  }
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
  vec4 ampColor = texture2D(ampMap, p);
  vec2 pos = texelColor.rg;
  float rot = texelColor.b;

  float speedMult = 0.3;
  float turnSpeed = 0.04;

  // get map color -> rotation
	float ampFromMap = ampColor.r;
  ampFromMap *= speedMult;
  vec4 targetDir = texture2D(directionMap, pos); // texture2D(directionMap, p)			// now getting direction from current position in direction map
  float rotEased = mix(rot, targetDir.r, turnSpeed);
	float rotation = rotEased * TWO_PI * 3.;

  // move snow
  // x is on an oscillation
  // y is always moving down at variable speeds
  float xOsc = 0.00025 * cos(pos.y * 10. + 20. * ampColor.g);
  pos.x = pos.x + xOsc;
  pos.y = pos.y + ampFromMap * amp + texelColor.b * 0.001;
	float z = 1.; // 0.5 + 0.5 * sin(ampCol * TWO_PI * 4.);

  // optical flow displacement, to be used with results from `optical-flow-td.glsl`
  // get uv ccords from current normalize position
  if(flowMode == 1) {
    vec2 posFlipY = vec2(pos.x, 1. - pos.y);
    vec2 opFlowDisplace = texture2D(flowMap, posFlipY).xy - 0.5;
    opFlowDisplace *= flowAmp;
    opFlowDisplace *= 1. + 0.75 * sin(p.x * 10.);  // use original UV.x for variance in displacement per particle. shouldn't be too uniform
		pos.x -= opFlowDisplace.x;
		pos.y += opFlowDisplace.y;
  }

	// wrap position and write back to texture
	pos = wrappedPos(pos);
	// pos = resetPos(pos);
  gl_FragColor = vec4(pos.x, pos.y, rotEased, z);
}
