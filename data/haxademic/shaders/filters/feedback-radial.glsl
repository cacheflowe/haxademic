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
uniform float alphaMult = 1.;

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
  vec2 displace = uv + vec2(amp * cos(curRads), amp * sin(curRads));
  vec4 sampleColor = texture2D(texture, displace) * vec4(vec3(samplemult), alphaMult);
  gl_FragColor = sampleColor;
}
