#ifdef GL_ES
precision highp float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time = 0.;

// uniform sampler2D repeatImg;
uniform float rows = 4.0;
uniform float padding = 0.0;
uniform float aspect = 1.5;
uniform float rotation = 0.;
uniform float offset = 0.5;
uniform float flipY = 0.0;
uniform vec4 baseColor = vec4(1.);


vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
		return uv;
}

void main() {
		// processing uv & color
		vec2 uv = vertTexCoord.xy - 0.5;
		uv.x *= texOffset.y / texOffset.x;		// Correct for base aspect ratio
    if(flipY == 1.) uv.y *= -1;
		uv = rotateCoord(uv, rotation);
		uv.y *= aspect;												// apply image repeat aspect ratio

    vec4 origColor = texture2D(texture, uv);
		vec4 color = baseColor;

    // calc row index to offset x of every other row
    float rowIndex = floor(uv.y * rows);
    float oddEven = mod(rowIndex, 2.);

    // create grid coords & set color
    vec2 uvRepeat = fract(uv * rows);
    if(oddEven >= 1.) {
        uvRepeat = fract(vec2(offset, 0.) + uv * rows);
    } else {
        uvRepeat = fract(vec2(-offset, 0.) + uv * rows);
    }

    // add padding and only draw once per cell
    float paddingAspect = aspect * padding;
    uvRepeat *= 1. + vec2(paddingAspect, paddingAspect) * 2.;
    if(uvRepeat.x >= 0. && uvRepeat.x <= 1. && uvRepeat.y >= 0. && uvRepeat.y <= 1.) {
      color = texture2D(texture, uvRepeat);
    }

    // draw repeating texture
    gl_FragColor = color;
}
