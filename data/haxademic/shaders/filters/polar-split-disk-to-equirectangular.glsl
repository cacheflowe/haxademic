// with help from Nikki & Alex Miller

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float crossfade = 1.0;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

void main() {
  // Quick aliases
  float x = vertTexCoord.x;
  float y = 1. - vertTexCoord.y;

	// coords for animated translation
	vec2 origUV = vec2(x, y);

  // Calculate polar theta, r and center of hemisphere. Fix if other hemisphere.
  float theta = TWO_PI * x + PI/2.;
  vec2 center = vec2(0.25, 0.5);
  float r = y;
  if (y > 0.5) {
    center = vec2(0.75, 0.5);
    r = 1.0 - r;
    theta = PI - theta;
  }
  // slight correction
  float rOffset = 0.002 * sin(x * TWO_PI + PI);
  r *= 0.993 + rOffset;

  // Calculate direction from angle, then lookup r distance in the direction. r
  // fits 4 times horizontally and 2 times vertically so have to squash.
  vec2 dir = vec2(cos(theta), sin(theta));
	vec2 mappedUV = center + r * dir * vec2(0.5, 1.0);

	// animate between UVs
	vec2 finalUV = mix(origUV, mappedUV, crossfade);

	// grab final color
  vec4 color = texture2D(texture, finalUV);
  gl_FragColor = color;
}

/*
// pristine version
void main() {
  // Quick aliases
  float x = vertTexCoord.x;
  float y = 1. - vertTexCoord.y;

  // Calculate polar theta, r and center of hemisphere. Fix if other hemisphere.
  float theta = TWO_PI * x;
  vec2 center = vec2(0.25, 0.5);
  float r = y;
  if (y > 0.5) {
    center = vec2(0.75, 0.5);
    r = 1.0 - r;
    theta = PI - theta;
  }

  // Calculate direction from angle, then lookup r distance in the direction. r
  // fits 4 times horizontally and 2 times vertically so have to squash.
  vec2 dir = vec2(cos(theta), sin(theta));
  vec4 color = texture2D(texture, center + r * dir * vec2(0.5, 1.0));

  gl_FragColor = color;
}
*/