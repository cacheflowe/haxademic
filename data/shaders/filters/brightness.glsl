// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float brightness;

void main() {
    vec4 recolored = texture2D( texture, vec2( vertTexCoord.x, vertTexCoord.y ) ) * brightness;
    gl_FragColor = vec4( recolored.r, recolored.g, recolored.b, 1.0 );
}