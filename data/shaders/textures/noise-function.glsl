// from: http://glsl.heroku.com/e#13450.0
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
uniform vec2 resolution;


float noise(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    vec2 p = vertTexCoord.xy;    
    float n = noise(vec2(p.x*cos(time/10000.0),p.y*sin(time/10000.0))); 
    gl_FragColor = vec4(n, n, n, 1.0);
}
