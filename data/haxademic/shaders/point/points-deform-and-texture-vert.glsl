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

uniform sampler2D colorMap;
uniform sampler2D displacementMap;
uniform float displaceAmp = 0.;
uniform float pointSize = 1.;
uniform float modelMaxExtent = 500.;
uniform int sheet = 0;
uniform int colorPointSize = 0;
uniform float maxPointSize = 4;

attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;

#define PI radians(180.)
#define TWO_PI radians(360.)

vec4 windowToClipVector(vec2 window, vec4 viewport, float clipw) {
  vec2 xypos = (window / viewport.zw) * 2.0;
  return vec4(xypos, 0.0, 0.0) * clipw;
}

void main() {
  // calc index of this vertex for positioning use
  float vertexIndex = gl_VertexID;

  // use point's original position (0-1) as its uv value
  // vec4 textureColor = texture2D( colorMap, vertTexCoord.xy ); // rgba color of displacement map
  // vec4 displacementColor = texture2D( displacementMap, vertTexCoord.xy ); // rgba color of displacement map
  
  // use centered model vertices as texture coords. 
  // for some reason, vertTexCoord is bogus and causes weird glitches in the geometry
  vec4 textureColor = texture2D( colorMap, 0.5 + vertex.xy / modelMaxExtent );              // rgba texture color
  vec4 displacementColor = texture2D( displacementMap, 0.5 + vertex.xy / modelMaxExtent );  // rgba displacement map color

  // copy original vertex (0-1) and mult components
  vec4 vertexDisplaced = (sheet == 1) ?
    vec4(vertex.x, vertex.y, displaceAmp, vertex.w) :       // modelMaxExtent is multiplied by the mix() function below
    vec4(vertex.x * displaceAmp, vertex.y * displaceAmp, vertex.z * displaceAmp, vertex.w);

  // displace amp based on displacement map
  vec4 vertexFinal = mix(vertex, vertexDisplaced, displacementColor.r);

  // custom point size - use color to grow point
  // if colorPointSize, use color texture for point size.
  // otherwise, use displacement map color for point size
  float finalPointSize = (colorPointSize == 1) ?
    pointSize * (1. + textureColor.r * maxPointSize) :
    pointSize * (1. + displacementColor.r * maxPointSize);

  // use custom vertex instead of Processing default (`vertex` uniform)
  // Processing default shader positioning:
  vec4 pos = modelview * vertexFinal;
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
  vertColor = textureColor;
}
