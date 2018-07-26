#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D tex1;
uniform sampler2D tex2;

float rgbToFloat(vec3 color) {
  return (color.r + color.g + color.b) / 3.;
}

void main() {
	vec2 uv = vertTexCoord.xy;
  vec3 color1 = texture2D(tex1, uv).rgb;
  vec3 color2 = texture2D(tex2, uv).rgb;
  float color = rgbToFloat(color1 - color2);
  color = smoothstep(0.05, 0.7, color);
	gl_FragColor = vec4(vec3(color), 1.);
}
