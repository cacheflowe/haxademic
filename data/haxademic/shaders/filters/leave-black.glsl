// by cacheflowe
// leaves black but turns other colors transparent

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float crossfade = 1.0;

void main() {
    vec4 color = texture2D(texture, vertTexCoord.xy);
    float grayColor = (color.r + color.g + color.b) / 3.;
    grayColor = smoothstep(0., 0.2, grayColor); // quick falloff from full black
    float alpha = 1. - (grayColor * crossfade);
    gl_FragColor = vec4(color.rgb, alpha);
}
