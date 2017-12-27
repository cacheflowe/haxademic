#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float amp = 0.0005;
uniform float samplemult = 1.0;
uniform float waveAmp = 0.;   // max: 0.01f
uniform float waveFreq = 1.;  // max: 20f

float TWO_PI = radians(360.);
float PI = radians(180.);

void main() {
  vec2 uv = vertTexCoord.xy;
  vec2 center = vec2(0.5);
  float distFromCenter = distance(uv, center);
  float curRads = atan(center.y - uv.y, center.x - uv.x);			// get current pixel's angle to center
  float ampByDist = amp * (1. + distFromCenter * 130.);       // speed up when further from center
  // if(waveAmp > 0.) ampByDist = ampByDist * (1. + 0.2 * sin(distFromCenter * 10.));
  if(waveAmp > 0.) curRads += sin(distFromCenter * waveFreq);
  vec2 displace = uv + vec2(waveAmp * cos(curRads), waveAmp * sin(curRads));
  vec4 sampleColor = texture2D(texture, displace) * vec4(vec3(samplemult), 1.);
  gl_FragColor = sampleColor;
}
