/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-21 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation, version 2.1.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

#define PROCESSING_LINE_SHADER

uniform mat4 modelviewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 viewport;
uniform int perspective;
uniform vec3 scale;

attribute vec4 position;
attribute vec4 color;
attribute vec4 direction;

varying vec4 vertColor;
  
void main() {
  vec4 posp = modelviewMatrix * position;
  vec4 posq = modelviewMatrix * (position + vec4(direction.xyz, 0));

  // Moving vertices slightly toward the camera
  // to avoid depth-fighting with the fill triangles.
  // Discussed here:
  // http://www.opengl.org/discussion_boards/ubbthreads.php?ubb=showflat&Number=252848  
  posp.xyz = posp.xyz * scale;
  posq.xyz = posq.xyz * scale;

  vec4 p = projectionMatrix * posp;
  vec4 q = projectionMatrix * posq;

  // formula to convert from clip space (range -1..1) to screen space (range 0..[width or height])
  // screen_p = (p.xy/p.w + <1,1>) * 0.5 * viewport.zw

  // prevent division by W by transforming the tangent formula (div by 0 causes
  // the line to disappear, see https://github.com/processing/processing/issues/5183)
  // t = screen_q - screen_p
  //
  // tangent is normalized and we don't care which direction it points to (+-)
  // t = +- normalize( screen_q - screen_p )
  // t = +- normalize( (q.xy/q.w+<1,1>)*0.5*viewport.zw - (p.xy/p.w+<1,1>)*0.5*viewport.zw )
  //
  // extract common factor, <1,1> - <1,1> cancels out
  // t = +- normalize( (q.xy/q.w - p.xy/p.w) * 0.5 * viewport.zw )
  //
  // convert to common divisor
  // t = +- normalize( ((q.xy*p.w - p.xy*q.w) / (p.w*q.w)) * 0.5 * viewport.zw )
  //
  // remove the common scalar divisor/factor, not needed due to normalize and +-
  // (keep viewport - can't remove because it has different components for x and y
  //  and corrects for aspect ratio, see https://github.com/processing/processing/issues/5181)
  // t = +- normalize( (q.xy*p.w - p.xy*q.w) * viewport.zw )

  vec2 tangent = (q.xy*p.w - p.xy*q.w) * viewport.zw;

  // don't normalize zero vector (line join triangles and lines perpendicular to the eye plane)
  tangent = length(tangent) == 0.0 ? vec2(0.0, 0.0) : normalize(tangent);

  // flip tangent to normal (it's already normalized)
  vec2 normal = vec2(-tangent.y, tangent.x);

  float thickness = direction.w;
  vec2 offset = normal * thickness;

  // Perspective ---
  // convert from world to clip by multiplying with projection scaling factor
  // to get the right thickness (see https://github.com/processing/processing/issues/5182)
  // invert Y, projections in Processing invert Y
  vec2 perspScale = (projectionMatrix * vec4(1, -1, 0, 0)).xy;

  // No Perspective ---
  // multiply by W (to cancel out division by W later in the pipeline) and
  // convert from screen to clip (derived from clip to screen above)
  vec2 noPerspScale = p.w / (0.5 * viewport.zw);

  gl_Position.xy = p.xy + offset.xy * mix(noPerspScale, perspScale, float(perspective > 0));
  gl_Position.zw = p.zw;

  vertColor = color;
}
