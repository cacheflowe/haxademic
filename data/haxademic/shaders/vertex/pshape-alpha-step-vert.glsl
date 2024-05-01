// ported from default ColorVert.glsl

uniform mat4 transformMatrix;

attribute vec4 position;
attribute vec4 color;

varying vec4 vertColor;
varying float vAlphaStep;
uniform float alphaStep = 1.;

void main() {
  gl_Position = transformMatrix * position;
  vAlphaStep = alphaStep;
  vertColor = color;
}
