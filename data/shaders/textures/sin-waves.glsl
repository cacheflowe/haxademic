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

#define LINES 4.0
#define BRIGHTNESS 0.9

const vec3 ORANGE = vec3(1.4, 0.8, 0.4);
const vec3 BLUE = vec3(0.5, 0.9, 1.3);
const vec3 GREEN = vec3(0.9, 1.4, 0.4);
const vec3 RED = vec3(1.8, 0.4, 0.3);

void main() {
    float x, y, xpos, ypos;
    float t = time * 30.0;
    vec3 c = vec3(0.0);

    vec2 p = vertTexCoord.xy;   //  - vec2(.5,.5)
    p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    xpos = p.x;
    ypos = p.y;

    x = xpos;
    for (float i = 0.0; i < LINES; i += 1.0) {
        for(float j = 0.0; j < 2.0; j += 1.0){
            y = ypos
            + (0.30 * sin(x * 2.000 +( i * 1.5 + j) * 0.2 + t * 0.050)
               + 0.300 * cos(x * 6.350 + (i  + j) * 0.2 + t * 0.050 * j)
               + 0.024 * sin(x * 12.35 + ( i + j * 4.0 ) * 0.8 + t * 0.034 * (8.0 *  j))
               + 0.5);

            c += vec3(1.0 - pow(clamp(abs(1.0 - y) * 5.0, 0.0,1.0), 0.25));
        }
    }

    c *= mix(
             mix(ORANGE, BLUE, xpos)
             , mix(GREEN, RED, xpos)
             ,(sin(t * 0.02) + 1.0) * 0.45
             ) * BRIGHTNESS;

    gl_FragColor = vec4(c, 1.0);
}
