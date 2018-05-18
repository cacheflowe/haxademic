// converted from: https://www.shadertoy.com/view/MtlGRn
// Reference: http://www.crytek.com/download/Sousa_Graphics_Gems_CryENGINE3.pdf on slide 36
// Implemented as GLSL example by Benjamin 'BeRo' Rosseaux - CC0
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

void main( void ) {
    vec4 color;
    
    // no scroll
//    int x = int(gl_FragCoord.x) / 32;
//    int y = int(gl_FragCoord.y) / 32;
//    int sum = x + y;

    // scroll
    float x = gl_FragCoord.x / 32.0;
    float y = gl_FragCoord.y / 32.0 + time;
    int sum = int(x) + int(y);

    
    if (int(mod(float(sum), float(2))) == 0)
        color = vec4(1.0,1.0,1.0,1.0);
    else
        color = vec4(0.0,0.0,0.0,1.0);
    
    gl_FragColor = color;
}
