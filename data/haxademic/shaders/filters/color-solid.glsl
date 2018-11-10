// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec4 targetColor = vec4(1.0);
uniform float crossfade = 1.0;

void main() {
  vec4 origColor = texture2D(texture, vertTexCoord.xy);
  gl_FragColor = mix(origColor, targetColor, crossfade);
}
