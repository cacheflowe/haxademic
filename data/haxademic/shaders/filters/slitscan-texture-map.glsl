#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

// uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D map;
uniform sampler2D frame_0;
uniform sampler2D frame_1;
uniform sampler2D frame_2;
uniform sampler2D frame_3;
uniform sampler2D frame_4;
uniform sampler2D frame_5;
uniform sampler2D frame_6;
uniform sampler2D frame_7;
uniform sampler2D frame_8;
uniform sampler2D frame_9;
uniform sampler2D frame_10;
uniform sampler2D frame_11;
uniform sampler2D frame_12;
uniform sampler2D frame_13;
uniform sampler2D frame_14;
uniform sampler2D frame_15;


float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
  // sample 
	vec2 p = vertTexCoord.xy;
  vec4 mapColor = texture2D(map, p);
  float mapAmp = rgbToGray(mapColor);

  // get posterization and map to crossfade across all images
  float idx = mapAmp * 15.;
  float crossfade = mod(idx, 1.);

  // crossfade across all images
  if(idx <= 1.) gl_FragColor = mix(texture2D(frame_0, p), texture2D(frame_1, p), crossfade);
  else if(idx <= 2.) gl_FragColor = mix(texture2D(frame_1, p), texture2D(frame_2, p), crossfade);
  else if(idx <= 3.) gl_FragColor = mix(texture2D(frame_2, p), texture2D(frame_3, p), crossfade);
  else if(idx <= 4.) gl_FragColor = mix(texture2D(frame_3, p), texture2D(frame_4, p), crossfade);
  else if(idx <= 5.) gl_FragColor = mix(texture2D(frame_4, p), texture2D(frame_5, p), crossfade);
  else if(idx <= 6.) gl_FragColor = mix(texture2D(frame_5, p), texture2D(frame_6, p), crossfade);
  else if(idx <= 7.) gl_FragColor = mix(texture2D(frame_6, p), texture2D(frame_7, p), crossfade);
  else if(idx <= 8.) gl_FragColor = mix(texture2D(frame_7, p), texture2D(frame_8, p), crossfade);
  else if(idx <= 9.) gl_FragColor = mix(texture2D(frame_8, p), texture2D(frame_9, p), crossfade);
  else if(idx <= 10.) gl_FragColor = mix(texture2D(frame_9, p), texture2D(frame_10, p), crossfade);
  else if(idx <= 11.) gl_FragColor = mix(texture2D(frame_10, p), texture2D(frame_11, p), crossfade);
  else if(idx <= 12.) gl_FragColor = mix(texture2D(frame_11, p), texture2D(frame_12, p), crossfade);
  else if(idx <= 13.) gl_FragColor = mix(texture2D(frame_12, p), texture2D(frame_13, p), crossfade);
  else if(idx <= 14.) gl_FragColor = mix(texture2D(frame_13, p), texture2D(frame_14, p), crossfade);
  else if(idx <= 15.) gl_FragColor = mix(texture2D(frame_14, p), texture2D(frame_15, p), crossfade);

  // gl_FragColor = texture2D(frame_0, p);
}
