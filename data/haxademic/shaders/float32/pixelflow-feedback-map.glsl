/**
 *
 * PixelFlow | Copyright (C) 2016 Thomas Diewald - http://thomasdiewald.com
 *
 * A Processing/Java library for high performance GPU-Computing (GLSL).
 * MIT License: https://opensource.org/licenses/MIT
 *
 */

#version 150
out vec4 glFragColor;

uniform vec2 resolution;
uniform sampler2D	texture;
uniform sampler2D	overlay;
uniform sampler2D	map;
uniform float amp = 0.001;
uniform float samplemult = 1.0;

const float TWO_PI = 6.28318530718;

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
  // get texture colors
  vec2 uv = gl_FragCoord.xy / resolution;
	vec4 color = texture2D(texture, uv);
	vec4 colorOverlay = texture2D(overlay, uv);
  vec4 mapCol = texture2D(map, uv);
  float grayColor = rgbToGray(mapCol);
	float rotate = grayColor * TWO_PI * 2.;
  vec2 displace = uv + vec2(amp * cos(rotate), amp * sin(rotate));
	displace = wrappedPos(displace);
  vec4 displaceColor = texture2D(texture, displace) * vec4(vec3(samplemult), 1.);
	vec4 finalColor = mix(displaceColor, colorOverlay, colorOverlay.a); //
  glFragColor = finalColor;
}
