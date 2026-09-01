// copied from lines-deform-frag.glsl as a starting point for custom noise-based displacement
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform float weight = 10.;
uniform bool fadesAlpha = false;
uniform bool flipY = false;

varying vec2 center;
varying vec2 normal;
varying vec4 vertColor;
varying vec4 vertColorMap;
varying vec4 vertTexCoord;

void main() {
  vec2 uv = gl_FragCoord.xy;
  if(flipY) uv.y = 1.0 - uv.y;
  vec2 v = uv - center;
  float alpha = (fadesAlpha) ?
    1.0 - abs(2.0 * dot(normalize(normal), v) / weight) :
    vertColor.a;
  gl_FragColor = vec4(vertColor.rgb, alpha);
}
