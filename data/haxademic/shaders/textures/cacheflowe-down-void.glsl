// built for the Ello gif contest:
// https://ello.co/medialivexello/post/gif-exhibition

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform float time;

#define PI 3.141592653589793
#define TAU 6.283185307179586

// from iq / bookofshaders
float cubicPulse( float c, float w, float x ) {
  x = abs(x - c);
  if( x>w ) return 0.0;
  x /= w;
  return 1.0 - x*x*(3.0-2.0*x);
}

void main(void) {
  float timeAdjusted = time * 0.75;

  //////////////////////////////////////////////////////
  // Create tunnel coordinates (p) and remap to normal coordinates (uv)
  // Technique from @iq: https://www.shadertoy.com/view/Ms2SWW
  // and a derivative:   https://www.shadertoy.com/view/Xd2SWD
  vec2 p = vertTexCoord.xy - 0.5;
  p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
  vec2 uvOrig = p;
  // added twist by me ------------
  float rotZ = 1. - 0.23 * sin(1. * cos(length(p * 1.5)));
  p *= mat2(cos(rotZ), sin(rotZ), -sin(rotZ), cos(rotZ));
  //-------------------------------
  float a = atan(p.y,p.x);												// angle of each pixel to the center of the screen
  float rSquare = pow( pow(p.x*p.x,4.0) + pow(p.y*p.y,4.0), 1.0/8.0 );	// modified distance metric (http://en.wikipedia.org/wiki/Minkowski_distance)
  float rRound = length(p);
  float r = mix(rSquare, rRound, 0.5 + 0.5 * sin(timeAdjusted * 2.)); 			// interp between round & rect tunnels
  vec2 uv = vec2( 0.3/r + timeAdjusted, a/3.1415927 );							// index texture by (animated inverse) radious and angle
  //////////////////////////////////////////////////////

  // subdivide to grid
  uv += vec2(0., 0.25 * sin(timeAdjusted + uv.x * 1.));			// pre-warp
  uv /= vec2(1. + 0.0002 * length(uvOrig));
  vec2 uvDraw = fract(uv * 8.);							// create grid

  // draw lines
  float col = cubicPulse(0.5, 0.14, uvDraw.x);
  col = max(col, cubicPulse(0.5, 0.08, uvDraw.y));

  // darker towards center, light towards outer
  col = col * r * 2.1;
  col += 0.15 * length(uvOrig);
  gl_FragColor = vec4(vec3(0., col, 0.), 1.);
}
