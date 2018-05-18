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

uniform float distance = 1.;

vec2 offsets[9] = vec2[](vec2(-1.0,-1.0), vec2(0.0,-1.0), vec2(1.0,-1.0), vec2(-1.0,0.0), vec2(0.0,0.0), vec2(1.0,0.0), vec2(-1.0,1.0), vec2(0.0,1.0), vec2(1.0,1.0));
vec4 neighbors[9];

void main() {
    vec4 color = texture2D(texture, vertTexCoord.xy);

    // output most common color
    if(color.a > 0. && color.a < 1.) {
      // find neighbor colors
      for (int i = 0; i < 9; i++) {
          neighbors[i] = texture2D(texture, vertTexCoord.xy + (offsets[i] * distance) * texOffset);
      }

      // count up black vs white neighbors
      int blackCount = 0;
      int whiteCount = 0;
      for(int i=0; i < 9; i++) {
        if(neighbors[i].a > 0.25) { // only pay attention to neighbor if it's not clear
          if(neighbors[i].r > 0.5) whiteCount++;
          if(neighbors[i].r <= 0.5) blackCount++;
        }
      }

      float finalColor = 1.;
      if(whiteCount < blackCount) finalColor = 0.;

      gl_FragColor = vec4(vec3(finalColor), 1.); // color.a
    } else {
      gl_FragColor = color;
    }
}
