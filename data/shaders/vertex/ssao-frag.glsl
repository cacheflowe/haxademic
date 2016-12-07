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


// uniform float scale;
uniform sampler2D texture;

uniform float cameraNear = 1.;
uniform float cameraFar = 100.;
uniform bool onlyAO = false;
uniform vec2 size = vec2(512, 512);
uniform float aoClamp = 0.5;
uniform float lumInfluence = 0.5;
uniform sampler2D tDiffuse;
uniform sampler2D tDepth;

varying vec2 vUv;

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
  return ( noise * 2.0  - 1.0 ) * noiseAmount;
}

float unpackDepth(vec4 packedDepth){
    // See Aras Pranckevičius' post Encoding Floats to RGBA
    // http://aras-p.info/blog/2009/07/30/encoding-floats-to-rgba-the-final/
    return dot(packedDepth, vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0));
 }

float readDepth( vec2 coord ) {
  float cameraFarPlusNear = cameraFar + cameraNear;
  float cameraFarMinusNear = cameraFar - cameraNear;
  float cameraCoef = 2.0 * cameraNear;
  return cameraCoef / ( cameraFarPlusNear - unpackDepth( texture2D( tDepth, coord ) ) * cameraFarMinusNear );
}

float compareDepths( float depth1, float depth2, inout float far ) {
  float garea = 2.0;
  float diff = ( depth1 - depth2 ) * diffMult;
  if ( diff < gDisplace ) {
    garea = diffArea;
  } else {
    far = 1.0;
  }
  float dd = diff - gDisplace;
  float gauss = pow( EULER, gaussMult * dd * dd / ( garea * garea ) );
  return gauss;
}

float calcAO( float depth, float dw, float dh ) {
  float dd = radius - depth * radius;
  vec2 vv = vec2( dw, dh );
  vec2 coord1 = vUv + dd * vv;
  vec2 coord2 = vUv - dd * vv;
  float temp1 = 0.0;
  float temp2 = 0.0;
  float far = 0.0;
  temp1 = compareDepths( depth, readDepth( coord1 ), far );
  if ( far > 0 ) {
    temp2 = compareDepths( readDepth( coord2 ), depth, far );
    temp1 += ( 1.0 - temp1 ) * temp2;
  }
  return temp1;
}

void main() {
  vec2 noise = rand( vUv );
  float depth = readDepth( vUv );
  float tt = clamp( depth, aoClamp, 1.0 );
  float w = ( 1.0 / size.x )  / tt + ( noise.x * ( 1.0 - noise.x ) );
  float h = ( 1.0 / size.y ) / tt + ( noise.y * ( 1.0 - noise.y ) );
  float ao = 0.0;
  float dz = 1.0 / float( samples );
  float z = 1.0 - dz / 2.0;
  float l = 0.0;
  float radsDL = 3.14 / float(samples);
  for ( int i = 0; i <= samples; i ++ ) {
    float r = sqrt( 1.0 - z );
    float pw = cos( l ) * r;
    float ph = sin( l ) * r;
    ao += calcAO( depth, pw * w, ph * h );
    z = z - dz;
    l = l + DL;
    // l = l + radsDL;
  }
  ao /= float( samples );
  ao = 1.0 - ao;
  vec3 color = texture2D( tDiffuse, vUv ).rgb;
  vec3 lumcoeff = vec3( 0.299, 0.587, 0.114 );
  float lum = dot( color.rgb, lumcoeff );
  vec3 luminance = vec3( lum );
  vec3 final = vec3( color * mix( vec3( ao ), vec3( 1.0 ), luminance * lumInfluence ) );
  if ( onlyAO ) {
    final = vec3( mix( vec3( ao ), vec3( 1.0 ), luminance * lumInfluence ) );
  }
  gl_FragColor = vec4( final, 1.0 );
}
