#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec3 colorCompare = vec3(0.);

void main() {
    vec4 textureColor = texture2D(texture, vertTexCoord.xy);
    float colorDistance = distance(colorCompare, textureColor.rgb) / 3.;
    gl_FragColor = vec4(vec3(colorDistance), 1.);
}
