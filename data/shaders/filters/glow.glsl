/**
 * glow filter by cacheflowe
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
uniform int replaceOriginal = 0;
uniform float sampleDistance = 20.0;
uniform float radialSamples = 12.0;
uniform float sampleStep = 1.0;
uniform vec4 glowColor = vec4(0.,0.,0.,0.5);
uniform vec2 offset = vec2(0., 20.);

void main() {
  // get current pixel color
  vec2 uv = vertTexCoord.xy;
  vec4 origColor = texture2D(texture, uv);
  vec4 replaceColor = (replaceOriginal == 1) ? glowColor : origColor;
  if(origColor.a > 0.99) {
    gl_FragColor = replaceColor;
  } else {
    // sample neighbor colors in a circle - find closest non-opaque pixel
    float sampleRadians = TWO_PI / radialSamples;
    float minDist = sampleDistance;
    int looking = 1;
    for (float angle = 0.05; angle < TWO_PI; angle += sampleRadians) { // start a little off-ceneter because straight lines with distance check are really apparent
      looking = 1;  // try to optimize a tiny bit
      for(float distToCheck = 0; distToCheck < sampleDistance; distToCheck += sampleStep) {
        if(looking == 1) {
          vec2 sampleLoc = uv + vec2(cos(angle), sin(angle)) * length(texOffset.xy) * distToCheck;
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
      gl_FragColor = mix(replaceColor, color, 1. - origColor.a); // mix between calculated glow and original color using original alpha as the gradient
    }
  }
}
