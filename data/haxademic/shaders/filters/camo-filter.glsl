#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time;

// swoosh helpers
uniform vec4 baseColor = vec4(1., 1., 1., 1.);
uniform sampler2D swooshImg;
uniform float aspectSwoosh = 1.5;
// text helpers
uniform float aspectText = 1.5;
uniform sampler2D textImg;
uniform float textColumns = 3.;
uniform float textRot = 0.;
uniform float textOffset = 0.5;

// camo config
uniform float colorSteps = 10.;
uniform float scale = 1.;

float rgbToGray(vec4 rgba) {
	  const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    return dot(rgba.xyz, W);
}

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
	  return uv;
}

float halftoneDots(vec2 texOffset, vec2 uv, float rows, float curRadius, float curRotation, float invert) {
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
    float aa = texOffset.y * 6.;
    float col = smoothstep(curRadius - aa, curRadius + aa, length(uvRepeat));
    if(invert == 1.) col = 1. - col;
    return col;
}

float halftoneLines(vec2 texOffset, vec2 uv, float rows, float curThickness, float curRotation, float invert) {
    // get original coordinate, translate & rotate
    uv = rotateCoord(uv, curRotation);
    // create grid coords
    vec2 uvRepeat = fract(uv * rows);
    // adaptive antialiasing, draw, invert
    float aa = texOffset.y * 1.;
    float col = smoothstep(curThickness - aa, curThickness + aa, length(uvRepeat.y - 0.5));
    if(invert == 1.) col = 1. - col;
   	return col;
}

float halftoneSwoosh(vec2 texOffset, vec2 uv, float rows, float padding, float curRotation, float invert) {
    // get original coordinate, translate & rotate
    uv = rotateCoord(uv, curRotation);
		// apply image repeat aspect ratio
		uv.y *= aspectSwoosh;

    // calc row index to offset x of every other row
    float rowIndex = floor(uv.y * rows);
    float oddEven = mod(rowIndex, 2.);

    // create grid coords & set color
		float offset = 0.25;
    vec2 uvRepeat = fract(uv * rows);
    if(oddEven >= 1.) {
        uvRepeat = fract(vec2(offset, 0.) + uv * rows);
    } else {
        uvRepeat = fract(vec2(-offset, 0.) + uv * rows);
    }

    // add padding and only draw once per cell
    uvRepeat *= 1. + padding * 2.;
    uvRepeat -= padding;
		// uvRepeat.x = 1. - uvRepeat.x;
		vec4 color = baseColor;
    if(uvRepeat.x >= 0. && uvRepeat.x <= 1. && uvRepeat.y >= 0. && uvRepeat.y <= 1.) color = texture(swooshImg, uvRepeat);

		// return color
		float col = rgbToGray(color);
    if(invert == 1.) col = 1. - col;
   	return col;
}

float halftoneText(vec2 texOffset, vec2 uv, float zoom, float padding, float progress, float curRotation, float invert) {
    // get original coordinate, translate & rotate
    uv = rotateCoord(uv, curRotation);
		// apply image repeat aspect ratio
		uv.y *= aspectText;

		// set vars from uniforms
		float columnWidth = 1. / textColumns;
		vec4 color = baseColor;

		// create grid coords & set color
		vec2 uvRepeat = fract(uv * zoom / aspectText);

		// calc columns and scroll/repeat them
		float colIndex = floor(uvRepeat.x * textColumns) + 1.;
		float yStepRepeat = colIndex * progress;
		uvRepeat += vec2(0., yStepRepeat);
		uvRepeat = fract(uvRepeat);

		// add padding
		float paddingX = padding / aspectText;
		float paddingY = paddingX * aspectText;
		uvRepeat.y *= 1. + paddingY;
		uvRepeat.y -= paddingY;
		uvRepeat.x *= (columnWidth + paddingX * 1.) * textColumns;
		uvRepeat.x -= paddingX * colIndex;
		if(uvRepeat.y > 0. && uvRepeat.y < 1.) {
				if(uvRepeat.x < columnWidth * colIndex && uvRepeat.x > columnWidth * (colIndex - 1.)) {
					color = texture(textImg, uvRepeat);
				}
		}

		// return color
		float col = rgbToGray(color);
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

    // draw different halftone patterns per color index
    float halftoneCol = 1.;
    if(lumaIndex == 0.) {
				halftoneCol = halftoneDots(texOffset, uvDraw, scale * 50., 0.1, 0.2 + time, 1.);
    } else if(lumaIndex == 1.) {
				halftoneCol = halftoneSwoosh(texOffset, uvDraw, scale * 24., 0.2, 0.9 + time, 1.);
    } else if(lumaIndex == 2.) {
				halftoneCol = halftoneText(texOffset, uvDraw, scale * 20., 0.05, 0.5, 1.4 + time, 1.);
    } else if(lumaIndex == 3.) {
				halftoneCol = halftoneLines(texOffset, uvDraw, scale * 84., 0.08, 2. - time, 1.);
    } else if(lumaIndex == 4.) {
				halftoneCol = halftoneLines(texOffset, uvDraw, scale * 84., 0.15, 2. + time, 1.);
		} else if(lumaIndex == 5.) {
				halftoneCol = halftoneDots(texOffset, uvDraw, scale * 60., 0.37, 0.5 - time, 1.);
  	} else if(lumaIndex == 6.) {
				halftoneCol = halftoneDots(texOffset, uvDraw, scale * 120., 0.45, 0.8 - time, 0.);
		} else if(lumaIndex == 7.) {
				halftoneCol = halftoneLines(texOffset, uvDraw, scale * 180., 0.25, 1. + time, 1.);
		} else if(lumaIndex == 8.) {
				halftoneCol = halftoneDots(texOffset, uvDraw, scale * 60., 0.34, 0.5 - time, 0.);
		} else if(lumaIndex == 9.) {
				halftoneCol = halftoneText(texOffset, uvDraw, scale * 25., 0.05, textOffset, textRot, 0.);
		} else if(lumaIndex == 10.) {
				halftoneCol = halftoneText(texOffset, uvDraw, scale * 30., 0.005, 0.5, -0.4 - time, 0.);  // 1./textColumns (progress)
		} else if(lumaIndex == 11.) {
				halftoneCol = halftoneSwoosh(texOffset, uvDraw, scale * 24., 0.2, 0.9 - time, 0.);
		} else if(lumaIndex >= 12.) {
				halftoneCol = halftoneDots(texOffset, uvDraw, scale * 50., 0.1, 0.2 + time, 0.);
    } else {
        // // fall back to posterized color
        // halftoneCol = lumaFloor;
				// // scratch that - set to base color
				// halftoneCol = rgbToGray(baseColor);
				halftoneCol = halftoneDots(texOffset, uvDraw, scale * 50., 0.1, 0.2 + time, 0.);
    }
    gl_FragColor = vec4(vec3(halftoneCol),1.0);

}
