#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D map;
// uniform vec2 mouse = vec2(0.5);

float TWO_PI = radians(360);

void main() {
	vec2 p = vertTexCoord.xy;
  vec4 texColor = texture2D(map, p);
  float rotate = texColor.r * TWO_PI;
  float amp = texColor.g * 0.02;
  vec2 displace = p + vec2(amp * cos(rotate), amp * sin(rotate));
  vec4 sampleColor = texture2D(texture, displace);
  if(sampleColor.a < 0.1) sampleColor = texture2D(texture, p + vec2(0, 0.03)); // if we have an empty pixel, grab a nearby pixel?
  gl_FragColor = sampleColor;
}
