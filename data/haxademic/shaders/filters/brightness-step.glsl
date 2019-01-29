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
uniform float brightStep = -1./255.;

void main() {
    vec4 recolored = texture2D( texture, vec2( vertTexCoord.x, vertTexCoord.y ) );
    gl_FragColor = vec4( recolored.r + brightStep, recolored.g + brightStep, recolored.b + brightStep, recolored.a );
}
