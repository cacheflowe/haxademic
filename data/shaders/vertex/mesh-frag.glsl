#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D textureMap;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  // use alternate texture passed in as uniform
  vec4 tiledTextureColor = texture2D(textureMap, fract(vertTexCoord.xy * 10.));
  gl_FragColor = tiledTextureColor * vertTexCoord.y;
  // use original texture assigner to PShape:
  // gl_FragColor = texture2D(texture, vertTexCoord.st) * vertColor;
}
