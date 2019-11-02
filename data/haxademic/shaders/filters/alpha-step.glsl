// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float alphaStep = -1./255.;

void main() {
  gl_FragColor = texture2D(texture, vertTexCoord.xy) + vec4(0., 0., 0., alphaStep);
}
