// Ported from: https://www.shadertoy.com/view/4syGWK
// And more info on the technique(s):
// - https://prideout.net/blog/distance_fields/
// - https://prideout.net/blog/distance_fields/distance.txt
// - https://www.shadertoy.com/view/4syGWK - jump flooding
// - https://www.shadertoy.com/results?query=tag=jfa
// - https://shaderbits.com/blog/various-distance-field-generation-techniques
// - https://blog.demofox.org/2016/02/29/fast-voronoi-diagrams-and-distance-dield-textures-on-the-gpu-with-the-jump-flooding-algorithm/
// - https://bgolus.medium.com/the-quest-for-very-wide-outlines-ba82ed442cd9
// - https://www.youtube.com/watch?v=A0pxY9QsgJE
// - https://www.youtube.com/watch?v=AT0jTugdi0M
// - https://www.shadertoy.com/view/4dK3WK
// - https://www.shadertoy.com/view/Mdy3DK
// - https://www.shadertoy.com/view/lsKGDV
// - https://www.shadertoy.com/view/7ttSzr
// - https://blog.demofox.org/2016/03/02/actually-making-signed-distance-field-textures-with-jfa/


#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D texture0;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float iter = 0.;
uniform float time = 0.;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

float luma(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}

// responsive aspect ratio fix from Kyle Meredith / @prismatic.visuals
float screenDistance(vec2 a, vec2 b) {
    vec2 diff = a - b;
    float aspect = (texOffset.y / texOffset.x);
    if(aspect > 1.0)
        diff.x *= aspect;
    else
        diff.y /= aspect;
    return length(diff);
}

void main() {
    // aliases
    float aspect = (texOffset.y / texOffset.x);
    vec2 aspectUV = vec2(aspect, 1.0);
    vec2 uv = vertTexCoord.xy;
    vec4 color = texture2D(texture, uv);
    float lumaVal = luma(color.rgb);

    // for first step, encode luminance values above throwshold to be the original uv coord
    if(iter == 0.) {
        if (lumaVal < 0.4) {
            gl_FragColor =  vec4(0.0);
        } else {
            gl_FragColor = vec4(uv, 0.0, 1.0);
        }
        // debug uv coords: gl_FragColor = vec4(uv, 0.0, 1.0);
    } else if(iter <= 13.) {
        // JFA step (for up to 4096x4096)
        // goes to 13 because the first step is used for encoding
        // with each step, the kernel sampling gets smaller
        // TODO: calc max kernel needed and remove extraneous passes
        float level = clamp(iter - 1.0, 0.0, 12.0);
        float stepKernelSize = pow(2., 12.0 - level); // 2^12 is 4096
        vec2 texelSize = texOffset; // helps us translate to actual pixels  via the stepKernelSize

        float bestDist = 999999.0;
        vec2 closestCoord = color.xy; 
        vec2 center = vec2(uv);
        for (int y = -1; y <= 1; ++y) {
            for (int x = -1; x <= 1; ++x) {
                vec2 kernelOffset = vec2(x,y) * texelSize * stepKernelSize;
                vec2 kernelSampleUV = center + kernelOffset;
                vec2 neighborUV = texture2D(texture, kernelSampleUV).xy;
                // float d = length(neighborUV - center);
                float d = screenDistance(neighborUV, center);
                if ((neighborUV.x != 0.0) && (neighborUV.y != 0.0) && (d < bestDist)) {
                    bestDist = d;
                    closestCoord = neighborUV;
                }
            }
        }        
        gl_FragColor = vec4(closestCoord,0.0,1.0);
    } else {
        // make sdf distance gradient
        // decode UV coord pointer stored in pixel
        vec2 nearestUV = texture2D(texture, color.rg).rg;  // vec2 p = (nearestUV * 2.0 - 1.0) * aspect;
        // float d = length(uv - nearestUV);
        float d = screenDistance(uv, nearestUV);
        gl_FragColor = vec4(vec3(d),1.0);
    }

}