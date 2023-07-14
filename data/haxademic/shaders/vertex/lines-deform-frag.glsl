#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform float weight = 10.;
uniform int fadesAlpha = 0;
uniform bool flipY = false;
uniform bool bgRemove = false;

varying vec2 center;
varying vec2 normal;
varying vec4 vertColor;
varying vec4 vertColorMap;
varying vec4 vertTexCoord;

void main() {
  vec2 uv = gl_FragCoord.xy;
  if(flipY) uv.y = 1.0 - uv.y;
  vec2 v = uv - center;
  float alpha = (fadesAlpha == 1) ?
    1.0 - abs(2.0 * dot(normalize(normal), v) / weight) :
    vertColor.a;
  if(bgRemove) alpha = min(alpha, vertColorMap.r);
  // vec3 greenCol = vertColor.rgr * vec3(0.4, 1.2, 0.4);
  gl_FragColor = vec4(vertColor.rgb, alpha);
}

/*
void main() {
  gl_FragColor = texture2D(texture, vertTexCoord.st) * vertColor;
}
*/
