
// by cacheflowe
// rotate & repeat. the PGraphics instance should have repeat mode turned on:
// pg.textureWrap(Texture.REPEAT);

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float aspect = 1.5;
uniform float zoom = 1.;
uniform float rotation = 0.;
uniform vec2 offset = vec2(0.);

void main() {
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	  uv *= (1. - zoom / 2.);
    uv *= mat2(cos(rotation), sin(rotation), -sin(rotation), cos(rotation));
    uv.y *= aspect;
    vec4 color = texture(texture, uv - 0.5 + offset);
    gl_FragColor = color;
}
