// ported from: http://glslsandbox.com/e#26469.2
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

#define SAMPLES 4

vec3 sample(vec2 pos) {
	float a = length(pos) + time*0.1;
	float b = mod(a*30.0, 2.0) > 0.8 ? 1.0 : 0.0;
	return mix(vec3(0.0, 0.0, 0.0), vec3(1.0, 1.0, 1.0), pow(b, 1.0));
}

void main (void) {
	vec2 pos = vertTexCoord.xy - vec2(0.5, 0.5);
	pos.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	vec3 color = vec3(0.0);
	float sdist = 0.002 / float(SAMPLES);
	for(int x = 0; x < SAMPLES; x++){
		for(int y = 0; y < SAMPLES; y++){
			color += sample(pos + vec2(sdist * float(x), sdist * float(y)));
		}
	}
	color /= float(SAMPLES*SAMPLES);
	gl_FragColor = vec4(color, 1.0 );
}
