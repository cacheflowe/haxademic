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

uniform sampler2D colorTexture;
uniform sampler2D progressTexture;
uniform float pointSize = 1.;
uniform float width = 256.;
uniform float height = 256.;
uniform float scale = 1.;
uniform float progressDistance = 30.;
uniform float gravity = 0.;
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

float exponentialIn(float t) {
  return t == 0.0 ? t : pow(2.0, 10.0 * (t - 1.0));
}

float cubicIn(float t) {
  return t * t * t;
}

void main() {
  // each point has 21 vertices?! apparently.
  // this only works up to 1024 particles (32x32) for some reason
  float vertexIndex = float(floor(float(gl_VertexID) / 21.));

  // use vertex index to look up position in texture
  float lookupX = mod(vertexIndex, width) / width;
  float lookupY = floor(vertexIndex / height) / height;
  lookupY = 1. - lookupY;   // flip y

  vec4 textureColor = texture2D( colorTexture, vec2(lookupX, lookupY) ); // rgba color of displacement map
  vec4 particleProgress = texture2D( progressTexture, vec2(lookupX, lookupY) ); // rgba color of displacement map

  // get PShape vertex position
  vec4 vertPosition = vec4(vertex.x, vertex.y, 0., 1.);
  // offset it via progress texture
  // move. speed should be half of progress, because overall amplitude in any direction is 0.5
  float size = particleProgress.g;
  float rotation = particleProgress.b * TWO_PI;
  float distAmp = particleProgress.r;
  float progress = 1. - particleProgress.a;
  float gravityProgress = cubicIn(progress) * progressDistance * gravity;
  // float speed = 1./255.;
  // posOffset.x = posOffset.x + speed * cos(rotation);
  // posOffset.y = posOffset.y + speed * sin(rotation);

  vertPosition.x += progress * progressDistance * distAmp * cos(rotation);
  vertPosition.y += progress * progressDistance * distAmp * sin(rotation) + gravityProgress;
  // vertPosition.xy += (-0.5 + particleProgress.rg) * progressDistance;  // TODO: make this configurable. offset is from 0.5


  // custom point size - use color to grow point
  float finalPointSize = pointSize * size * particleProgress.a;

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
  float colorMult = particleProgress.a;
  vertColor = vec4(textureColor.rgb * colorMult, particleProgress.a);
  // vertColor = vec4(1., 1., 1., 0.4);
}
