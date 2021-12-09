// borrowed from: https://www.shadertoy.com/view/WsjSzm

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

void main() {
	vec2 uv = vertTexCoord.xy;
	float vt  = texture2D(texture, vec2(0.5) + vec2(sin(time * 0.2) * 0.1, 0.0)).x;
	float f   = texture2D(texture, (uv - 0.5) * (vt * 0.2 + 0.8) + 0.5).x;  
	vec3 col = texture2D(texture, (uv - 0.5) * f + 0.5).xyz;
	float v = length(uv * 2.0 - 1.0);
	v *= v * (1.0 - vt);
	// col *= (1.0 - 0.9 * v);
	gl_FragColor = vec4(col, 1.0);
}
