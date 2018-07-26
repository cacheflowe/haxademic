
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float saturation = 1.;

// rgb/hsv conversion functions from: https://stackoverflow.com/questions/15095909/from-rgb-to-hsv-in-opengl-glsl

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
  // convert rgb
  vec4 textureColor = texture2D(texture, vertTexCoord.xy);
  vec3 hsv = rgb2hsv(textureColor.rgb);
  // mix with 1 being original, and remap to each side of the HSV 's' value (0-1+)
  // float originalSaturation = hsv.g;
  // float finalSaturation = (saturation < 0.5) ?
  //   mix(0., originalSaturation, saturation * 2.) :
  //   mix(originalSaturation, 1., -0.5 + saturation * 2.);
  // if(originalSaturation > 0.01)
    hsv.g *= saturation;
  // convert back to rgb & draw
  vec3 rgb = hsv2rgb(hsv);
  gl_FragColor = vec4(rgb.r, rgb.g, rgb.b, textureColor.a);
  // gl_FragColor = vec4(originalSaturation, originalSaturation, originalSaturation, textureColor.a);
}
