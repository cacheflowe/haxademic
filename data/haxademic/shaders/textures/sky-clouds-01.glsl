// forked from: https://www.shadertoy.com/view/lddcRl
// original by: https://www.shadertoy.com/user/MonterMan

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

float hash(vec2 uv)
{
    return fract(sin(dot(uv, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(vec2 uv)
{
    vec2 ipos = floor(uv);
    vec2 fpos = fract(uv);
    
    float a = hash(ipos);
    float b = hash(ipos + vec2(1.0, 0.0));
    float c = hash(ipos + vec2(0.0, 1.0));
    float d = hash(ipos + vec2(1.0, 1.0));
    
    vec2 u = smoothstep(0.0, 1.0, fpos);
    
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 uv)
{
    float amp = 100.;
    float freq = 0.6;
    
    float acc = 0.0;
    float total_weight = 1.0;
    for (int i = 0; i < 5; ++i)
    {
        acc += amp * noise(freq * uv);
        total_weight += amp;
        
        amp *= 0.35;
        freq *= 2.0;
    }
    
    acc /= total_weight; //normalize fbm value
    return acc;
}

void main() {
    // coords
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv *= 8.0 + 7.0 * sin(time * 0.01);     // zoom

    float t = fbm(uv + fbm(uv + fbm(uv - 0.35*time) + 0.2*time) + 0.3*time);
    vec3 base_col = vec3(0.2, 0.5, 0.7);
    vec3 layered_col = vec3(0.9);
    
    vec3 col = mix(base_col, layered_col, (0.6 + 0.4 * sin(time * 0.1)) * t);
    col = sqrt(col);
    gl_FragColor = vec4(col, 1.0);
}