// from: https://www.shadertoy.com/view/4lSGRw

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float amp = 0.;
uniform float separation = 0.;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;


vec2 computeUV( vec2 uv, float k, float kcube ){
  vec2 t = uv - .5;
  float r2 = t.x * t.x + t.y * t.y;
  float f = 0.;

  if( kcube == 0.0){
    f = 1. + r2 * k;
  }else{
    f = 1. + r2 * ( k + kcube * sqrt( r2 ) );
  }

  vec2 nUv = f * t + .5;
  nUv.y = 1. - nUv.y;

  return nUv;
}

void main() {
  vec2 uv = vertTexCoord.xy;
  uv.y = 1. - uv.y; // flip y
  // uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

  float k = 1.0 * (amp * (.9 * separation));
  float kcube = .5 * (amp);
  float offset = .1 * (amp * (.5 * separation));

  float red = texture2D( texture, computeUV( uv, k + offset, kcube ) ).r;
  float green = texture2D( texture, computeUV( uv, k, kcube ) ).g;
  float blue = texture2D( texture, computeUV( uv, k - offset, kcube ) ).b;

  gl_FragColor = vec4( red, green,blue, 1. );
}
