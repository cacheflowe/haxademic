// from: https://www.shadertoy.com/view/MslGR8
// and:  https://www.shadertoy.com/view/3tsSzl
// and:  https://www.shadertoy.com/view/tllcR2

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D blueNoiseTex;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;
uniform float noiseAmp = 7.;

vec4 bluenoise( vec2 fc ) {
    return texture2D( blueNoiseTex, fc );
}

highp float random(vec2 coords) {
    return fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    // get color & aspect-corrected uv
    vec2 uv = vertTexCoord.xy;
    vec4 color = texture2D(texture, uv);

    // get moving uv for noise
    vec2 seed = uv.xy;
    seed.x *= texOffset.y / texOffset.x;
    vec2 seedBlue = seed;
    seed += fract(time);

    // check for texture size - we need to compensate for differnce in noise vs destination texture size
    ivec2 texSizeNoise = textureSize(blueNoiseTex, 0);
    seedBlue.x *= texOffset.x / float(texSizeNoise.x);
    seedBlue.y *= texOffset.y / float(texSizeNoise.y);

    // do random noise then blue noise
    float amp = noiseAmp / 255.;
    vec3 outcol = color.rgb + mix(-amp, amp, random(seed));
    outcol = outcol.rgb + mix(-amp, amp, bluenoise(seedBlue).r);
    // add more layers of offset noise
    outcol = outcol.rgb + mix(-amp, amp, random(seed + 0.25));
    outcol = outcol.rgb + mix(-amp, amp, bluenoise(seedBlue + 0.25).r);
    outcol = outcol.rgb + mix(-amp, amp, random(seed + 0.5));
    outcol = outcol.rgb + mix(-amp, amp, bluenoise(seedBlue + 0.5).r);
    outcol = outcol.rgb + mix(-amp, amp, random(seed + 0.75));
    outcol = outcol.rgb + mix(-amp, amp, bluenoise(seedBlue + 0.75).r);

    // write color
    gl_FragColor = vec4(outcol, color.a);
}
