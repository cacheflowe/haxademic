#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D directionMap;
uniform float amp = 0.0025;

float TWO_PI = radians(360);

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

vec2 wrappedPos(vec2 pos) {
  if(pos.x > 1.) pos.x -= 1.;
  if(pos.x < 0.) pos.x += 1.;
  if(pos.y > 1.) pos.y -= 1.;
  if(pos.y < 0.) pos.y += 1.;
  return pos;
}

void main() {
	vec2 p = vertTexCoord.xy;

  // get cur color/position
  vec2 pos = texture2D(texture, p).xy;

  // get map color -> rotation
  vec4 dir = texture2D(directionMap, p);
  float rotation = rgbToGray(dir) * TWO_PI;
  // float rotation = dir.r; // * TWO_PI;

  // move
  // float curAmp = rgbToGray(dir) * amp;
  // pos.x += amp * cos(rotation);
  // pos.y += amp * sin(rotation);
  pos.x += amp * cos(dir.x * TWO_PI);
  pos.y += amp * sin(dir.y * TWO_PI);
  // pos.x += amp;
  // pos.y += 0.01 * rgbToGray(dir) * sin(dir.y * TWO_PI);

  pos = wrappedPos(pos);
  gl_FragColor = vec4(pos.x, pos.y, 0., 1.);
}
