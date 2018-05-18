// dilation.fs
// from: http://www.blitzbasic.com/Community/posts.php?topic=85263
// maximum of 3x3 kernel

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

vec2 offsets[9] = vec2[](vec2(-1.0,-1.0), vec2(0.0,-1.0), vec2(1.0,-1.0), vec2(-1.0,0.0), vec2(0.0,0.0), vec2(1.0,0.0), vec2(-1.0,1.0), vec2(0.0,1.0), vec2(1.0,1.0));


void main() {
    vec4 maxValue = vec4(0.0);
    for (int i = 0; i < 9; i++) {
        maxValue = max(texture2D( texture, vertTexCoord.xy + offsets[i] * texOffset ), maxValue);
    }
    gl_FragColor = maxValue;
}
