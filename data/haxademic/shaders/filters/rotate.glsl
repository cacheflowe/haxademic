// by cacheflowe
// rotate & repeat. the PGraphics instance should have repeat mode turned on:
// pg.textureWrap(Texture.REPEAT);

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D textureDupe;              // uv multiplication gets weird and buggy on original texture
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float zoom = 1.;
uniform float rotation = 0.;
uniform vec2 offset = vec2(0.);

void main() {
    vec2 uv = vertTexCoord.xy - 0.5;
	uv *= zoom;
    // uv.y *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv *= mat2(cos(rotation), sin(rotation), -sin(rotation), cos(rotation));
    gl_FragColor = texture2D(textureDupe, uv - 0.5 + offset);
}
