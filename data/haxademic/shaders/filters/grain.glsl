// from: https://www.shadertoy.com/view/ldScWw

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;
uniform float crossfade = 0.1;

float grain (vec2 st) {
    return fract(sin(dot(st.xy, vec2(17.0,180.)))* 2500. + time);
}

void main() {
  vec2 uv = vertTexCoord.xy;
  vec4 color = texture2D(texture, uv);
  vec4 grainVal = vec4(vec3(grain(uv)), 1.);
  gl_FragColor = mix(color, grainVal, crossfade);
}
