#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;

// camo config
uniform float time;
float colorSteps = 8.;
uniform float scale = 1.;

float rgbToGray(vec4 rgba) {
	  const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    return dot(rgba.xyz, W);
}

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
	  return uv;
}

float halftoneDots(vec2 uv, float rows, float curRadius, float curRotation, float invert) {
    // update layout params
    // vec2 curCenter = vec2(cos(iGlobalTime), sin(iGlobalTime));
    // get original coordinate, translate & rotate
    // uv += curCenter;
    uv = rotateCoord(uv, curRotation);
    // calc row index to offset x of every other row
    float rowIndex = floor(uv.y * rows);
    float oddEven = mod(rowIndex, 2.);
    // create grid coords
    vec2 uvRepeat = fract(uv * rows) - 0.5;
    if(oddEven == 1.) {							// offset x by half
        uvRepeat = fract(vec2(0.5, 0.) + uv * rows) - vec2(0.5, 0.5);
    }
    // adaptive antialiasing, draw, invert
		float aa = texOffset.y * 10.;
    float col = smoothstep(curRadius - aa, curRadius + aa, length(uvRepeat));
    if(invert == 1.) col = 1. - col;
    return col;
}

float halftoneLines(vec2 uv, float rows, float curThickness, float curRotation, float invert) {
    // get original coordinate, translate & rotate
    uv = rotateCoord(uv, curRotation);
    // create grid coords
    vec2 uvRepeat = fract(uv * rows);
    // adaptive antialiasing, draw, invert
    float aa = texOffset.y * 10.;
    float col = smoothstep(curThickness - aa, curThickness + aa, length(uvRepeat.y - 0.5));
    if(invert == 1.) col = 1. - col;
   	return col;
}

void main() {
    // shadertoy uv & color
	  // vec2 uvDraw = (2. * fragCoord - iResolution.xy) / iResolution.y;
    // vec2 uv = fragCoord.xy / iResolution.xy;
	  // vec4 color = texture(iChannel0, uv);

    // processing uv & color
    vec4 color = texture2D(texture, vertTexCoord.xy);
    vec2 uvDraw = vertTexCoord.xy - 0.5;
    uvDraw.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

    // posterize color & store index
    float luma = rgbToGray(color) * 1.;
    float lumaIndex = floor(luma * colorSteps);
   	float lumaFloor = lumaIndex / colorSteps;

		// posterize -> halftone gradient configurations
		float halftoneCol = 0.;
		if(lumaIndex == 0.) {
			halftoneCol = halftoneDots(uvDraw, scale * 50., 0.1, 0.2 + time, 1.);
		} else if(lumaIndex == 1.) {
			halftoneCol = halftoneLines(uvDraw, scale * 84., 0.08, 2. - time, 1.);
		} else if(lumaIndex == 2.) {
			halftoneCol = halftoneDots(uvDraw, scale * 120., 0.45, 0.8 + time, 0.);
		} else if(lumaIndex == 3.) {
			halftoneCol = halftoneDots(uvDraw, scale * 60., 0.37, 0.5 - time, 1.);
		} else if(lumaIndex == 4.) {
			halftoneCol = halftoneLines(uvDraw, scale * 40., 0.18, 2. + time, 0.);
		} else if(lumaIndex == 5.) {
			halftoneCol = halftoneDots(uvDraw, scale * 60., 0.34, 0.5 - time, 0.);
		} else if(lumaIndex == 6.) {
			halftoneCol = halftoneLines(uvDraw, scale * 84., 0.15, 2. + time, 0.);
		} else if(lumaIndex >= 7.) {
			halftoneCol = halftoneDots(uvDraw, scale * 50., 0.1, 0.2 - time, 0.);
		}
		gl_FragColor = vec4(vec3(halftoneCol),1.0);

}
