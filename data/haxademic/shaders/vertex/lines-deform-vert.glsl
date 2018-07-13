// started with basic LINES shader: https://processing.org/tutorials/pshader/

uniform mat4 transform;
uniform vec4 viewport;
uniform vec4 texMatrix;

attribute vec4 vertex;
attribute vec4 position;
attribute vec4 color;
attribute vec4 direction;
attribute vec2 texCoord;

varying vec2 center;
varying vec2 normal;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float weight = 10.;
uniform sampler2D colorMap;
uniform sampler2D displacementMap;
uniform float displaceAmp;
uniform float modelMaxExtent = 500.;
uniform int colorThickness = 0;
uniform int sheet = 0;

#define PROCESSING_LINE_SHADER

vec3 clipToWindow(vec4 clip, vec4 viewport) {
  vec3 dclip = clip.xyz / clip.w;
  vec2 xypos = (dclip.xy + vec2(1.0, 1.0)) * 0.5 * viewport.zw;
  return vec3(xypos, dclip.z * 0.5 + 0.5);
}

void main() {
  // copy incoming vertex position and alter/use the copy
  vec4 posUpdated = position;

  // get/pass colors
  vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
  vec4 texDisplace = texture2D( displacementMap, 0.5 + position.xy / modelMaxExtent ); // rgba color
  vec4 texColor = texture2D( colorMap, 0.5 + position.xy / modelMaxExtent ); // displacement color
  // texColor = vec4(0., 1., 0., 1.);
  vertColor = texColor;

  // displace
  if(sheet == 1) {
    posUpdated.z = displaceAmp * texDisplace.r;
  } else {
    posUpdated.x *= 1. + displaceAmp * texDisplace.r;
    posUpdated.y *= 1. + displaceAmp * texDisplace.r;
    posUpdated.z *= 1. + displaceAmp * texDisplace.r;
  }

  // default line rendering shader code ------------------
  vec4 clip0 = transform * posUpdated;
  vec4 clip1 = clip0 + transform * vec4(direction.xyz, 0);
  float thickness = direction.w * weight;  // weight added by @cacheflowe
  if(colorThickness == 1) thickness *= texColor.r;

  // clip0.z = 10.;
  // clip1.z = 500.;
  vec3 win0 = clipToWindow(clip0, viewport);
  vec3 win1 = clipToWindow(clip1, viewport);
  vec2 tangent = win1.xy - win0.xy;

  normal = normalize(vec2(-tangent.y, tangent.x));
  vec2 offset = normal * thickness;
  gl_Position.xy = clip0.xy + offset.xy;
  gl_Position.zw = clip0.zw;

  // pass along vertex position
  center = (win0.xy + win1.xy) / 2.0;
  // end default line rendering shader code ------------------
}
