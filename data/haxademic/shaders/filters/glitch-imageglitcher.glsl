// GLSL version of ImageGlitcher: https://www.airtightinteractive.com/demos/js/imageglitcher/
// from: https://www.shadertoy.com/view/MtXBDs

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform bool colorSeparation = false;
uniform float time = 0;
uniform float amp = 0.2;
uniform float glitchSpeed = 0.16;
uniform float barSize = 0.25;
uniform float numSlices = 10.0;
uniform float crossfade = 1.;

//2D (returns 0 - 1)
float random2d(vec2 n) {
	return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float randomRange (in vec2 seed, in float min, in float max) {
	return min + random2d(seed) * (max - min);
}

// return 1 if v inside 1d range
float insideRange(float v, float bottom, float top) {
   return step(bottom, v) - step(top, v);
}

void main() {
    float timeAdjusted = floor(time * glitchSpeed * 60.0);
    vec2 uv = vertTexCoord.xy;

    // copy orig
    vec3 color = texture2D(texture, uv).rgb;
    vec3 glitchColor = texture2D(texture, uv).rgb;

    // randomly offset slices horizontally
    float maxOffset = amp/2.0;
    for (float i = 0.0; i < numSlices * amp; i += 1.0) {
        float sliceY = random2d(vec2(timeAdjusted , 2345.0 + float(i)));
        float sliceH = random2d(vec2(timeAdjusted , 9035.0 + float(i))) * barSize;
        float hOffset = randomRange(vec2(timeAdjusted , 9625.0 + float(i)), -maxOffset, maxOffset);
        vec2 uvOff = uv;
        uvOff.x += hOffset;
        if (insideRange(uv.y, sliceY, fract(sliceY + sliceH)) == 1.0 ){
        	glitchColor = texture2D(texture, uvOff).rgb;
        }
    }

    // do slight offset on one entire channel
    if(colorSeparation == true) {
        float maxColOffset = amp/6.0;
        float rnd = random2d(vec2(timeAdjusted , 9545.0));
        vec2 colOffset = vec2(randomRange(vec2(timeAdjusted , 9545.0),-maxColOffset,maxColOffset),
                       randomRange(vec2(timeAdjusted , 7205.0),-maxColOffset,maxColOffset));
        if (rnd < 0.33){
            glitchColor.r = texture2D(texture, uv + colOffset).r;

        }else if (rnd < 0.66){
            glitchColor.g = texture2D(texture, uv + colOffset).g;

        } else{
            glitchColor.b = texture2D(texture, uv + colOffset).b;
        }
    }

    gl_FragColor = vec4(mix(color, glitchColor, crossfade), 1.);
}
