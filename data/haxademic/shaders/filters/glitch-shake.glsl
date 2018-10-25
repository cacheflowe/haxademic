// glitch shader forked from: https://www.shadertoy.com/view/Md2GDw

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;
uniform float glitchSpeed = 0.16;
uniform float amp = 0.3;
uniform float crossfade = 1.;
uniform float subdivide1 = 64.;
uniform float subdivide2 = 64.;

// noise from https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83

float rand(float n){return fract(sin(n) * 43758.5453123);}
float rand(vec2 n) {
	return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}
float noise(float p){
	float fl = floor(p);
  	float fc = fract(p);
	return mix(rand(fl), rand(fl + 1.0), fc);
}

float noise(vec2 n) {
	const vec2 d = vec2(0.0, 1.0);
  	vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
	return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}


vec3 noise3d(vec2 uv) {
    uv *= 1.;	// * 300 looks like real RGB noise
    return vec3(
    	noise(uv.xy),
        rand(noise(uv.yx)),
        noise(uv.yx)
    );
}

void main() {
	vec2 uv = vertTexCoord.xy;
	vec4 color = texture2D(texture, uv);
	vec2 block = floor(vertTexCoord.xy * vec2(16));
	vec2 uv_noise = block / subdivide1;
	uv_noise += floor(vec2(time * glitchSpeed) * vec2(1234.0, 3543.0)) / subdivide2;

	float shakeThresh = mod(time * glitchSpeed * 1236.0453, 1.0);

	// glitch some blocks and lines
	if (noise3d(uv_noise).r > shakeThresh) {
		vec2 dist = (fract(uv_noise) - 0.5);
		uv += dist * amp;
	}
	vec4 glitchColor = texture2D(texture, uv);
	gl_FragColor = mix(color, glitchColor, crossfade);
}
