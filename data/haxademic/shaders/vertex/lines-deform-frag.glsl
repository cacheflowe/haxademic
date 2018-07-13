#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform float weight = 10.;
uniform int fadesAlpha = 0;

varying vec2 center;
varying vec2 normal;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  vec2 v = gl_FragCoord.xy - center;
  float alpha = (fadesAlpha == 1) ?
    1.0 - abs(2.0 * dot(normalize(normal), v) / weight) :
    vertColor.a;
  gl_FragColor = vec4(vertColor.rgb, alpha);
}

/*

void main() {
  gl_FragColor = texture2D(texture, vertTexCoord.st) * vertColor;
}
*/
