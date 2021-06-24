// shader based on ofxFlowTools opticalFlow shader by Matthias Oostrik
// modified by @cacheflowe

// The MIT License (MIT)
//
// Copyright (c) 2015 Matthias Oostrik ( www.matthiasoostrik.com )
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;

// samplers
uniform sampler2D tex0;
uniform sampler2D tex1;
uniform sampler2D texFlow;

uniform bool firstFrame = false;
uniform float uForce = 0.5;
uniform float uOffset = 8.;
uniform float uLambda = 0.012;
uniform float uThreshold = 0.2;
uniform float uDecayLerp = 1.;
uniform vec2 uInverse = vec2(-1., -1.);

float luma(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

void main()
{
	vec2 uv  = vertTexCoord.xy;

	vec2 pixelOffset = vec2(texOffset.x*uOffset, texOffset.y*uOffset);
	vec2 offX = vec2(pixelOffset.x,0.0);
	vec2 offY = vec2(0.0,pixelOffset.y);

	// difference calculation
	vec4 a = texture2D(tex0, uv);
	vec4 b = texture2D(tex1, uv);
	float texDiff = luma(a) - luma(b);

	// gradient calculation
	float gradX =  texture2D(tex0, uv + offX).r - texture2D(tex0, uv - offX).r;
		    gradX += texture2D(tex1, uv + offX).r - texture2D(tex1, uv - offX).r;

	float gradY =  texture2D(tex0, uv + offY).r - texture2D(tex0, uv - offY).r;
		    gradY += texture2D(tex1, uv + offY).r - texture2D(tex1, uv - offY).r;

	float gradMag = sqrt((gradX*gradX)+(gradY*gradY)+uLambda);

	float vx = texDiff * (gradX/gradMag);
	float vy = texDiff * (gradY/gradMag);

	vec2 flow = vec2(0.0);
	flow.x = -vx * uInverse.x;
	flow.y = -vy * uInverse.y;

	// apply treshold
	float strength = length(flow);
	if (strength * uThreshold > 0.0) {
		if (strength < uThreshold) {
			flow = vec2(0.0);
		}
		else {
			strength = (strength - uThreshold) / (1.0 - uThreshold);
			flow = normalize(flow) * vec2(strength);
		}
	}

	// apply force
	flow *= vec2(uForce);

	// if we're decaying the results...
	if(uDecayLerp < 1.) {
		// get previous frame of flow
		// subtract 0.5 - up/left is negative, right/down is positive
		vec4 prevFlow = texture2D(texFlow, uv);
		prevFlow.x -= 0.5;
		prevFlow.y -= 0.5;

		// lerp toward current flow calc if we're decaying...
		// otherwise use a quick constant lerp to the higher value
		if(abs(flow.x) < abs(prevFlow.x)) flow.x = mix(prevFlow.x, flow.x, uDecayLerp); 
		else 															flow.x = mix(prevFlow.x, flow.x, 0.3);
		if(abs(flow.y) < abs(prevFlow.y)) flow.y = mix(prevFlow.y, flow.y, uDecayLerp); 
		else 															flow.y = mix(prevFlow.y, flow.y, 0.3);
	}

	// add 0.5 as the resting state, which is mid-gray
	flow.xy += 0.5;
	gl_FragColor = vec4(flow.xy, 0.5, 1.0);

	// default to mid gray (resting state) on first frame
	if(firstFrame == true) {
		gl_FragColor = vec4(0.5, 0.5, 0.5, 1.0);
	}

	// debug draw ///////////
	// gl_FragColor = a;
	// gl_FragColor = vec4(flow.x);
}
