/*
  Part of the Processing project - http://processing.org

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
  float totalVerts = 256. * 256.;
  float x = vertex.x * spread;
  float y = vertex.y * spread;
  float vertexIndex = x + y * 256.;

  // copy vertex and adjust components
  vec4 vertCopy = vertex;
  vertCopy.y = vertCopy.y * spread;
  vertCopy.x = vertCopy.x * spread;
  vertCopy.z = vertCopy.z + displaceStrength * dv.r;

  // try a different way of displacing - spiral?
  vec4 vertSpiral = vertex;
  vertSpiral.x = cos(vertexIndex * 0.01) * vertexIndex * 0.01;
  vertSpiral.y = sin(vertexIndex * 0.01) * vertexIndex * 0.01;

  // animate between layouts
  vec4 mixedVert = mix(vertCopy, vertSpiral, mixVal);

  // use custom vertex instead of original
  vec4 pos = modelview * mixedVert;
  // pos.x = pos.x * 256.;
  // pos.y = pos.y * 256.;
  // pos.x += sin(pos.y);
  // pos.z = sin(pos.x) * 10.;
  vec4 clip = projection * pos;

  if (0 < perspective) {
    // Perspective correction (points will look thiner as they move away
    // from the view position).
    gl_Position = clip + projection * vec4(offset.xy * pointSize, 0, 0);
  } else {
    // No perspective correction.
    vec4 offset = windowToClipVector(offset.xy * pointSize, viewport, clip.w);
    gl_Position = clip + offset;
  }


  vertColor = color;
  vertColor = dv;
  // vertColor = vec4(sin(vertexIndex * 0.07));
  // vertColor = vec4(vec3(sin(pos.x), sin(pos.y), cos(pos.x)), 1.);
  // vertColor = vec4(vec3(offset.x + offset.y), 1.);
  // vertColor = vec4(vec3(1.), 1.);

}
