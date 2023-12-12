// by @jorgemoag - https://www.shadertoy.com/view/WdKXRV
// references:
// https://thelig.ht/chladni/

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform float time;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

// const float PI = 3.14159265;

void main() {
  vec2 uv = vertTexCoord.xy - 0.5;
  uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

  vec4 s1 = vec4(1.0, 1.0, 7.0, 2.0);
  vec4 s2 = vec4(-2.0, 1.0, 4.0, 4.6);

  float t = 0.5 * sin(time) + 0.5;

  float a = mix(s1.x, s2.x, t);
  float b = mix(s1.y, s2.y, t);
  float n = mix(s1.z, s2.z, t);
  float m = mix(s1.w, s2.w, t);

  float max_amp = abs(a) + abs(b);
  float amp = a * sin(PI * n * uv.x) * sin(PI * m * uv.y) + b * sin(PI * m * uv.x) * sin(PI * n * uv.y);

  float col = step(abs(amp), 0.1);

  gl_FragColor = vec4(vec3(col), 1.0);
}
