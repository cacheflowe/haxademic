#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertTexCoord;

uniform sampler2D sourceTexture;
uniform bool flipX = false;
uniform bool flipY = false;

void main() {
  vec2 uv = vertTexCoord.xy;
  if(flipY == true) uv.y = 1. - uv.y;
  if(flipX == true) uv.x = 1. - uv.x;
  gl_FragColor = texture2D(sourceTexture, uv);
}
