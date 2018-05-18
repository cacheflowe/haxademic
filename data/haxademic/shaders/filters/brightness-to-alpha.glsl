#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
    vec4 color = texture2D( texture, vec2( vertTexCoord.x, vertTexCoord.y ) );
    float alpha = 1.0 - (color.r + color.g + color.b) / 3.0;
    gl_FragColor = vec4(color.rgb, alpha * color.a);
}
