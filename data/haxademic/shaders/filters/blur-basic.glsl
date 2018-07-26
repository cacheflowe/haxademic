// blur.fs
// from: http://www.blitzbasic.com/Community/posts.php?topic=85263
// blur (low-pass) 3x3 kernel

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
    vec4 neighbor[9];
    for (int i = 0; i < 9; i++) {
        neighbor[i] = texture2D( texture, vertTexCoord.xy + offsets[i] * texOffset );
    }

//   1 2 1
//   2 1 2   / 13
//   1 2 1

    gl_FragColor = (neighbor[0] + (2.0*neighbor[1]) + neighbor[2] + 
                    (2.0*neighbor[3]) + neighbor[4] + (2.0*neighbor[5]) + 
                    neighbor[6] + (2.0*neighbor[7]) + neighbor[8]) / 13.0;
}
