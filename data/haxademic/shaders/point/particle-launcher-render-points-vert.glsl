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

attribute int vertIndex;

// added:
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform sampler2D colorMap;
uniform sampler2D positionMap;
uniform float pointSize = 1.;
uniform float width = 256.;
uniform float height = 256.;
uniform float depth = 256.;
uniform float scale = 1.;
// uniform float progressDistance = 30.;
// uniform float gravity = 0.;
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

// CURL START ///////////////////////////////////////////////////////////////
vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 mod289(vec4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 permute(vec4 x) { return mod289(((x*34.0)+1.0)*x); }
vec4 taylorInvSqrt(vec4 r) { return 1.79284291400159 - 0.85373472095314 * r; }
float snoise(vec3 v) { const vec2 C = vec2(1.0/6.0, 1.0/3.0) ; const vec4 D = vec4(0.0, 0.5, 1.0, 2.0); vec3 i = floor(v + dot(v, C.yyy) ); vec3 x0 = v - i + dot(i, C.xxx) ; vec3 g = step(x0.yzx, x0.xyz); vec3 l = 1.0 - g; vec3 i1 = min( g.xyz, l.zxy ); vec3 i2 = max( g.xyz, l.zxy ); vec3 x1 = x0 - i1 + C.xxx; vec3 x2 = x0 - i2 + C.yyy; vec3 x3 = x0 - D.yyy; i = mod289(i); vec4 p = permute( permute( permute( i.z + vec4(0.0, i1.z, i2.z, 1.0 )) + i.y + vec4(0.0, i1.y, i2.y, 1.0 )) + i.x + vec4(0.0, i1.x, i2.x, 1.0 )); float n_ = 0.142857142857; vec3 ns = n_ * D.wyz - D.xzx;vec4 j = p - 49.0 * floor(p * ns.z * ns.z); vec4 x_ = floor(j * ns.z); vec4 y_ = floor(j - 7.0 * x_ ); vec4 x = x_ *ns.x + ns.yyyy; vec4 y = y_ *ns.x + ns.yyyy; vec4 h = 1.0 - abs(x) - abs(y);vec4 b0 = vec4( x.xy, y.xy ); vec4 b1 = vec4( x.zw, y.zw );vec4 s0 = floor(b0)*2.0 + 1.0; vec4 s1 = floor(b1)*2.0 + 1.0; vec4 sh = -step(h, vec4(0.0));vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ; vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ; vec3 p0 = vec3(a0.xy,h.x); vec3 p1 = vec3(a0.zw,h.y); vec3 p2 = vec3(a1.xy,h.z); vec3 p3 = vec3(a1.zw,h.w);  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3))); p0 *= norm.x; p1 *= norm.y; p2 *= norm.z; p3 *= norm.w; vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0); m = m * m; return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1), dot(p2,x2), dot(p3,x3) ) ); }
vec3 snoiseVec3( vec3 x ){float s = snoise(vec3( x )); float s1 = snoise(vec3( x.y - 19.1 , x.z + 33.4 , x.x + 47.2 )); float s2 = snoise(vec3( x.z + 74.2 , x.x - 124.5 , x.y + 99.4 )); vec3 c = vec3( s , s1 , s2 ); return c;}
vec3 curlNoise( vec3 p ) { const float e = .1; vec3 dx = vec3( e , 0.0 , 0.0 ); vec3 dy = vec3( 0.0 , e , 0.0 ); vec3 dz = vec3( 0.0 , 0.0 , e ); vec3 p_x0 = snoiseVec3( p - dx ); vec3 p_x1 = snoiseVec3( p + dx ); vec3 p_y0 = snoiseVec3( p - dy ); vec3 p_y1 = snoiseVec3( p + dy ); vec3 p_z0 = snoiseVec3( p - dz ); vec3 p_z1 = snoiseVec3( p + dz ); float x = p_y1.z - p_y0.z - p_z1.y + p_z0.y; float y = p_z1.x - p_z0.x - p_x1.z + p_x0.z; float z = p_x1.y - p_x0.y - p_y1.x + p_y0.x; const float divisor = 1.0 / ( 2.0 * e ); return normalize( vec3( x , y , z ) * divisor ); }
// CURL END /////////////////////////////////////////////////////////////////

void main() {
  // GET POSITION FROM POSITION TEXTURE'S PIXELS ----------------------
  // calc index of this vertex for positioning use
  vec4 particlePosition = texture2D( positionMap, vec2(vertex.x, vertex.y) ); // rgba color of displacement map
  float w = width;
  float h = height;
  float x = -w / 2. + particlePosition.x * w;
  float y = -h / 2. + particlePosition.y * h;
  float z = 0; // -depth * (1. - particlePosition.a);
  // z = 0. - (1. - particlePosition.a) * 500.;  // move into distance with progress
  float progress = 1. - particlePosition.a;
  vec4 vertPosition = vec4(x, y, z, 1.) * 2.;

  // get particle color from extra color map
  vec4 textureColor = texture2D(colorMap, vec2(vertex.x, vertex.y));

  // offset position with curl noise?
  float curlCohesion = 30.;  // larger numbers bring particles cohesion closer
  float curlZoom = 300.;
  float curlAmp = 200. * progress;  // multiply by progress to spread furtherover time
  vec3 curlInputOffset = textureColor.rgb / curlCohesion;  // use particle color to slightly offset its input into the curl to give a little randomness
  vec3 curlVertInput = vertPosition.xyz;
  curlVertInput.z = 0.;  // things get too curly if we're moving z and also using that for the curl input
  curlVertInput /= curlZoom;
  vec3 curlInput = curlVertInput + curlInputOffset;
  vec3 curlResult = curlNoise(curlInput);
  vertPosition.xy += curlResult.xy * curlAmp; // only curl .xy for now. z fades off into the distance
  vertPosition.z += 0.; // curlResult.z * curlAmp * 10.; // only curl .xy for now. z fades off into the distance
  vertPosition.z += curlResult.z * curlAmp * 1.; // only curl .xy for now. z fades off into the distance

  // custom point size - use color to grow point
  float finalPointSize = pointSize * (1. - progress);

  // SET POSITION ----------------------
  // use custom vertex instead of Processing default (`vertex` uniform)
  vec4 pos = modelview * vertPosition;
  vec4 clip = projection * pos;
  // Processing default shader positioning:
  if (0 < perspective) {
    // Perspective correction (points will look thiner as they move away from the view position).
    gl_Position = clip + projection * vec4(offset.xy * finalPointSize, 0, 0);
  } else {
    // No perspective correction.
    vec4 offset = windowToClipVector(offset.xy * finalPointSize, viewport, clip.w);
    gl_Position = clip + offset;
  }

  // SET COLOR ----------------------
  // use original vertex color
  // vertColor = color;
  // or instead, use texture-mapped color :)
  vertColor = vec4(textureColor.rgb, particlePosition.a);
  vertColor = textureColor.rgba;
  // vertColor = vec4(1., 1., 1., 0.4);
}
