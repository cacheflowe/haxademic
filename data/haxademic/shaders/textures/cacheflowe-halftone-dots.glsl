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

uniform float rows = 5.;
uniform float radius = 0.25;
uniform float invert = 1.;
uniform float rotation = 0.;
uniform vec2 center = vec2(0,0);


vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
    return uv;
}

void main() {
    // get original coordinate, translate & rotate
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv += center;
    uv = rotateCoord(uv, rotation);
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
    float col = smoothstep(radius - aa, radius + aa, length(uvRepeat));
    if(invert == 1) col = 1. - col;
    gl_FragColor = vec4(vec3(col),1.0);
}
