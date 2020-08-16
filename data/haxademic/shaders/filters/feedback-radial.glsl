#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float amp = 1. / 255.;
uniform float samplemult = 1.0;
uniform float waveAmp = 0.;   // max: 0.01f
uniform float waveFreq = 0.;  // max: 20f
uniform float waveStart = 0.;
uniform float alphaMult = 1.;
uniform float multX = 1.;
uniform float multY = 1.;

float TWO_PI = radians(360.);
float PI = radians(180.);

void main() {
  vec2 uv = vertTexCoord.xy;
  vec2 center = vec2(0.5);
  float distFromCenter = distance(uv, center);
  float curRads = atan(center.y - uv.y, center.x - uv.x);			// get current pixel's angle to center
  curRads += sin(waveStart + distFromCenter * waveFreq) * waveAmp;
  vec2 displaceDir = vec2(amp * cos(curRads), amp * sin(curRads));
  displaceDir.y *= texOffset.y / texOffset.x;		// Correct displacement for aspect ratio
  displaceDir.x *= multX;                       // allow tweaks to axis of displacement
  displaceDir.y *= multY;
  vec2 displaceSampleUV = uv + displaceDir;
  vec4 sampleColor = texture2D(texture, displaceSampleUV) * vec4(vec3(samplemult), alphaMult);
  gl_FragColor = sampleColor;
}
