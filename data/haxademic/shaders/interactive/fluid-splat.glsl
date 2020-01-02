#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec3 color = vec3(0., 1., 0.);
uniform vec2 point = vec2(0.5);
uniform float radius = 0.01;
uniform float strength = 0.6;

void main () {
  // draw colored circle on top of existing texture
  vec2 uv = vertTexCoord.xy;
  vec2 p = uv - point;
  p.x *= texOffset.y / texOffset.x;		// correct aspect ratio
  vec3 splat = exp(-dot(p, p) / radius) * color;
  vec3 base = texture2D(texture, uv).xyz;
  gl_FragColor = vec4(base + splat * strength, 1.0);
}