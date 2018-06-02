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
  vec4 color = texture(texture, uv);
  // keep xy but move z
  vec2 pos = color.rg;
  float z = color.b;
  z += 0.001;
  if(z > 1.) z = 0.;
	// wrap position and write back to texture
  glFragColor = vec4(pos.x, pos.y, z, 1.);
}
