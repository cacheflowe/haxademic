// converted from: https://www.shadertoy.com/view/MtlGRn
// Reference: http://www.crytek.com/download/Sousa_Graphics_Gems_CryENGINE3.pdf on slide 36
// Implemented as GLSL example by Benjamin 'BeRo' Rosseaux - CC0
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time;

const float PI = 3.14159265359;

// o = tap sample xy, f = f-factor, n = diaphragm shape ngon, phiShutterMax = max. lens shutter rotation
vec2 getBokehTapSampleCoord(const in vec2 o, const in float f, const float n, const in float phiShutterMax){
    vec2 ab = (o * 2.0) - vec2(1.0);
    vec2 phir = ((ab.x * ab.x) > (ab.y * ab.y)) ? vec2((abs(ab.x) > 1e-8) ? ((PI * 0.25) * (ab.y / ab.x)) : 0.0, ab.x) : vec2((abs(ab.y) > 1e-8) ? ((PI * 0.5) - ((PI * 0.25) * (ab.x / ab.y))) : 0.0, ab.y);
    phir.x += f * phiShutterMax;
   	phir.y *= (f > 0.0) ? pow((cos(PI / n) / cos(phir.x - ((2.0 * (PI / n)) * floor(((n * phir.x) + PI) / (2.0 * PI))))), f) : 1.0;
    return vec2(cos(phir.x), sin(phir.x)) * phir.y;
}

void main(){
    // should for real usage: (fstop - fstopmin) / (fstopmax - fstopmin)
    float f = (sin(time) * 0.5) + 0.5;

    // Diaphragm shape structure: 4.0 = box, 5.0 = pentagon, 6.0 = hexagon, 7.0 = heptagon, 8.0 = octagon etc.
    float ngon = 6.0; // 6.0 because I like hexagons :-)

    vec2 coord = (vertTexCoord.xy - vec2(0.5));
    coord.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    coord *= 1.; // zoom

    float v = 0.0;
    for(float y = 0.0; y <= 1.0; y += 1.0 / 16.0){
        for(float x = 0.0; x <= 1.0; x += 1.0 / 16.0){
            vec2 c = getBokehTapSampleCoord(vec2(x, y), f, ngon, PI * 0.5);
            v = mix(1.0, v, pow(smoothstep(0.0, 0.05, length(coord - c)), 8.0));
        }
    }
    vec3 color = vec3(smoothstep(0.25, 0.75, v));
    gl_FragColor = vec4(color, 1.);
}
