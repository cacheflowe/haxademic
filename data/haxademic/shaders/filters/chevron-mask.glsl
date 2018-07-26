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
uniform float lines = 40;
uniform float amp = 0.04;
uniform float freq = 40;

#define PI     3.14159265358

float saw(float rads) {
    rads += PI * 0.5;
    float percent = mod(rads, PI) / PI;
    float dir = sign(sin(rads));
    return dir * (2. * percent  - 1.);
}

void main() {
    vec4 textureColor = texture2D(texture, vertTexCoord.xy);
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv += vec2(0.0, amp * saw(uv.x * freq));
    float col = 0.5 + 0.5 * sin(uv.y * lines);
    float aA = lines * 0.001;
    col = smoothstep(0.5 - aA, 0.5 + aA, col);
	  gl_FragColor = vec4(vec3(col), textureColor.a);
}
