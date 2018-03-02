// https://github.com/v002/v002-Optical-Flow/blob/master/v002.GPUHSFlow.frag
//Andrew Benson - andrewb@cycling74.com
//2009
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

// texcoords
varying vec2 texcoord0;
varying vec2 texcoord1;

// samplers
uniform sampler2D tex0;
uniform sampler2D tex1;

//variables
uniform vec2 scale = vec2(2.3);
uniform vec2 offset = vec2(0.05);
uniform float lambda = 1.02;

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main() {
	vec2 texcoord0 = vertTexCoord.xy;
	vec2 texcoord1 = vertTexCoord.xy;
	vec4 a = texture2D(tex0, texcoord0);
	vec4 b = texture2D(tex1, texcoord1);
	vec2 x1 = vec2(offset.x,0.);
	vec2 y1 = vec2(0.,offset.y);

	//get the difference
	vec4 frameDiff = b-a;

	//calculate the gradient
	vec4 gradx = texture2D(tex1, texcoord1+x1)-texture2D(tex1, texcoord1-x1);
	gradx += texture2D(tex0, texcoord0+x1)-texture2D(tex0, texcoord0-x1);

	vec4 grady = texture2D(tex1, texcoord1+y1)-texture2D(tex1, texcoord1-y1);
	grady += texture2D(tex0, texcoord0+y1)-texture2D(tex0, texcoord0-y1);

	vec4 gradmag = sqrt((gradx*gradx)+(grady*grady)+vec4(lambda));

	vec4 vx = frameDiff*(gradx/gradmag);
	float vxd = rgbToGray(vx);//assumes greyscale
	// format output for flowrepos, out(-x,+x,-y,+y)
	vec2 xout = vec2(max(vxd,0.),abs(min(vxd,0.)))*scale.x;

	vec4 vy = frameDiff*(grady/gradmag);
	float vyd = rgbToGray(vy);
	//format output for flowrepos, out(-x,+x,-y,+y)
	vec2 yout = vec2(max(vyd,0.),abs(min(vyd,0.)))*scale.y;

//	gl_FragColor = vec4(yout.xy,yout.xy);

	gl_FragColor = clamp(vec4(xout.xy,yout.xy), 0.0, 1.0);
}
