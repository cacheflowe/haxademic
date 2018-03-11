#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
    vec4 color = texture2D(texture, vertTexCoord.xy);
    vec3 inverted = vec3(1.0) - color.rgb;
    gl_FragColor = vec4(inverted.rgb, color.a);
}
