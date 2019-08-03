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


// added:
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform sampler2D positionMap;
uniform float pointSize = 1.;
uniform float width = 256.;
uniform float height = 256.;
uniform float scale = 1.;
// uniform float vertIndexDivisor = 1.;
uniform float mode = 0.;

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
  // each point has 21 vertices?! apparently.
  // this only works up to 1024 particles (32x32) for some reason
  // float vertexIndex = float(floor(float(gl_VertexID) / 21.));
  float vertexIndex = float(gl_VertexID);
  // float vertexIndex = float(floor(float(gl_VertexID) / vertIndexDivisor));

  // use vertex index to look up position in texture
  float lookupX = mod(vertexIndex, width) / width;
  float lookupY = floor(vertexIndex / height) / height;

  vec4 textureColor = texture2D( positionMap, vec2(vertex.x, vertex.y) ); // rgba color of displacement map

  // use vertex color for positioning use - here we're putting points in a cube
  float w = width * scale;
  float h = height * scale;
  float x = -w / 2. + textureColor.x * w;
  float y = -h / 2. + textureColor.y * h;
  float z = -h / 2. + textureColor.z * h;
  vec4 vertPosition = vec4(x, y, z, 1.);

  // custom point size - use color to grow point
  float finalPointSize = pointSize;

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
  vertColor = vec4(textureColor.rgb * colorMult, 1.);
  // vertColor = vec4(1., 1., 1., 0.4);
}
