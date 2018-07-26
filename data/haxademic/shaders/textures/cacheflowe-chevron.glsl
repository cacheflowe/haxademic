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

float saw(float rads) {
    rads += PI * 0.5;
    float percent = mod(rads, PI) / PI;
    float dir = sign(sin(rads));
    return dir * (2. * percent  - 1.);
}

void main() {
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    float amp = 0.03 + 0.03 * sin(time);
    float freq = 25. + 15. * sin(time);
    float numLines = 40. + 15. * sin(time);
    uv += vec2(1.0, amp * saw(uv.x * freq));
    float col = 0.5 + 0.5 * sin(uv.y * numLines);
    float aA = numLines * 0.0015;
    col = smoothstep(0.5 - aA, 0.5 + aA, col);
	  gl_FragColor = vec4(vec3(col),1.0);
}
