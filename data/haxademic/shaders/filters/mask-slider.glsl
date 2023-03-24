// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec2 offset = vec2(0.);

void main() {
    vec2 uv = vertTexCoord.xy;
    uv += offset;
    vec4 curColor = texture2D(texture, uv);
    gl_FragColor = curColor;
    if(uv.x < 0. || uv.y < 0. || uv.x > 1. || uv.y > 1.) {
        gl_FragColor = vec4(0.);
    }
}
