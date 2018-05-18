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
#define TWO_PI 6.28318530718

float saw(float rads) {
    rads += PI * 0.5; // sync oscillation up with sin()
    float percent = mod(rads, PI) / PI;
    float dir = sign(sin(rads));
    return dir * (2. * percent  - 1.);
}

void main()
{
    // set time & centered position
    float timeAdjusted = time + 10.;
    timeAdjusted *= 0.5;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv *= 4.; // zoom out
	  // calc additive distance from control points
    float dist = 0.;
    for(int ii = 1; ii < 5; ii++) {
        float i = float(ii);
        float rads = 1.75 * sin(timeAdjusted / 1./i);	// oscillate control point radius
        float ctrlX = sin(sin(uv.x / timeAdjusted) + sin(uv.y + timeAdjusted * i) + cos(timeAdjusted + timeAdjusted) * sin(timeAdjusted * i));
        float ctrlY = sin(sin(uv.y + timeAdjusted) + sin(uv.x + timeAdjusted / i) + cos(timeAdjusted + timeAdjusted) * cos(timeAdjusted * i));
        vec2 ctrlPoint = rads + vec2(ctrlX, ctrlY);
      	dist += (10. + 7. * sin(timeAdjusted * 0.5)) * distance(uv, ctrlPoint);
    }
    // oscillate color components by distance factor. smoothstep for contrast boost
    vec3 col = vec3(
        smoothstep(0.1, 0.8, abs(sin(timeAdjusted + dist * 0.11)) * 0.5 + 0.2),
        smoothstep(0.1, 0.8, abs(cos(timeAdjusted + dist * 0.22)) * 0.37 + 0.4),
        smoothstep(0.1, 0.8, abs(sin(timeAdjusted + dist * 0.33)) * 0.15 + 0.4)
    );
    float color = min(min(col.r, col.g), col.b);
    // color *= 2.; // brighten up
    color = smoothstep(0.3, 0.5, color);
	  // vignette outside of center
    // float vignetteInner = 0.75;
    // float vignetteDarkness = 0.4;
    // col -= smoothstep(0., 0.7, max(0., length(uv) - vignetteInner) * vignetteDarkness);
    gl_FragColor = vec4(vec3(color), 1.0);
}
