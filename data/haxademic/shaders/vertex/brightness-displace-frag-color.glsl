#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  gl_FragColor = vec4(2.0 * abs(sin(vertTexCoord.x * 2.0)), 2.0 * abs(sin(vertTexCoord.y * 2.0)), 2.0 * abs(sin(vertTexCoord.x * 2.0 - vertTexCoord.y * 2.0)), 1.0);
}

