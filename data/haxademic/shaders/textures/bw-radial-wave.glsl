// from: http://glslsandbox.com/e#35835.0

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

#define pi 3.141592

void main() {
    vec2 uv = vertTexCoord.xy - vec2(.5,.5);
    uv.x *= texOffset.y/texOffset.x;
    uv *= 0.4;
    float vertices = 10.0 + 9.0 * sin(time);
    float a = atan(uv.x,uv.y)/pi/2.;
    float r = length(uv);
    float c = r*2.+sin(pi*a*2.*vertices)*.05+sin(pi*a*2.*12.+time*10.+r*100.)*.01+sin(pi*a*2.*12.+time*10.+r*1000.)*.01-time/20.;
    c=fract(c*10.)*(1.-r);

    gl_FragColor = vec4( vec3(1.0)*c, 1.0 );
}
