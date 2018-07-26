// from: https://github.com/BradLarson/GPUImage/blob/master/framework/Source/GPUImageSaturationFilter.m

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float saturation = 0.;

const vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);

void main() {
    vec4 textureColor = texture2D(texture, vertTexCoord.xy);
    float luminance = dot(textureColor.rgb, luminanceWeighting);
    vec3 greyScaleColor = vec3(luminance);

    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);
}
