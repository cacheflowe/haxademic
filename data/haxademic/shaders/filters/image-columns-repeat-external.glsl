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

uniform sampler2D inputImg;
uniform float columns = 6.;
uniform float scrollProgress = 0.5;
uniform float zoom = 1.;
uniform float padding = 0.15;
uniform float aspect = 1.5;
uniform float rotation = 0.;
uniform vec4 baseColor = vec4(1.);

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
		return uv;
}

void main() {
		// processing uv & color
		vec2 uv = vertTexCoord.xy - 0.5;
		uv.x *= texOffset.y / texOffset.x;		// Correct for base aspect ratio
		uv = rotateCoord(uv, rotation);
		uv.y *= aspect;												// apply image repeat aspect ratio

    // set vars from uniforms
    float columnWidth = 1. / columns;
		vec4 color = baseColor;

    // create grid coords & set color
    vec2 uvRepeat = fract(uv * zoom / aspect);

    // calc columns and scroll/repeat them
    float colIndex = floor(uvRepeat.x * columns) + 1.;
    float yStepRepeat = colIndex * scrollProgress;
    uvRepeat += vec2(0., yStepRepeat);
    uvRepeat = fract(uvRepeat);

    // add padding
    float paddingX = padding / aspect;
    float paddingY = paddingX * aspect;
    uvRepeat.y *= 1. + paddingY;
    uvRepeat.y -= paddingY;
    uvRepeat.x *= (columnWidth + paddingX * 1.) * columns;
    uvRepeat.x -= paddingX * colIndex;
    if(uvRepeat.y > 0. && uvRepeat.y < 1.) {
        if(uvRepeat.x < columnWidth * colIndex && uvRepeat.x > columnWidth * (colIndex - 1.)) {
          color = texture(inputImg, uvRepeat);
        }
    }

    // draw repeating texture
    gl_FragColor = color;
}
