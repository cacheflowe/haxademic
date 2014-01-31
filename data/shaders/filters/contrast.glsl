// converted from: https://github.com/BradLarson/GPUImage/blob/master/framework/Source/GPUImageContrastFilter.m

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float contrast;

void main() {
    vec4 textureColor = texture2D( texture, vec2( vertTexCoord.x, vertTexCoord.y ) );
    gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);
}