// by cacheflowe
// colorizes pixels, but uses original image's alpha. useful for fake shadows

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec4 color = vec4(0., 0., 0., 1.);

void main() {
    vec4 origColor = texture2D(texture, vertTexCoord.xy);
    gl_FragColor = vec4(color.rgb, min(origColor.a, color.a));
}
