// adapted from https://github.com/pjkarlik/TruchetTiles/blob/master/src/shader/truchet/fragmentShader.js
// then ported from https://www.interactiveshaderformat.com/sketches/2845#
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time;

#define PI 3.14159265358979323846264

uniform bool lineMode = false;
uniform float tileSize = 0.1;
uniform float noiseSeed = 0.3499;
uniform float rotation = 0.;

vec2 hash2(vec2 p) {
  vec2 o = (p+0.5)/256.0;
  return o;
}

float goldNoise(vec2 coord, float seed){
  float phi = 1.61803398874989484820459 * 00000.1;
  float pi2 = PI * 00000.1;
  float sq2 = 1.41421356237309504880169 * 10000.0;
  float temp = fract(
    sin(
      dot(
        coord*(seed+phi), vec2(phi, pi2)
      )
    ) * sq2
  );
  return temp;
}

vec3 pattern(vec2 uv) {
  vec2 grid = floor(uv);
  vec2 subuv = fract(uv);
  float mult = 0.5;
  float dnoise = goldNoise(grid, noiseSeed);
  vec2 rand = hash2(grid);
  float shade = 0.;
  float df;
  float check = dnoise;
  if( check <= .25 ) {
    df = subuv.x - subuv.y; // tl
  } else if( check <= .5 ) {
    df = 1. - subuv.y - subuv.x;
  } else if( check <= .75 ) {
    df = subuv.y - subuv.x;
  } else if( check <= 1. ) {
    df = subuv.y - 1. + subuv.x;
  }
  shade = smoothstep(.0, -.02, df);
  if (lineMode)
	shade += smoothstep(.02, .04, df);
  return vec3( shade );
}

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
    return uv;
}

void main (void) {
	vec2 uv = vertTexCoord.xy - vec2(0.5, 0.5);
	uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	uv *= 1.0 / tileSize;
  uv = rotateCoord(uv, rotation);
	vec3 color = pattern(uv);
	gl_FragColor = vec4(color, 1.0);

}
