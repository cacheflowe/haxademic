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

void main() {
  // get color
  vec2 uv = gl_FragCoord.xy / resolution;
  vec4 color = texture2D(texture, uv);
  // cycle colors up
  color.r += 0.001;
  color.g += 0.001;
  color.b += 0.001;
  if(color.r >= 1.) color.r = 0.;
  if(color.g >= 1.) color.g = 0.;
  if(color.b >= 1.) color.b = 0.;
  // set color back on texture
  glFragColor = vec4(color.r, color.g, color.b, color.a);
}
