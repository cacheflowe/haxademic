// from: https://www.shadertoy.com/view/MlXXRS


#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float time;
uniform float amplitude;
varying vec4 vertColor;
varying vec4 vertTexCoord;




void main() {
	vec2 cPos = vertTexCoord.xy - 0.5;
	float cLength = length(cPos);

	float spread = 10.0;
	vec2 uv = vertTexCoord.xy + (cPos/cLength) * cos(cLength*spread-time*14.0) * (0.04 * amplitude);
	vec3 col = texture2D(texture,uv).xyz;

	gl_FragColor = vec4(col,1.0);
}
