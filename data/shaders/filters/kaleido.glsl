#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float sides;
uniform float angle;

void main() {
    vec2 p = vertTexCoord.xy - 0.5;
    p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    float r = length(p);
    float a = atan(p.y, p.x) + angle;
    float tau = 2. * 3.1416 ;
    a = mod(a, tau/sides);
    a = abs(a - tau/sides/2.) ;
    p = r * vec2(cos(a), sin(a));
    vec4 color = texture2D(texture, p + 0.5);
    gl_FragColor = color;
}
