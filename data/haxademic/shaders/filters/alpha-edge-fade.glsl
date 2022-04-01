// Somewhat of an antialias for the edges of opaque pixels

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float radiusCheck = 1.;

vec2 offsets[9] = vec2[](vec2(-1.0,-1.0), vec2(0.0,-1.0), vec2(1.0,-1.0), vec2(-1.0,0.0), vec2(0.0,0.0), vec2(1.0,0.0), vec2(-1.0,1.0), vec2(0.0,1.0), vec2(1.0,1.0));

float remap(float value, float low1, float high1, float low2, float high2) {
	return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
    vec4 colorOrig = texture2D(texture, vertTexCoord.xy);

    // get normalized neighbors' alpha
    float neighborsAlpha = 0.;
    for (int i = 0; i < 9; i++) {
        vec4 curNeighborColor = texture2D(texture, vertTexCoord.xy + offsets[i] * texOffset * radiusCheck);
        neighborsAlpha += curNeighborColor.a;
    }
    neighborsAlpha = neighborsAlpha / 9.;

    // fade alpha based on neighbors' alpha
    if(colorOrig.a > 0.1 && neighborsAlpha < colorOrig.a) {
        colorOrig.a = neighborsAlpha;
    }
    
    gl_FragColor = colorOrig;
}
