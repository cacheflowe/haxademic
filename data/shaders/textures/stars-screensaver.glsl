// converted from: http://glsl.heroku.com/e#17716.0
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

//	vec2 uv = vertTexCoord.xy - vec2(.5,.5);

const float Tau = 6.2832;
const float speed = .02;
const float density = .03;
const float shape = .04;

float random( vec2 seed ) {
    return fract(sin(seed.x+seed.y*1e3)*1e5);
}

float Cell(vec2 coord) {
    vec2 cell = fract(coord) * vec2(.5,2.) - vec2(.0,.5);
    return (1.-length(cell*2.-1.))*step(random(floor(coord)),density)*2.;
}

void main( void ) {
    vec2 position = vertTexCoord.xy + vec2(.5,.5);
    vec2 p = (position - 1.0) * 50.;
    
    float a = fract(atan(p.x, p.y) / Tau);
    float d = length(p);
    vec2 coord = vec2(pow(d, shape), a)*256.;
    vec2 delta = vec2(-time*speed*256., .5);
    float c = 0.;
    
    for(int i=0; i < 7; i++) {
        coord += delta;
        c = max(c, Cell(coord));
    }
    float grey = c * d;
    gl_FragColor = vec4(grey);
    // gl_FragColor = vec4(0.1, c * d, 0.1 , 1.);
}