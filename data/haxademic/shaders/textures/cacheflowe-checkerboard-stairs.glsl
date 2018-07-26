#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform float time;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

void main()
{
	// vec2 uv = (2. * fragCoord.xy - iResolution.xy) / iResolution.y;	 // center coordinates
  vec2 uv = vertTexCoord.xy - 0.5;
  uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
  float timeAdjusted = time * 2.;					  				 // adjust time
  float dist = length(uv) * 5.0;									 // adjust distance from center
  float cellSizeAdjust = dist/2. + dist * sin(PI + timeAdjusted);		     // adjust cell size from center
  float zoom = 4. * sin(timeAdjusted);									 // oscillate zoom
  uv *= 7. + cellSizeAdjust + zoom;                                // zoom out
	vec3 col = vec3(1. - fract(uv.y)); 							     // default fade to black
  if(floor(mod(uv.x, 2.)) == floor(mod(uv.y, 2.))) {				 // make checkerboard when cell indices are both even or both odd
      col = vec3(fract(uv.x)); 									 // use fract() to make the gradient along x & y
	}
  col = smoothstep(0.3,0.7, col);									 // smooth out the color
	gl_FragColor = vec4(col,1.0);
}
