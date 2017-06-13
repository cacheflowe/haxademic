#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D tex1;
uniform sampler2D map;

void main() {
	vec2 p = vertTexCoord.xy;
  vec3 maskColor = texture2D(map, p).rgb;
  float mixValue = (maskColor.r + maskColor.g + maskColor.b) / 3.;
  vec3 color1 = texture2D(tex1, p.xy).rgb;
  vec3 color2 = vec3(1.);
  vec3 color = mix(color1, color2, mixValue);
	gl_FragColor = vec4(color, 1.);
}
