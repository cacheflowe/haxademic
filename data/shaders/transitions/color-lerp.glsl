// cacheflowe original

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

void main() {
    vec2 p = vertTexCoord.xy;
    vec3 a = texture2D(from, p).rgb;
    vec3 b = texture2D(to, p).rgb;
    float progFactor = 0.25 + 0.15 * sin(p.y * 3.14 * 60.0);
    float prog = smoothstep(progFactor, 1.0 - progFactor, progress);
    vec3 m = mix(a, b, prog);
    gl_FragColor = vec4(m, 1.0);
}
