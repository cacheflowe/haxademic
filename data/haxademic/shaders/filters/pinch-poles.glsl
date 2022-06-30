#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float crossfade;

float remap(float value, float low1, float high1, float low2, float high2) {
    return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
    vec2 uv = vertTexCoord.xy;
    float unitY = remap(uv.y, 0., 1., 1., -1.);
    float unitX = remap(uv.x, 0., 1., -1., 1.);
    float xAmp = cos(asin(unitY));	// pinch amp at poles
    float halfW = 0.5;
    float xRemapped = halfW + (halfW * unitX / xAmp); // * Xamp to go outward like an equirectangular texture
    float sampleX = mix(uv.x, xRemapped, crossfade);
    vec4 textureColor = texture2D(texture, vec2(sampleX, uv.y));
    gl_FragColor = textureColor;
    if(sampleX > 1. || sampleX < 0.) gl_FragColor = vec4(0.,0.,0.,1.);
}
