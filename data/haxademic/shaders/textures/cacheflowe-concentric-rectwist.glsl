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
uniform float amp = 0.;
uniform float freq = 0.5;

#define PI     3.14159265358
#define TWO_PI (PI * 2.)

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
	  return uv;
}

void main()
{
    float timeAdjusted = time * 0.5;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // uv *= 0.5; // zoom in

    uv *= 10.;
    if(amp > 0.) uv = rotateCoord(uv, amp * length(uv) * sin(time * freq));
    float lines = 60. + 20. * sin(time);
    float col = 0.5 + 0.5 * sin(max(abs(uv.x), abs(uv.y)) * lines - time * 5.);
    gl_FragColor = vec4(vec3(smoothstep(0.15, 0.85, col)), 1.);
    // float aa = 0.1;
    // gl_FragColor = vec4(vec3(smoothstep(0.5 - aa, 0.5 + aa, col)), 1.);
}
