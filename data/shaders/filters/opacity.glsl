// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float opacity;

void main() {
    vec4 color = texture2D( texture, vertTexCoord.xy );
    gl_FragColor = vec4( color.r, color.g, color.b, opacity );
}