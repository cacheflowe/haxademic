
// from: https://www.shadertoy.com/view/3lXcW8

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float radialLength = 0.95;     // 0.5 - 1.0
uniform float imageBrightness = 9.0;   // 0 - 10
uniform float flareBrightness = 4.5;   // 0 - 10
uniform float iters = 100.;            // 100, higher is better

// no idea how this `define` works...
#define T texture(texture,.5+(p.xy*=.992)).rgb

void main() {
    // create a vec position. z is a brightness calculation that gets updated in the loop below
    float brightZ = max(0.0, (imageBrightness/10.0) - 0.5) - 0.5;
    vec3 p = vec3(vertTexCoord.xy - 0.5, brightZ);

    // get color
    vec3 o = T;
    
    // iterate
    for (float i=0.0; i < iters; i++) {
        p.z += pow(max(0.0, 0.5-length(T)), 10.0/flareBrightness) * exp(-i * (1.0-(radialLength)) );
    }
    
    vec3 flare = p.z * vec3(0.7, 0.9, 1.0); // tint
    gl_FragColor = vec4(o*o+flare, 1.0);
}
