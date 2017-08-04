#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float spread;
uniform float darkness = 1.;

void main() {
    vec4 color = texture2D( texture, vertTexCoord.xy );
    float dist = distance( vertTexCoord.xy, vec2( 0.5 ) );
    color.rgb *= 1. - dist;
    gl_FragColor = color;
}
