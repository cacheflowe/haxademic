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
#define TWO_PI (PI * 2.)

float saw(float rads) {
    rads += PI * 0.5; // sync oscillation up with sin()
    float percent = mod(rads, PI) / PI;
    float dir = sign(sin(rads));
    return dir * (2. * percent  - 1.);
}

void main()
{
    float timeAdjusted = time * 1.2;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // uv *= 0.6; // zoom
    // uv -= vec2(0.5, 0);
    float freq = 25. + 15. * sin(timeAdjusted);
    float amp = 0.1 + 0.1 * sin(timeAdjusted);
    float increaseOsc = abs(uv.x) * 1.15;	// more oscillations to the right
    float yOsc = saw(PI/2. + increaseOsc * freq);
    float yOscOffset = saw(PI + increaseOsc * freq);
    float yAdd = amp * (increaseOsc + increaseOsc * yOsc);
    uv += vec2(0., yAdd * (0.5 + 0.5 * cos(abs(uv.x))));
    float stripeFreq = 50.;
    float col = smoothstep(0.3, 0.4, 0.5 + 0.5 * sin(uv.y * stripeFreq));	// faded stripes
    //if(yOscOffset > 0.) col -= uv.x * 1. * amp;				// add fake lighting
	  gl_FragColor = vec4(vec3(col),1.0);
}
