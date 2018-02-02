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
uniform sampler2D colorMap;

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
  // use point's original position (0-1) as its uv value
  vec4 textureColor = texture2D( positionMap, vertex.xy ); // rgba color of displacement map
  vec4 color = texture2D( colorMap, vertex.xy ); // rgba color of displacement map

  // calc index of this vertex for positioning use
  float totalVerts = width * height;
  float x = textureColor.r * width;
  float y = textureColor.g * height;
  float vertexIndex = x + y * width;
  vertexIndex = gl_VertexID;

  // custom vertex manipulation?
  vec4 mixedVert = vertex;
  mixedVert.x = x + color.g; // use color components to shift slightly to obscure grid
  mixedVert.y = y + color.b;
  mixedVert.z = (color.r - 0.5) * width * 2.; // (color.r - 0.5) * width * 0.75;

  // adjust to reduce pixelation
  mixedVert += vec4(color.r * 10., color.g * 10., 0., 0.);

  // custom point size - use color to grow point
  float finalPointSize = pointSize;

  // use custom vertex instead of Processing default (`vertex` uniform)
  // Processing default shader positioning:
  vec4 pos = modelview * mixedVert;
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
  float colorMult = 3.;
  vertColor = vec4(color.rgb * colorMult, 0.5);
  // vertColor = vec4(1., 1., 1., 0.4);
}
