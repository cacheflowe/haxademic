#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float amp = 0.001;
uniform float samplemult = 1.0;
uniform float wavy = 0.;

float TWO_PI = radians(360.);
float PI = radians(180.);

void main() {
  vec2 uv = vertTexCoord.xy;
  vec2 center = vec2(0.5);
  float distFromCenter = distance(uv, center);
  float curRads = atan(center.y - uv.y, center.x - uv.x);			// get current pixel's angle to center
  float ampByDist = amp * (1. + distFromCenter * 130.); // speed up when further from center
  float wobbleAmp = ampByDist * (1. + 0.1 * sin(distFromCenter * 100.));
  curRads += 0.9 * sin(distFromCenter * 10.);
  vec2 displace = uv + vec2(wobbleAmp * cos(curRads), wobbleAmp * sin(curRads));
  vec4 sampleColor = texture2D(texture, displace) * vec4(vec3(samplemult), 1.);
  gl_FragColor = sampleColor;
}
