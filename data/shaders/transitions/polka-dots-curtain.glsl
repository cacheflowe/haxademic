// from: http://transitions.glsl.io/transition/791d0f058ae6a83e0c15

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D from;
uniform sampler2D to;
uniform float progress;

const float SQRT_2 = 1.414213562373;
uniform float dots = 20.0;
uniform vec2 center = vec2(0, 0);

void main() {
  vec2 p = vertTexCoord.xy;
  // p = vec2(p.x, 1.0-p.y);	// flipV while processing has issues with PImage
  float x = progress /2.0; 
  bool nextImage = distance(fract(p * dots), vec2(0.5, 0.5)) < (2.0 * x / distance(p, center)); 
  if(nextImage) gl_FragColor = texture2D(to, p);
  else gl_FragColor = texture2D(from, p);
}
