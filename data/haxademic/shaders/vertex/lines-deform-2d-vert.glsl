// copied from lines-deform-vert.glsl as a starting point for custom noise-based displacement
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
varying vec4 vertColorMap;
varying vec4 vertTexCoord;

uniform float weight = 10.;
uniform sampler2D displacementMap;
uniform sampler2D colorMap;
uniform float displaceAmp;
uniform float modelMaxExtent = 500.;
uniform bool flipY = false;
uniform int displaceMode = 0; // 0 = push along z, 1 = push x/y while staying on the z-plane

#define PROCESSING_LINE_SHADER
#define TWO_PI 6.28318530718

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
  vec2 uv = position.xy;
  if(flipY) uv.y = 1.0 - uv.y;
  // modelMaxExtent is the shape's max radius from origin, so divide by the full diameter to fill 0-1 UV
  vec4 texDisplace = texture2D( displacementMap, 0.5 + uv / (modelMaxExtent * 2.0) ); // rgba noise color
  vec4 texColor = texture2D( colorMap, 0.5 + uv / (modelMaxExtent * 2.0) ); // swappable visual texture
  vertColor = texColor;
  vertColorMap = texDisplace;

  // TODO: customize displacement math here
  if(displaceMode == 1) {
    // stay on the z-plane, using the r/g noise channels directly as x/y push (remapped to -1..1)
    posUpdated.x += displaceAmp * (texDisplace.r * 2.0 - 1.0);
    posUpdated.y += displaceAmp * (texDisplace.g * 2.0 - 1.0);
  } else {
    posUpdated.z = displaceAmp * texDisplace.r;
  }

  // default line rendering shader code ------------------
  vec4 clip0 = transform * posUpdated;
  vec4 clip1 = clip0 + transform * vec4(direction.xyz, 0);
  float thickness = direction.w * weight;  // weight added by @cacheflowe

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
