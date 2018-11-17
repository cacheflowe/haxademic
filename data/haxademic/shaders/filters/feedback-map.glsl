#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D map;
uniform float amp = 0.01;
uniform float samplemult = 1.0;
uniform float alphaMult = 1.;

float TWO_PI = radians(360);

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

vec2 wrappedPos(vec2 pos) {
  if(pos.x > 1.) pos.x = 0.;
  if(pos.x < 0.) pos.x = 1.;
  if(pos.y > 1.) pos.y = 0.;
  if(pos.y < 0.) pos.y = 1.;
  return pos;
}

void main() {
	vec2 p = vertTexCoord.xy;
  vec4 texColor = texture2D(map, p);
  float grayColor = rgbToGray(texColor);
  float rotate = grayColor * TWO_PI * 3.;
  vec2 displace = p + vec2(amp * cos(rotate), amp * sin(rotate));
  // displace = wrappedPos(displace);
  vec4 sampleColor = texture2D(texture, displace) * vec4(vec3(samplemult), alphaMult);
  gl_FragColor = sampleColor;
}
