// originally from: https://www.shadertoy.com/view/MtcXDH

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float aperture;

const float PI = 3.1415926535;

void mainImage( out vec4 o, vec2 I )
{
    vec2 p = (-iResolution.xy + 2.0*I)/iResolution.y;		// normalized coordinates (-1 to 1 vertically)
    float zoom = 0.2 + 0.05 * sin(iGlobalTime);
    p *= zoom;
    float wrap = 20. * sin(iGlobalTime);
    o = texture(iChannel0, 0.5 + p / sqrt(1. - wrap * dot(p,p)));
}
