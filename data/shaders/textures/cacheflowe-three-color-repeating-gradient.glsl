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

#define PI     3.14159265358

uniform vec3 color1 = vec3(0.8, 0.2, 0.2);
uniform vec3 color2 = vec3(0.0, 0.0, 0.0);
uniform vec3 color3 = vec3(1.0, 1.0, 1.0);
float stops = 3.;
uniform float zoom = 1.;
uniform float scrollY = 0.;
uniform float oscFreq = 0.;
uniform float oscAmp = 0.;
uniform float fade = 0.;

void main() {
    vec2 uv = vertTexCoord.xy;
        
    // make it wave, just for fun
    uv.y += oscAmp * sin(uv.x * oscFreq);

    // animate to test repeat
    uv.y *= zoom;			// zoom
    uv.y -= scrollY;	    // scroll
    uv = fract(uv);			// repeat
    
    // fade colors
    float stopSize = 1. / stops;
    vec3 col = vec3(0.);
    float smoothSLow = 0. + fade * 0.5;
    float smoothSHigh = 1. - fade * 0.5;
    if(uv.y < stopSize) {
    	col = mix(color1, color2, smoothstep(smoothSLow, smoothSHigh, uv.y * stops)); 
    } else if(uv.y < stopSize * 2.) {
    	col = mix(color2, color3, smoothstep(smoothSLow, smoothSHigh, (uv.y - stopSize) * stops));
    } else {
    	col = mix(color3, color1, smoothstep(smoothSLow, smoothSHigh, (uv.y - stopSize * 2.) * stops)); 
    }
    
    // draw
    gl_FragColor = vec4(col,1.0);
}
