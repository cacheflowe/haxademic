// from: http://transitions.glsl.io/transition/2a5fa2f77c883dd661f9

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D from;
uniform sampler2D to;
uniform float progress;

uniform float interpolationPower = 5.;

void main() {
  // vec2 p = gl_FragCoord.xy / resolution.xy;
  vec2 p = vertTexCoord.xy;
  // p = vec2(p.x, 1.0-p.y);	// flipV while processing has issues with PImage
  vec4 fTex = texture2D(from,p);
  vec4 tTex = texture2D(to,p);
  gl_FragColor = mix(distance(fTex,tTex)>progress?fTex:tTex,
                     tTex,
                     pow(progress,interpolationPower));
}