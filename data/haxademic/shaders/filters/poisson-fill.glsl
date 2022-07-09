// Poisson Filling for Processing
// Made possible with support from The Frank-Ratchye STUDIO For Creative Inquiry
// At Carnegie Mellon University. http://studioforcreativeinquiry.org/
// from: https://gist.github.com/LingDong-/09d4e65d0c320246b950206db1382092

// #define PROCESSING_COLOR_SHADER

uniform sampler2D unf;
uniform sampler2D fil;
uniform int w;
uniform int h;
uniform bool isup;

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

    int x = (j);
    int y = (i);

    vec4 acc = vec4(0.0,0.0,0.0,0.0);
    for (int dy = -2; dy <= 2; dy++) {
        for (int dx = -2; dx <= 2; dx++) {
            int nx = x + dx;
            int ny = y + dy;
            vec4 col = texture2D(unf, vec2((float(nx)) * step.x, (float(ny)) * step.y));

            acc.r += h1(dx+2) * h1(dy+2) * (col.r*floor(col.a+ab));
            acc.g += h1(dx+2) * h1(dy+2) * (col.g*floor(col.a+ab));
            acc.b += h1(dx+2) * h1(dy+2) * (col.b*floor(col.a+ab));
            acc.a += h1(dx+2) * h1(dy+2) * floor(col.a+ab);
        }
    }
    if (acc.a == 0.0){
      gl_FragColor = acc;
    }else{
      gl_FragColor = vec4(acc.r/acc.a,acc.g/acc.a,acc.b/acc.a,1.0);
    }
    
  }else{
    float h2 = 0.0270;

    vec4 acc = vec4(0.0,0.0,0.0,0.0);
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            int nx = j + dx;
            int ny = i + dy;


            vec4 col = texture2D(unf, 1.0*vec2((float(nx)-0.75) * step.x, (float(ny)-0.75) * step.y));

            acc.r += G(dx+1) * G(dy+1) * (col.r*floor(col.a+ab));
            acc.g += G(dx+1) * G(dy+1) * (col.g*floor(col.a+ab));
            acc.b += G(dx+1) * G(dy+1) * (col.b*floor(col.a+ab));
            acc.a += G(dx+1) * G(dy+1) * floor(col.a+ab);
        }
    }
    for (int dy = -2; dy <= 2; dy++) {
        for (int dx = -2; dx <= 2; dx++) {
            float nx = float(j) + float(dx)*1.0;
            float ny = float(i) + float(dy)*1.0;
  
            vec4 col = texture2D(fil, 1.0*vec2((float(nx)-0.75) * step.x, (float(ny)-0.75) * step.y));

            acc.r += h2 * h1(dx+2) * h1(dy+2) * (col.r*floor(col.a+ab));
            acc.g += h2 * h1(dx+2) * h1(dy+2) * (col.g*floor(col.a+ab));
            acc.b += h2 * h1(dx+2) * h1(dy+2) * (col.b*floor(col.a+ab));
            acc.a += h2 * h1(dx+2) * h1(dy+2) * floor(col.a+ab);
        }
    }
    if (acc.a == 0.0){
      gl_FragColor = acc;
    }else{
      gl_FragColor = vec4(acc.r/acc.a,acc.g/acc.a,acc.b/acc.a,1.0);
    }
  }
}

