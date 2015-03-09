// Radial sin /cos displacement
// Ported from felixturner (http://airtight.cc/), which was ported from http://uglyhack.appspot.com/videofx/

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
uniform float speed;
uniform float strength;
uniform float size;

void main() {
    vec2 uv = vertTexCoord.xy; // hmm.
    vec2 p = vertTexCoord.xy;
    gl_FragColor = texture2D(texture, uv + strength * vec2(cos(time*speed+length(p*size)), sin(time*speed+length(p*size))));
}
