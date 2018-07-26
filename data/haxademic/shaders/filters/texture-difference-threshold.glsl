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
uniform float diffThresh = 0.035;
uniform float falloffBW = 0.06;

float rgbToFloat(vec3 color) {
  return (color.r + color.g + color.b) / 3.;
}

void main() {
  // get colors
	vec2 uv = vertTexCoord.xy;
  float curColor = rgbToFloat(texture2D(texture, uv).rgb);                    // b/w final activity map
  vec3 color1 = texture2D(tex1, uv).rgb;                                      // texture 1
  vec3 color2 = texture2D(tex2, uv).rgb;                                      // texture 2
  // check color difference & draw a black or white pixel
  float colorDiff = distance(color1.rgb, color2.rgb) / 3.;                    // normalize the difference between color components
  float color = (colorDiff > diffThresh) ? 1. : 0.;                           // if different enough, set to white
  if(color < curColor) color = mix(curColor, 0., falloffBW);                  // fall off if inactive
	gl_FragColor = vec4(vec3(color), 1.);
}
