// @author alteredq / http://alteredqualia.com/
//
// Film grain & scanlines shader
//
// - ported from HLSL to WebGL / GLSL
// http://www.truevision3d.com/forums/showcase/staticnoise_colorblackwhite_scanline_shaders-t18698.0.html
//
// Screen Space Static Postprocessor
//
// Produces an analogue noise overlay similar to a film grain / TV static
//
// Original implementation and noise algorithm
// Pat 'Hawthorne' Shearon
//
// Optimized scanlines + noise version with intensity scaling
// Georg 'Leviathan' Steinrohder
//
// This version is provided under a Creative Commons Attribution 3.0 License
// http://creativecommons.org/licenses/by/3.0/

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
uniform int grayscale;
uniform float nIntensity;
uniform float sIntensity;
uniform float sCount;

void main() {
    vec4 color = texture2D(texture, vertTexCoord.xy);
    float x = vertTexCoord.x * vertTexCoord.y * time *  1000.0;
    x = mod( x, 13.0 ) * mod( x, 123.0 );
    float dx = mod( x, 0.01 );
    vec3 cResult = color.rgb + color.rgb * clamp( 0.1 + dx * 100.0, 0.0, 1.0 );
    vec2 sc = vec2( sin( vertTexCoord.y * sCount ), cos( vertTexCoord.y * sCount ) );
    cResult += color.rgb * vec3( sc.x, sc.y, sc.x ) * sIntensity;
    cResult = color.rgb + clamp( nIntensity, 0.0,1.0 ) * ( cResult - color.rgb );
    if( grayscale == 1 ) {
        cResult = vec3( cResult.r * 0.3 + cResult.g * 0.59 + cResult.b * 0.11 );
    }
    gl_FragColor =  vec4( cResult, color.a );
}