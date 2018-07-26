// borrowed from: https://github.com/jamieowen/glsl-blend/blob/master/darken.glsl

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D targetTexture;

float blendDarken(float base, float blend) {
	return min(blend,base);
}

vec3 blendDarken(vec3 base, vec3 blend) {
	return vec3(blendDarken(base.r,blend.r),blendDarken(base.g,blend.g),blendDarken(base.b,blend.b));
}

vec3 blendDarken(vec3 base, vec3 blend, float opacity) {
	return (blendDarken(base, blend) * opacity + base * (1.0 - opacity));
}

void main() {
	vec2 uv = vertTexCoord.xy;
  vec4 colorCurrent = texture2D(texture, uv);
  vec4 colorTarget = texture2D(targetTexture, uv);
	gl_FragColor = vec4(blendDarken(colorCurrent.rgb, colorTarget.rgb), 1.);
}



