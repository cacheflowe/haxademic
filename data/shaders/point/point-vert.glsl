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

uniform sampler2D displacementMap;
uniform float displaceStrength = 0.;
uniform float pointSize = 1.;
uniform float spread = 1.;
uniform float width = 256.;
uniform float height = 256.;
uniform float mixVal = 1.;

attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;


vec4 windowToClipVector(vec2 window, vec4 viewport, float clipw) {
  vec2 xypos = (window / viewport.zw) * 2.0;
  return vec4(xypos, 0.0, 0.0) * clipw;
}

void main() {
  // use point's original position as its uv value
  vec4 dv = texture2D( displacementMap, vertex.xy ); // rgba color of displacement map

  // calc index of this vertex
  float totalVerts = width * height;
  float x = vertex.x * width;
  float y = vertex.y * height;
  float vertexIndex = x + y * width;

  // copy vertex and adjust components
  vec4 vertCopy = vertex;
  float heightSpread = height * spread;
  float widthSpread = width * spread;
  vertCopy.y = -heightSpread/2. + vertCopy.y * heightSpread;
  vertCopy.x = -widthSpread/2. + vertCopy.x * widthSpread;
  vertCopy.z = vertCopy.z + displaceStrength * dv.r;

  // try a different way of displacing - spiral?
  vec4 vertSpiral = vertex;
  float pointRads = vertexIndex * 0.01;
  float pointRadius = vertexIndex * 0.01;
  vertSpiral.x = cos(pointRads) * pointRadius;
  vertSpiral.y = sin(pointRads) * pointRadius;
  vertSpiral.z = vertSpiral.z;// + displaceStrength * dv.r;

  // animate between layouts
  // float mixValOffset = mixVal + sin(vertexIndex * 0.001);
  float easedMix = smoothstep(0.1, 0.9, mixVal);
  vec4 mixedVert = mix(vertCopy, vertSpiral, easedMix);

  // custom point size
  float finalPointSize = pointSize * (1. + (easedMix * (dv.r * 4.)));

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

  // use texture-mapped color
  vertColor = dv;
}
