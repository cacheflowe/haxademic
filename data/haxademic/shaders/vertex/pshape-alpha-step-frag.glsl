// ported from default ColorFrag.glsl

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying vec4 vertColor;
varying float vAlphaStep;

void main() {
  vec4 finalColor = vertColor;
  finalColor.a = finalColor.a + vAlphaStep;
  finalColor.a = clamp(finalColor.a, 0.0, 1.0);
  gl_FragColor = finalColor;
}