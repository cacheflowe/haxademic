// In the default shader we won't be able to access the shadowMap's depth anymore,
// just the color, so this function will pack the 16bit depth float into the first
// two 8bit channels of the rgba vector.

vec4 packDepth(float depth) {
  float depthFrac = fract(depth * 255.0);
  return vec4(depth - depthFrac / 255.0, depthFrac, 1.0, 1.0);
}

void main(void) {
  gl_FragColor = packDepth(gl_FragCoord.z);
}
