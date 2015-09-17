// converted from: http://alaingalvan.tumblr.com/post/79864187609/glsl-color-correction-shaders

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float brightness;
uniform float contrast;
uniform float gamma;

vec3 brightnessContrast(vec3 value, float brightness, float contrast) {
    return (value - 0.5) * contrast + 0.5 + brightness;
}

vec3 gammaFilter(vec3 value, float param) {
    return vec3(pow(abs(value.r), param),pow(abs(value.g), param),pow(abs(value.b), param));
}

void main() {
    vec4 recolored = texture2D( texture, vec2( vertTexCoord.x, vertTexCoord.y ) );
    gl_FragColor = vec4( gammaFilter( brightnessContrast(recolored.rgb, brightness, contrast), gamma ), 1.0 );
}