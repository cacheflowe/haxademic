/**
 * @author alteredq / http://alteredqualia.com/
 * Adapted to Processing by @cacheflowe
 *
 * Screen-space ambient occlusion shader
 * - ported from
 *   SSAO GLSL shader v1.2
 *   assembled by Martins Upitis (martinsh) (http://devlog-martinsh.blogspot.com)
 *   original technique is made by ArKano22 (http://www.gamedev.net/topic/550699-ssao-no-halo-artifacts/)
 * - modifications
 * - modified to use RGBA packed depth texture (use clear color 1,1,1,1 for depth pass)
 * - refactoring and optimizations
 */

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER


varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform sampler2D texture;

uniform float cameraNear = 1.;
uniform float cameraFar = 100.;
uniform bool onlyAO = false;
uniform vec2 size = vec2(512, 512);
uniform float aoClamp = 0.5;
uniform float lumInfluence = 0.5;
uniform sampler2D tDiffuse;
uniform sampler2D tDepth;

#define DL 2.399963229728653
#define EULER 2.718281828459045

uniform int samples = 8;
uniform float radius = 10.0;
uniform bool useNoise = true;
uniform float noiseAmount = 0.00003;
uniform float diffArea = 0.5;
uniform float gDisplace = 0.5;
uniform float diffMult = 100.0;
uniform float gaussMult = -2.0;

// RGBA depth

// #include <packing>
// replaced THREE.js call with unpackDepth()

vec2 rand( const vec2 coord ) {
  vec2 noise;
  if ( useNoise ) {
    float nx = dot ( coord, vec2( 12.9898, 78.233 ) );
    float ny = dot ( coord, vec2( 12.9898, 78.233 ) * 2.0 );
    noise = clamp( fract ( 43758.5453 * sin( vec2( nx, ny ) ) ), 0.0, 1.0 );
  } else {
    float ff = fract( 1.0 - coord.s * ( size.x / 2.0 ) );
    float gg = fract( coord.t * ( size.y / 2.0 ) );
    noise = vec2( 0.25, 0.75 ) * vec2( ff ) + vec2( 0.75, 0.25 ) * gg;
  }
  return ( noise * 2.0  - 1.0 ) * noiseAmount * 1.;
}

// SSAO (Screen Space AO) - by moranzcw - 2021
// Email: moranzcw@gmail.com
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#define PI 3.14159265359

// --------------------------------------
// oldschool rand() from Visual Studio
// --------------------------------------
int seed = 1;
void srand(int s) {
  seed = s;
}
int rand(void) {
  seed = seed * 0x343fd + 0x269ec3;
  return (seed >> 16) & 32767;
}
float frand(void) {
  return float(rand()) / 32767.0;
}
// --------------------------------------
// hash by Hugo Elias
// --------------------------------------
int hash(int n) {
  n = (n << 13) ^ n;
  return n * (n * n * 15731 + 789221) + 1376312589;
}

// SSAO (Screen Space AO) - by moranzcw - 2021
// Email: moranzcw@gmail.com
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.


vec3 sphereVolumeRandPoint() {
  vec3 p = vec3(frand(), frand(), frand()) * 2.0 - 1.0;
  while(length(p) > 1.0) {
    p = vec3(frand(), frand(), frand()) * 2.0 - 1.0;
  }
  return p;
}

float depth(vec2 coord) {
  return texture2D(tDepth, coord).r;
}

float SSAO(vec2 coord)
{
  float cd = depth(coord);
  float screenRadius = 0.5 * (radius / cd) / 0.53135;
  float li = 0.0;
  float count = 0.0;
  for(float i=0.0; i<samples; i++)
  {
      vec3 p = sphereVolumeRandPoint() * frand();
      vec2 sp = vec2(coord.x + p.x * screenRadius, coord.y + p.y * screenRadius);
      float d = depth(sp);
      float at = pow(length(p)-1.0, 2.0);
      li += step(cd + p.z * radius, d) * at;
      if(at < 0.7) {
        count += at;
      }
  }
  return li / count;
}

void main() {
  vec2 uv = vertTexCoord.xy;
  vec2 noise = rand( uv );
  uv += noise;
  float d = depth( uv );
  vec3 color = texture2D( tDiffuse, uv ).rgb;

  vec3 ssaoResult = vec3(SSAO(uv));
  // vec3 ssaoResult = vec3(0.4) + step(d, 1e5 - 1.0) * vec3(0.8) * SSAO(uv);


  vec3 lumcoeff = vec3(0.299, 0.587, 0.114);
  float lum = dot(color.rgb, lumcoeff);
  vec3 luminance = vec3(lum);
  vec3 final = vec3(color * mix(vec3(ssaoResult), vec3(1.0), luminance * lumInfluence));
  if(onlyAO) {
    final = vec3(mix(vec3(ssaoResult), vec3(1.0), luminance * lumInfluence));
  }
  gl_FragColor = vec4(final, 1.0);


  if ( onlyAO ) {
    gl_FragColor = vec4(ssaoResult, 1.0);
  } else {
    // final = vec3( mix( vec3( ssaoResult ), vec3( 1.0 ), luminance * lumInfluence ) );

    gl_FragColor = vec4( color.rgb + ssaoResult, 1.0 );
  }

  // debug
  // gl_FragColor = vec4( vec3(d), 1.0 );
  // gl_FragColor = texture2D(tDepth, uv);
}
