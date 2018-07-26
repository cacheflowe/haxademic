// from: http://glslsandbox.com/e#36034.1

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

void main()
{
    vec2 p = vertTexCoord.xy - vec2(.5,.5);
    p *= 0.3; // zoom
    float amp = 0.025;
    p.x += sin(p.y*3.14 * 50.0) * amp * sin(p.y*3.14+time);;
    float col = sin(p.x * 3.14 * 10.0 + sin(time*1.0));
    gl_FragColor = vec4( vec3(col), 1.0 );
}
