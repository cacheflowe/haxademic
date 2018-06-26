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
uniform sampler2D	map;
uniform float time;

void main() {
  // get texture colors
  vec2 uv = gl_FragCoord.xy / resolution;
  vec4 color = texture2D(texture, uv);
  vec4 mapCol = texture2D(map, uv);
  // set color based on trig from map
  color.r += sin(mapCol.r * 6.28 + time) * 0.01;
  color.g += sin(mapCol.g * 6.28 + time) * 0.01;
  color.b += sin(mapCol.b * 6.28 + time) * 0.01;
  glFragColor = vec4(color.r, color.g, color.b, color.a);
}
