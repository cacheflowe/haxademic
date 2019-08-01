/*
  Originally part of the Processing project - http://processing.org
  https://github.com/processing/processing/blob/master/core/src/processing/opengl/shaders/PointVert.glsl

  Copyright (c) 2011-13 Ben Fry and Casey Reas

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

#define PROCESSING_POINT_SHADER

uniform mat4 projection;
uniform mat4 modelview;

uniform vec4 viewport;
uniform int perspective;

attribute vec4 vertex;
attribute vec4 color;
attribute vec2 offset;

varying vec4 vertColor;

//!test passing attribute
//attribute float test_att;
//varying float test_var;




// added:
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform sampler2D positionMap;
uniform float pointSize = 1.;
uniform float width = 256.;
uniform float height = 256.;
uniform float depth = 256.;

attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;

#define PI radians(180.)
#define TWO_PI radians(360.)

// default points shader function
vec4 windowToClipVector(vec2 window, vec4 viewport, float clipw) {
  vec2 xypos = (window / viewport.zw) * 2.0;
  return vec4(xypos, 0.0, 0.0) * clipw;
}

void main() {
  float vertexIndex = float(gl_VertexID);

  // float lookupX = mod(vertexIndex, width) / width;
  // float lookupY = (vertexIndex - lookupX) / width;

  vec4 textureColor = texture2D( positionMap, vec2(vertex.x, vertex.y) ); // rgba color of displacement map
  // vec4 textureColor = vec4(vertex.x, vertex.y, 0.5, 1.); // rgba color of displacement map

  // calc index of this vertex for positioning use
  float w = width;
  float h = height;
  float x = -w / 2. + textureColor.x * w;
  float y = -h / 2. + textureColor.y * h;
  float z = depth/2. - textureColor.z * depth;
  vec4 vertPosition = vec4(x, y, z, 1.);

  // custom point size - use color to grow point
  float finalPointSize = pointSize; // * textureColor.z;

  // use custom vertex instead of Processing default (`vertex` uniform)
  // Processing default shader positioning:
  vec4 pos = modelview * vertPosition;
  vec4 clip = projection * pos;

  // Processing default shader positioning:
  if (0 < perspective) {
    // Perspective correction (points will look thiner as they move away
    // from the view position).
    gl_Position = clip + projection * vec4(offset.xy * finalPointSize, 0, 0);
  } else {
    // No perspective correction.
    vec4 offset = windowToClipVector(offset.xy * finalPointSize, viewport, clip.w);
    gl_Position = clip + offset;
  }

  // use original vertex color
  // vertColor = color;
  // or instead, use texture-mapped color :)
  float colorMult = 1.;
  vertColor = vec4(1., 1., 1., 1. - textureColor.z);
  // vertColor = vec4(textureColor.rgb * colorMult, 1.);
  // vertColor = textureColor;
}
