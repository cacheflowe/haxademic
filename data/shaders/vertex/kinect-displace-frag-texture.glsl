#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
	vec4 color = texture2D(texture, vertTexCoord.st) * vertColor;
    if(color.r < 0.0 && color.g < 0.0 && color.b < 0.0) {
        gl_FragColor = vec4(0,0,0,0);
    } else {
        gl_FragColor = color;
    }
}
