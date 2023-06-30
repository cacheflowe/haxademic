// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D uMaskTexture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

const vec3 bugYellow = vec3(200. / 255., 195. / 255., 72. / 255.);
const float bugYellowThresh = 0.25;
uniform float uTime = 0.;
uniform float uSmoothLow = 0.0;
uniform float uSmoothHigh = 1.0;
uniform float uAlphaMapLow = -0.3;
uniform float uAlphaMapHigh = 2.0;
uniform float uShowMask = 0.0;

float remap(float value, float low1, float high1, float low2, float high2) {
  return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
  vec2 vUv = vertTexCoord.xy;
  // vUv.y = 1. - vUv.y; // flip y

  vec2 offsetAnim = vec2(cos(uTime/2.) * 0.02, sin(uTime) * 0.02);
  vec4 colorScene = texture2D(texture, vUv + offsetAnim);
  vec4 colorMask = texture2D(uMaskTexture, vUv);
  vec4 maskAdjust = colorMask;

  // handle special black/yellow artifacts in map
  if(distance(colorMask.rgb, bugYellow) < bugYellowThresh) {
    maskAdjust.r = 0.0;
  }

  // calculate alpha from mask, with extra curves & remapping for feathering/threshold/sharpness
  float mapToAlpha = maskAdjust.r;
  mapToAlpha = remap(mapToAlpha, 0., 1., uAlphaMapLow, uAlphaMapHigh);
  mapToAlpha = clamp(mapToAlpha, 0., 1.);
  if(uSmoothLow > 0.001) mapToAlpha = smoothstep(uSmoothLow, uSmoothHigh, mapToAlpha);
  float finalAlpha = min(mapToAlpha, colorScene.a); // use mask alpha unless it's transparent

  // draw!
  vec4 finalColor = vec4(colorScene.rgb, finalAlpha);
  gl_FragColor.rgba = finalColor;

  // debug
  if(uShowMask > 0.) {
    gl_FragColor.rgba = mix(finalColor, colorMask, uShowMask);
  }

  // debug views
  // if(finalAlpha < 0.9) gl_FragColor.rgba = mix(colorScene, colorMask, 1. - finalAlpha);
  // gl_FragColor.rgba = colorScene;
  // gl_FragColor.rgba = colorMask;
}
