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

uniform vec2  wh;
uniform sampler2D	tex;

void main() {
  ////////////////////////////////////////
  // post-process
  vec2 uv = gl_FragCoord.xy / wh;
  vec4 color = texture(tex, uv);
  // vec4 color = texture(tex, gl_FragCoord.xy);
  color.r = color.r + 0.001;
  color.g += 0.001;
  color.b += 0.001;
  if(color.r >= 1.) color.r = 0.;
  if(color.g >= 1.) color.g = 0.;
  if(color.b >= 1.) color.b = 0.;
  glFragColor = vec4(color.r, color.g, color.b, color.a);
  ////////////////////////////////////////


  // vec4 blur = texture(tex, gl_FragCoord.xy);

  // for(int i = 1; i <= +radius; i++){
  //   blur += texture(tex, (gl_FragCoord.xy + dir * i) * wh_rcp);
  //   blur += texture(tex, (gl_FragCoord.xy - dir * i) * wh_rcp);
  // }

  // glFragColor = blur / float(radius * 2 + 1);

  ////////////////////////////////////////
  // generative colors
  // glFragColor = vec4(0.5 + 0.5 * sin(gl_FragCoord.x / 64.), 1., 1., 1.);
  ////////////////////////////////////////
}
