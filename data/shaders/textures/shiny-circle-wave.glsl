// from https://www.shadertoy.com/view/ltBXRc
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
uniform vec2 resolution;
uniform vec2 mouse;


mat2 rotate2d(float angle){
    return mat2(cos(angle),-sin(angle),
                sin(angle),cos(angle));
}

float variation(vec2 v1, vec2 v2, float strength, float speed) {
	return sin(
        dot(normalize(v1), normalize(v2)) * strength + time * speed
    ) / 100.0;
}

vec3 paintCircle (vec2 uv, vec2 center, float rad, float width) {

    vec2 diff = center-uv;
    float len = length(diff);

    len += variation(diff, vec2(0.0, 1.0), 5.0, 2.0);
    len -= variation(diff, vec2(1.0, 0.0), 5.0, 2.0);

    float circle = smoothstep(rad-width, rad, len) - smoothstep(rad, rad+width, len);
    return vec3(circle);
}


void main()
{
    vec2 uv = vertTexCoord.xy - 0.5;// - vec2(.5,.5);
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

    //uv.x *= 1.5;
    //uv.x -= 0.25;

    vec3 color;
    float radius = 0.35;
    vec2 center = vec2(0);


    //paint color circle
    color = paintCircle(uv, center, radius, 0.1);

    //color with gradient
    vec2 v = rotate2d(time) * uv;
    color *= vec3(v.x, v.y, 0.7-v.y*v.x);

    //paint white circle
    color += paintCircle(uv, center, radius, 0.01);


	gl_FragColor = vec4(color, 1.0);
}
