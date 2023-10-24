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

uniform int samples = 8;
uniform bool onlyAO = false;
uniform vec2 size = vec2(512, 512);
uniform float aoClamp = 0.5;
uniform float lumInfluence = 0.5;
uniform sampler2D tDiffuse;
uniform sampler2D tDepth;

#define DL 2.399963229728653
#define EULER 2.718281828459045

float dGamma = 152.;//1:10:2
float oscSpeed = 3.;//0:10:5

float radAttenuation = 5.;//0:2:1
uniform float radius = 0.05;//0:0.6:0.024
float spiral = 16524.56;//1:100:50
float spinSpeed = .10;
int time = 0;

float depth(vec2 coord) {
  return texture2D(tDepth, coord).r;
}

void main() {
  vec2 uv = vertTexCoord.xy;
  uv *= 1. + 0.1 * sin(time / 3.5);
  uv += vec2(time / 15., sin(time) / 15.);
  float dp = depth(uv);

  float f;
  vec2 offset;
  float dTotal;

  int samplesDone = 0;
  for(int i = 0; i < samples; i++) {
    f = float(i) / float(samples);
    offset = vec2(radius * pow(f, radAttenuation) * sin(f * spiral + time * spinSpeed), radius * pow(f, radAttenuation) * cos(f * spiral + time * spinSpeed));
    float dd = texture(tDepth, uv + offset).r - dp; // should this be abs()?
    if(dd < 0.01) {
      dTotal += max(dd, 0.);
      samplesDone++;
    }
  }
  dTotal /= float(samplesDone);
  dTotal = (1. - dTotal);
  dTotal = pow(dTotal, dGamma);
  gl_FragColor.rgb = vec3(dTotal * (0.25 * dp + 0.65));
  gl_FragColor.rgb = mix(gl_FragColor.rgb, vec3(dp), 0.5 + 0.5 * sin(time * oscSpeed));
  gl_FragColor.a = 1.;
}
