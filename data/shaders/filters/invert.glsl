#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
// varying vec2 position;   // normal glsl call

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
    // normal glsl call to get pixel color
    //    vec2 p = position;
    //    vec4 color = texture2D(texture, p);
    
    vec4 color = texture2D(texture, vertTexCoord.xy);
    color.rgb = 1.0 - color.rgb;
    gl_FragColor = color;
}