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

uniform float distance = 1.5;

vec2 offsets[9] = vec2[](vec2(-1.0,-1.0), vec2(0.0,-1.0), vec2(1.0,-1.0), vec2(-1.0,0.0), vec2(0.0,0.0), vec2(1.0,0.0), vec2(-1.0,1.0), vec2(0.0,1.0), vec2(1.0,1.0));
float neighbors[9];

void main() {
    for (int i = 0; i < 9; i++) {
        neighbors[i] = texture2D( texture, vertTexCoord.xy + (offsets[i] * distance) * texOffset ).r;
    }

    // count up neighbors in totals count array
    int colorCount[9];
    for(int i=0; i < 9; i++) {
        colorCount[i] = 0;
        for(int j=0; j < 9; j++) {
            if(neighbors[i] == neighbors[j]) colorCount[i] += 1;
        }
    }

    // find most common color in kernel
    int maxColors = 0;
    int maxIndex = 0;
    for(int i=0; i < 9; i++) {
        if(colorCount[i] > maxColors) {
            maxColors = colorCount[i];
            maxIndex = i;
        }
    }

    // output most common color
    gl_FragColor = vec4(vec3(neighbors[maxIndex]), 1.0);
}







// // draw most common color in kernel (or original)
// if(mod(iGlobalTime, 1.) > 0.5) {
//   gl_FragColor = vec4(vec3(neighbors[4]), 1.0);
// } else {
//   gl_FragColor = vec4(vec3(neighbors[maxIndex]), 1.0);
// }
