// from: http://glslsandbox.com/e#18087.0
// by @Flexi23
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




float pi2_inv = 0.159154943091895335768883763372;

float border(vec2 uv, float thickness){
    uv = fract(uv - vec2(0.5));
    uv = min(uv, vec2(1.)-uv)*2.;
    return clamp(max(uv.x,uv.y)-1.+thickness,0.,1.)/thickness;;
}

vec2 spiralzoom(vec2 domain, vec2 center, float n, float spiral_factor, float zoom_factor, vec2 pos){
    vec2 uv = domain - center;
    float d = length(uv);
    return vec2( atan(uv.y, uv.x)*n*pi2_inv + log(d)*spiral_factor, -log(d)*zoom_factor) + pos;
}

void main( void ) {
    vec2 uv = vertTexCoord.xy - vec2(.5,.5);
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

    vec2 spiral_uv = spiralzoom(uv,vec2(0.),8.,-.5,1.8,vec2(0.5,0.5)*time*0.5);
    vec2 spiral_uv2 = spiralzoom(uv,vec2(0.),3.,.9,1.2,vec2(-0.5,0.5)*time*.8);
    vec2 spiral_uv3 = spiralzoom(uv,vec2(0.),5.,.75,4.0,-vec2(0.5,0.5)*time*.7);

    gl_FragColor = vec4(border(spiral_uv,0.9), border(spiral_uv2,0.9) ,border(spiral_uv3,0.9),1.);
}
