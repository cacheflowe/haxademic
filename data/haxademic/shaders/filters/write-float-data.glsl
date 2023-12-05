#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertTexCoord;

uniform float[512] data;

void main() {
  vec2 uv = vertTexCoord.xy;
  vec2 texSize = textureSize(texture, 0);
  int arrLength = data.length();
  int dataIndex = int(floor(uv.x * float(arrLength)));
  float curData = data[dataIndex];
  gl_FragColor = vec4(curData, curData, curData, 1.0);
}
