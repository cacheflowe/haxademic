#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform int flip = 0;

void main() {
    vec4 color = texture2D( texture, vec2( vertTexCoord.x, vertTexCoord.y ) );
    float alpha = (color.r + color.g + color.b) / 3.0;
    if(flip == 1) alpha = 1.0 - alpha;
    gl_FragColor = vec4(color.rgb, alpha * color.a);
}
