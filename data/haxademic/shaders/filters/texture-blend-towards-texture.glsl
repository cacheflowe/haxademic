#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D targetTexture;
uniform float blendLerp = 0.2;//0.04;

void main() {
	vec2 uv = vertTexCoord.xy;
  vec4 colorCurrent = texture2D(texture, uv);
  vec4 colorTarget = texture2D(targetTexture, uv);
	gl_FragColor = mix(colorCurrent, colorTarget, blendLerp);
}
