// from: http://transitions.glsl.io/transition/35e8c18557995c77278e

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D from;
uniform sampler2D to;
uniform float progress;

uniform float blocksize = 1.0;

// A simple PRNG
float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
  vec2 p = vertTexCoord.xy;
  gl_FragColor = mix(texture2D(from, p), texture2D(to, p), step(rand(floor(gl_FragCoord.xy/blocksize)), progress));
}
