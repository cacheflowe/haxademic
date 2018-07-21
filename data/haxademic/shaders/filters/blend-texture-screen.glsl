// borrowed from: https://github.com/jamieowen/glsl-blend/blob/master/screen.glsl

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D targetTexture;

float blendScreen(float base, float blend) {
	return 1.0-((1.0-base)*(1.0-blend));
}

vec3 blendScreen(vec3 base, vec3 blend) {
	return vec3(blendScreen(base.r,blend.r),blendScreen(base.g,blend.g),blendScreen(base.b,blend.b));
}

vec3 blendScreen(vec3 base, vec3 blend, float opacity) {
	return (blendScreen(base, blend) * opacity + base * (1.0 - opacity));
}

void main() {
	vec2 uv = vertTexCoord.xy;
  vec4 colorCurrent = texture2D(texture, uv);
  vec4 colorTarget = texture2D(targetTexture, uv);
	gl_FragColor = vec4(blendScreen(colorCurrent.rgb, colorTarget.rgb), 1.);
}



