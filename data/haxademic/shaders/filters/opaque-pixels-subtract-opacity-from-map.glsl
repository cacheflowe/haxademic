// by cacheflowe
// if opaque pixels are found, reduce original alpha by opacity of map
// useful for knocking out transparency on the destination image

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D opacityMap;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
    vec4 origColor = texture2D(texture, vertTexCoord.xy);
    vec4 opacityMap = texture2D(opacityMap, vertTexCoord.xy);
    if(opacityMap.a > 0.0) {
        gl_FragColor = vec4(origColor.rgb, origColor.a * (1. - opacityMap.a));
    } else {
        gl_FragColor = origColor;
    }
}
