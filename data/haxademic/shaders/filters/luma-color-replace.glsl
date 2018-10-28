
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec4 targetColor = vec4(1., 70./255., 6./255., 1.);
uniform float diffRange = 0.1;
uniform float lumaTarget = 0.5;

float luma(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
  vec2 uv = vertTexCoord.xy;
  vec4 color = texture2D(texture, uv);
  float grayVal = luma(color);
  vec4 grayColor = vec4(vec3(grayVal), 1.);

  float lumaDiff = distance(lumaTarget, grayVal);
  if(lumaDiff > diffRange) lumaDiff = 1.;

  float crossfade = lumaDiff * 1. / diffRange;	// normalize from 0-diffRange
  crossfade = smoothstep(0.4, 0.6, 1. - crossfade);
  gl_FragColor = mix(grayColor, targetColor, crossfade);
}
