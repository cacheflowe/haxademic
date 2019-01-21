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

void main() {
  vec4 color1 = texture2D(tex1, vertTexCoord.xy);
  vec4 color2 = texture2D(tex2, vertTexCoord.xy);
  float colorDistance = distance(color1.rgb, color2.rgb) / 3.;
  if(color1.a + color2.a < 1.5) {
    gl_FragColor = vec4(vec3(0., 0., 0.), 1.);
  } else {
    gl_FragColor = vec4(vec3(colorDistance), 1.);
  }
}
