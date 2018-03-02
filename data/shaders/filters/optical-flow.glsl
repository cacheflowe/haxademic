// Ported from: Andrew Benson - andrewb@cycling74.com - 2009
// https://github.com/v002/v002-Optical-Flow/blob/master/v002.GPUHSFlow.frag
// output modifications by Cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

// samplers
uniform sampler2D tex0;
uniform sampler2D tex1;

//variables
uniform vec2 scale = vec2(2.3);
uniform vec2 offset = vec2(0.05);
uniform float lambda = 1.02;

float TWO_PI = radians(360.);
float PI = radians(180.);

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

float rgbToFloat(vec3 color) {
  return (color.r + color.g + color.b) / 3.;
}

void main() {

	vec2 uv0 = vertTexCoord.xy;
	vec2 uv1 = vertTexCoord.xy;
	vec4 a = texture2D(tex0, uv0);
	vec4 b = texture2D(tex1, uv1);
	vec2 offsetX = vec2(offset.x,0.);
	vec2 offsetY = vec2(0.,offset.y);

	// get the color difference between frames
	vec4 frameDiff = b - a;

	// calculate the gradient on each axis
	vec4 gradientX = texture2D(tex1, uv1 + offsetX) - texture2D(tex1, uv1 - offsetX);
	gradientX += texture2D(tex0, uv0 + offsetX) - texture2D(tex0, uv0 - offsetX);
	vec4 gradientY = texture2D(tex1, uv1 + offsetY) - texture2D(tex1, uv1 - offsetY);
	gradientY += texture2D(tex0, uv0 + offsetY) - texture2D(tex0, uv0 - offsetY);

	// calc directional magnitude
	vec4 gradientMagnitude = sqrt((gradientX * gradientX) + (gradientY * gradientY) + vec4(lambda));

	//
	vec4 vx = frameDiff * (gradientX / gradientMagnitude);
	float vxd = rgbToGray(vx); // assumes greyscale
	vec2 xout = vec2(max(vxd,0.),abs(min(vxd,0.)))*scale.x; // format output for flowrepos, out(-x,+x,-y,+y)

	vec4 vy = frameDiff * (gradientY / gradientMagnitude);
	float vyd = rgbToGray(vy);
	vec2 yout = vec2(max(vyd,0.),abs(min(vyd,0.)))*scale.y; // format output for flowrepos, out(-x,+x,-y,+y)

	// get rotation & strength
	float dir = atan(rgbToFloat(vy.rgb), rgbToFloat(vx.rgb));
	float rot = (PI + dir) / TWO_PI;	// normalize rotation to 0-1
	float amp = abs(rgbToFloat(vx.rgb) + rgbToFloat(vy.rgb));

	// draw to buffer
	// gl_FragColor = clamp(vec4(xout.xy,yout.xy), 0.0, 1.0);
	gl_FragColor = vec4(rot, amp, 0., 1.);
}
