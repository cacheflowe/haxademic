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

uniform sampler2D mapPositions;
uniform sampler2D mapColor;
uniform sampler2D mapRandom;
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

float luma(vec3 color) {
  return dot(color, vec3(0.299, 0.587, 0.114));
}

// default points shader function
vec4 windowToClipVector(vec2 window, vec4 viewport, float clipw) {
  vec2 xypos = (window / viewport.zw) * 2.0;
  return vec4(xypos, 0.0, 0.0) * clipw;
}

void main() {
  float vertexIndex = float(gl_VertexID);
  vec2 uvOrig = vec2(vertex.x, vertex.y);

  // get position from texture
  vec4 positionColor = texture2D(mapPositions, uvOrig); // rgba color of particles positions
  vec4 randomColor = texture2D(mapRandom, uvOrig);
  // vec4 textureColor = vec4(vertex.x, vertex.y, 0.5, 1.); // rgba color of displacement map

  // calc stage & local position
  float w = width;
  float h = height;
  float x = positionColor.x;
  float y = positionColor.y;
  float z = 0;
  
  // get uv based on particle current position
  vec2 uvCur = vec2(x / width, y / height);

  // get speed from texture
  float speed = positionColor.b;

  // get color under particle's current position
  vec4 colorMapColor = texture2D(mapColor, uvCur); 

  // custom point size - use speed to shrink point
  // speed can be > 1, so we need to divide a bit
  float speedShrink = (0.15 * speed);
  float finalPointSize = pointSize - speedShrink;
  finalPointSize *= 0.8 + randomColor.b * 0.4;

  // get z from texture
  z = luma(colorMapColor) * depth; // * depth/2.; // - textureColor.z * depth;

  // use custom vertex instead of Processing default (`vertex` uniform)
  // Processing default shader positioning:
  vec4 vertPosition = vec4(x, y, z, 1.);
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
  vertColor = vec4(1., 1., 1., 1.);
  // vertColor = vec4(1., 1. - speed/3f, 1. - speed/3f, 1.); // speed is variable & should be divided dynamically
  // vertColor = vec4(positionColor.rgb * colorMult, 1.);
  // vertColor = vec4(colorMapColor.rgb * colorMult, 1.);
  // vertColor = textureColor;
}
