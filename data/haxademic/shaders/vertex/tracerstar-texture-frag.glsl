#ifdef GL_ES
precision highp float;
precision mediump int;
#endif

#define PROCESSING_COLOR_SHADER


attribute vec4 position;

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

varying vec3 v_texCoord3D;
uniform float time = 0;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

void main(void) {
  // remap 3d uv coords for wavy line
  vec3 p = v_texCoord3D.xyz;
  float radsToCenter = atan(p.x, p.z);
  p.y += sin(time + radsToCenter * 8.) * 200.;

  // stripes
  float grey = smoothstep(0.35, 0.65, 0.5 + 0.5 * sin(time + p.y / 10.));
  vec4 color = vec4(grey, grey, grey, 1.0);

  gl_FragColor = color;
}
