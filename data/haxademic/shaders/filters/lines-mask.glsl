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
uniform float lines = 10.;
uniform float rotation = 0.;

#define PI     3.14159265358

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
    return uv;
}

void main() {
    vec4 textureColor = texture2D(texture, vertTexCoord.xy);
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv = rotateCoord(uv, rotation);
    float col = 0.5 + 0.5 * sin(uv.y * lines);
    float aA = lines * 0.001;
    col = smoothstep(0.5 - aA, 0.5 + aA, col);
	  gl_FragColor = vec4(vec3(col), textureColor.a);
}
