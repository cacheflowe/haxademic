/**
 * GlowFilter, originally by mishaa
 * converted from PIXI.js
 * http://www.html5gamedevs.com/topic/12756-glow-filter/?hl=mishaa#entry73578
 * http://codepen.io/mishaa/pen/raKzrm
 */

 #ifdef GL_ES
 precision mediump float;
 precision mediump int;
 #endif

 #define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

const float PI = 3.14159265358979323846264;
const float TWO_PI = PI * 2.0;
uniform float sampleDistance = 20.0;
uniform float radialSamples = 12.0;
uniform float sampleStep = 1.0;
uniform vec4 glowColor = vec4(0.,0.,0.,0.5);
uniform vec2 offset = vec2(0., 20.);

void main() {
  // get current pixel color
  vec2 uv = vertTexCoord.xy;
  vec4 ownColor = texture2D(texture, uv);
  if(ownColor.a > 0.99) {
    gl_FragColor = ownColor;
  } else {
    // sample neighbor colors in a circle - find closest non-opaque pixel
    // vec2 offsetPixels = offset * texOffset.xy; // normalize to pixel size
    float sampleRadians = TWO_PI / radialSamples;
    float minDist = sampleDistance;
    int looking = 1;
    for (float angle = 0.05; angle < TWO_PI; angle += sampleRadians) { // start a little off-ceneter because straight lines with distance check are really apparent
      looking = 1;
      for(float distToCheck = 0; distToCheck < sampleDistance; distToCheck += sampleStep) {
        if(looking == 1) {
          vec2 sampleLoc = uv + vec2(sin(angle), cos(angle)) * length(texOffset.xy) * distToCheck; // offsetPixels +
          vec4 curColor = texture2D(texture, sampleLoc);
          if(curColor.a > 0.99) {
            minDist = min(minDist, distance(uv, sampleLoc.xy));
            looking = 0;
          }
        }
      }
    }
    if(minDist == sampleDistance) {
      gl_FragColor = vec4(0.);
    } else {
      float textureW = 1. / length(texOffset.xy);
      float falloffMult = (textureW / sampleDistance) * textureW;
      vec4 color = vec4(glowColor.rgb, glowColor.a - glowColor.a * ((minDist / sampleDistance) * textureW * 2.));
      gl_FragColor = mix(ownColor, color, 1. - ownColor.a); // mix between calculated glow and original color using original alpha as the gradient
    }
  }
}
