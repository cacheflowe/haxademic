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

uniform sampler2D waveformTex;
uniform vec3 color1 = vec3(0.8, 0.2, 0.2);
uniform vec3 color2 = vec3(0.0, 0.0, 0.0);
float stops = 2.;
uniform float zoom = 1.;
uniform float waveformTexZoom = 1.;
uniform float fade = 0.;
uniform float rotate = 0.;
uniform float amp = 0.2;
uniform vec2 offset = vec2(0.);

vec2 rotateCoord(vec2 uv, float rads) {
  uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
	return uv;
}

void main() {
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // add vertical displacement
    uv.y += amp * (-0.5 + texture2D(waveformTex, uv * waveformTexZoom).r);
    // rotate
    uv = rotateCoord(uv, rotate);
    // offset
    uv.x = uv.x + offset.x;
    uv.y = uv.y + offset.y;
    // zoom
    uv *= zoom;			// zoom
    // repeat
    uv = fract(uv);	// repeat

    // fade colors
    float stopSize = 1. / stops;
    vec3 col = vec3(0.);
    float smoothSLow = 0. + fade * 0.5;
    float smoothSHigh = 1. - fade * 0.5;
    if(uv.y < stopSize) {
    	col = mix(color1, color2, smoothstep(smoothSLow, smoothSHigh, uv.y * stops));
    } else if(uv.y < stopSize * 2.) {
    	col = mix(color2, color1, smoothstep(smoothSLow, smoothSHigh, (uv.y - stopSize) * stops));
    }

    // draw
    gl_FragColor = vec4(col,1.0);
    // gl_FragColor = vec4(texture2D(waveformTex, uv).rgb,1.0);

}
