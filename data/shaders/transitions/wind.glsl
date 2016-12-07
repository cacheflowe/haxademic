// from: http://transitions.glsl.io/transition/7de3f4b9482d2b0bf7bb

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

uniform float size = 0.3;

float rand (vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    vec2 p = vertTexCoord.xy;
    float r = rand(vec2(0, p.y));
    float m = smoothstep(0.0, -size, p.x*(1.0-size) + size*r - (progress * (1.0 + size)));
    gl_FragColor = mix(texture2D(from, p), texture2D(to, p), m);
}