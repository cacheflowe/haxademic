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

uniform float r = 1.0;
uniform float g = 1.0;
uniform float b = 1.0;
uniform float a = 1.0;
uniform float crossfade = 1.0;

void main() {
  vec4 color = texture2D(texture, vertTexCoord.xy);
  gl_FragColor = mix(color, vec4(r, g, b, a), crossfade);
}
