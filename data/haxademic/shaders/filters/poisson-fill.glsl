// Poisson Filling for Processing
// Made possible with support from The Frank-Ratchye STUDIO For Creative Inquiry
// At Carnegie Mellon University. http://studioforcreativeinquiry.org/
// from: https://gist.github.com/LingDong-/09d4e65d0c320246b950206db1382092
// And then merged with Patricio's Lygia poisson fill shader by @cacheflowe
// https://github.com/patriciogonzalezvivo/lygia/blob/main/morphological/poissonFill.glsl
// #define PROCESSING_COLOR_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D unf;
uniform sampler2D fil;
uniform int w;
uniform int h;
uniform bool isup;

const float[] h1arr = float[](1.0334, 0.6836, 0.1507);
const float[] garr = float[](0.7753, 0.0312);
int absi(int x) {
  return ( (x < 0)? x * -1 : x );
}

float h1(int i){
  if (i == 0 || i == 4){
    return 0.1507;
  }
  if (i == 1 || i == 3){
    return 0.6836;
  }
  return 1.0334;
}

float G(int i){
  if (i == 0 || i == 2){
    return 0.0312;
  }
  return 0.7753;
}

void main() {
  float ab = 0.0;

  vec2 step = 1.0 / vec2(float(w),float(h));
  int i = int(gl_FragCoord.y+0.5);
  int j = int(gl_FragCoord.x+0.5);

  if (!isup){
    vec4 acc = vec4(0.0);
    for(int dy = -2; dy <= 2; dy++) {
      for(int dx = -2; dx <= 2; dx++) {
        vec2 uv = (gl_FragCoord.xy + vec2(dx, dy)) * step;
        if(uv.x <= 0.0 || uv.x >= 1.0 || uv.y <= 0.0 || uv.y >= 1.0)
          continue;
        vec4 col = texture2D(unf, uv);
        acc += col * h1arr[absi(dx)] * h1arr[absi(dy)];
      }
    }
    gl_FragColor = (acc.a == 0.0) ? acc : vec4(acc.rgb / acc.a, 1.0);
    
  } else {
    float h2 = 0.0270;

    vec4 acc = vec4(0.0);
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            vec2 uv = (gl_FragCoord.xy + vec2(dx, dy)) * step;
            vec4 col = texture2D(unf, uv);
            acc += col * garr[absi(dx)] * garr[absi(dy)];
        }
    }
    for (int dy = -2; dy <= 2; dy++) {
        for (int dx = -2; dx <= 2; dx++) {
            vec2 uv = (gl_FragCoord.xy + vec2(dx, dy)) * step;
            vec4 col = texture2D(fil, uv);
            acc += col * h2 * h1arr[absi(dx)] * h1arr[absi(dy)];
        }
    }
    gl_FragColor = (acc.a == 0.0) ? acc : vec4(acc.rgb / acc.a, 1.0);
  }
}

