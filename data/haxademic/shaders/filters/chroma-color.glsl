// from: https://github.com/BradLarson/GPUImage/blob/master/framework/Source/GPUImageChromaKeyFilter.m

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float thresholdSensitivity;
uniform float smoothing;
uniform vec3 colorToReplace;

void main()
{
    vec4 textureColor = texture2D(texture, vertTexCoord.xy);
    float blendValue = smoothstep(thresholdSensitivity, thresholdSensitivity + smoothing, distance(colorToReplace, textureColor.rgb));
    gl_FragColor = vec4(textureColor.rgb, textureColor.a * blendValue);
}
