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

// saw method ported from my Processing code to be a drop-in replacement for sin()
// there's probably a way better way to do this..
float saw(float rads) {
    rads += PI * 0.5; // sync oscillation up with sin()
    float percent = mod(rads, PI) / PI;
    float dir = sign(sin(rads));
    return dir * (2. * percent  - 1.);
}

void main()
{
    float timeAdjusted = time * 0.25;
    // center coordinates
    // vec2 uv = (2. * fragCoord - iResolution.xy) / iResolution.y;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv *= 0.5; // zoom in

    // wobble
    float wobbleOscillations = 4. + 4. * sin(timeAdjusted);
    float wobbleAmp = 0.3;
    float radiusOsc = 1. + wobbleAmp + wobbleAmp * sin(PI/2. + wobbleOscillations * atan(uv.x, uv.y));
    float dist = length(uv) * radiusOsc; // distance(uv, center) * wobble;
    // line params
    float expandTime = timeAdjusted * -20.;
    float spacing = 40. + 20. * sin(timeAdjusted);
    float baseColor = 0.5;// 0.6 + 0.2 * sin(timeAdjusted);
    float colorSpread = 0.5;// + 0.2 * sin(PI + timeAdjusted);
    // concentric color oscillation
    float color = baseColor + colorSpread * saw(expandTime + spacing * dist);
    color = smoothstep(0.45, 0.55, color);
    gl_FragColor = vec4(vec3(color), 1.);
}
