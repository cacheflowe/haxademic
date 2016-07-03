// sharpen.fs
// from: http://www.blitzbasic.com/Community/posts.php?topic=85263
// 3x3 sharpen kernel

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

//   -1 -1 -1
//   -1  9 -1
//   -1 -1 -1

uniform float sharpness = 1.0;

vec2 offsets[9] = vec2[](vec2(-1.0,-1.0), vec2(0.0,-1.0), vec2(1.0,-1.0), vec2(-1.0,0.0), vec2(0.0,0.0), vec2(1.0,0.0), vec2(-1.0,1.0), vec2(0.0,1.0), vec2(1.0,1.0));
vec4 neighbor[9];

void main() {
    for (int i = 0; i < 9; i++) {
        neighbor[i] = texture2D( texture, vertTexCoord.xy + (offsets[i] * sharpness) * texOffset );
    }

    gl_FragColor = (neighbor[4] * 9.0) - 
                    (neighbor[0] + neighbor[1] + neighbor[2] + 
                     neighbor[3] + neighbor[5] + 
                     neighbor[6] + neighbor[7] + neighbor[8]);
}
