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
    vec4 textureColor = texture2D(texture, vec2(vertTexCoord.x, vertTexCoord.y));
    textureColor.rgb = ((textureColor.rgb - 0.5) * contrast) + 0.5;
    gl_FragColor = vec4(clamp(textureColor.rgb, 0., 1.), textureColor.a);
}
