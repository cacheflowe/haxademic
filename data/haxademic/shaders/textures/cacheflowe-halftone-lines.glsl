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

uniform float linesRows = 5.0;
uniform float thickness = 0.25;
uniform float invert = 0;

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
    return uv;
}

void main() {
    float rows = linesRows * 0.5;//linesRows + 3. * sin(time);
    float curThickness = 0.25 + 0.22 * cos(time);
    float curRotation = 0.8 * sin(time);
    // get original coordinate, translate & rotate
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv = rotateCoord(uv, curRotation);
    // create grid coords
    vec2 uvRepeat = fract(uv * rows);
    // adaptive antialiasing, draw, invert
    float aa = texOffset.y * 1.;
    float col = smoothstep(curThickness - aa, curThickness + aa, length(uvRepeat.y - 0.5));
    if(invert == 1) col = 1. - col;
    gl_FragColor = vec4(vec3(col),1.0);
}
