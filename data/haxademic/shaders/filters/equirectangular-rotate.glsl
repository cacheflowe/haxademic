#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float rotXAxis = 0.;
uniform float rotYAxis = 0.;

// from: https://www.shadertoy.com/view/4dBBWV

#define PI 3.1415926535897932384626433832795

float map(float v, float low1, float high1, float low2, float high2) {
  return (v-low1)/(high1-low1)*(high2-low2);
}

vec2 xyzToLonLat(vec3 v) {
  vec3 p = normalize(v);
  float lat = map(asin(p.y), PI*0.5, -PI*0.5, 0.0, 1.0);
  float lon = map(atan(p.x, -p.z), PI, -PI, 0.0, 1.0);
  return vec2(lon, lat);
}

vec3 lonLatToXYZ(vec2 lonLat) {
  float lon = map(lonLat.x, 0.0, 1.0, -PI, PI);
  float lat = map(lonLat.y, 0.0, 1.0, -PI*0.5, PI*0.5);
  float x = sin(lat)*sin(lon);
  float y = cos(lat);
  float z = sin(lat)*cos(lon);
  return vec3(x,y,z);
}

vec3 xRot(vec3 v, float theta) {
  float x = v.x;
  float y = v.y*cos(theta) - v.z*sin(theta);
  float z = v.y*sin(theta) + v.z*cos(theta);
  return vec3(x,y,z);
}

vec3 yRot(vec3 v, float theta) {
  float x = v.z*sin(theta) + v.x*cos(theta);
  float y = v.y;
  float z = v.z*cos(theta) - v.x*sin(theta);
  return vec3(x,y,z);
}

vec3 zRot(vec3 v, float theta) {
  float x = v.x*cos(theta) - v.y*sin(theta);
  float y = v.x*sin(theta) + v.y*cos(theta);
  float z = v.z;
  return vec3(x,y,z);
}

vec2 equiRemap(vec2 lonLat, vec2 delta) {
  vec3 v = lonLatToXYZ(lonLat);
  v = yRot(v,delta.x);
  v = xRot(v,delta.y);
  return xyzToLonLat(v);
}

void main() {
  vec2 uv = equiRemap(vertTexCoord.xy, vec2(rotYAxis, rotXAxis));
  gl_FragColor = texture2D(texture, uv);
}

