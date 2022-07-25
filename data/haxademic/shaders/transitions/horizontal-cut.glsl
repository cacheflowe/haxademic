// cacheflowe original

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D from;
uniform sampler2D to;
uniform float progress;

void main() {
    vec2 p = vertTexCoord.xy;
    vec4 a = texture2D(from, p);
    vec4 b = texture2D(to, p);
    vec4 finalColor = a;
    if(p.x > progress) finalColor = b;
    gl_FragColor = finalColor;
}
