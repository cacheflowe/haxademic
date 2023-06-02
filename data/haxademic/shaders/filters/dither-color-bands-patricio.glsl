// from Lygia p5 example. thank you Patricio!

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D blueNoiseTex;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;
uniform float ditherAmp = 0.8;

float remap(float value, float low1, float high1, float low2, float high2) {
    return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

vec4 bluenoise( vec2 fc ) {
    ivec2 texSizeNoise = textureSize(blueNoiseTex, 0);
    // ivec2 srcSize = textureSize(texture, 0);

    // check for texture size - we need to compensate for difference in 
    // noise vs destination texture size for texture repeating
    vec2 uvPixel = 1. / texOffset.xy * fc;   // get actual pixel position
    vec2 seedBlue = uvPixel / texSizeNoise.x;    
    return texture2D( blueNoiseTex, seedBlue );
}

float remap_pdf(float v) {
    v = v * 2.0 - 1.0;
    v = sign(v) * (1.0 - sqrt(1.0 - abs(v)));
    return 0.5 + 0.5 * v;
}

vec3 ditherBlueNoise(vec2 uv, vec3 rgb, float curTime) {
    uv += 1337.0 * fract(curTime * 0.001);
    vec4 bn = bluenoise(uv);
    vec3 bn_tri = vec3(remap_pdf(bn.x), 
                        remap_pdf(bn.y), 
                        remap_pdf(bn.z));

    float amp = remap(ditherAmp, 0., 1., 20., 1.);
    rgb += (bn_tri * 1.0 - 0.5) / amp;
    return rgb;
}


void main() {
    vec2 uv = vertTexCoord.xy;
    vec4 color = texture2D(texture, uv);
    color.rgb = ditherBlueNoise(uv, color.rgb, time);
    color.rgb = floor(color.rgb * 255.0) / 255.0;

    gl_FragColor = color;

    // debug blue noise 
    // gl_FragColor = bluenoise(uv);
}
