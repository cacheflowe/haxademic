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
#define TWO_PI (PI * 2.)

// void main()
// {
//     float timeAdjusted = time / 10.;
//     vec2 uv = vertTexCoord.xy - 0.5;
//     uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
//     uv *= 0.6; // zoom
//     gl_FragColor = vec4(vec3(color),1.0);
// }

// triangle shape from: https://thebookofshaders.com/edit.php?log=160414041142

uniform int steps = 36;
uniform float brighten = 1.4;

float map(float value, float low1, float high1, float low2, float high2) {
   return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

float triangle(vec2 p, float size) {
    vec2 q = abs(p);
    return max(q.x * 0.866025 + p.y * 0.5, -p.y * 0.5) - size * 0.5;
}

float hexagon(vec2 p, float radius) {
    vec2 q = abs(p);
    return max(abs(q.y), q.x * 0.866025 + q.y * 0.5) - radius;
}

void main()
{
    float timeAdjusted = time * 2.;
    vec2 st = vertTexCoord.xy - 0.5;
    st.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    st *= 3.; // zoom
    // st -= vec2(0, 0.3); // offset overall y a bit for more "floor"

    // start white and head towards black as triangles shrink
    float col = 1.;
    float sizeStart = 5. + cos(timeAdjusted);
    float sizeEnd = 0.;
    float stepSize = sizeStart / float(steps);
    for(int i = 0; i < steps; i++) {
        float curStepSize = float(i) * stepSize;
        // float stepColor = map(curStepSize, sizeStart, sizeEnd, 1., 0.05);
        float stepColor = (mod(float(i), 2.) == 0.) ? 0. : 1.;
        float yCompensate = float(i) * -0.22; // triangle isn't centered, so we can offset for better concentricity
        vec2 stMoved = st + 0.2 * vec2(0, yCompensate + sin(float(i) * 0.25 + timeAdjusted * 3.)); // offset wobble y down the tunnel, 3x faster than main oscillation
        if(triangle(stMoved, curStepSize) > 0.) {
        	col = stepColor;
        }
    }
	  gl_FragColor = vec4(vec3(col * brighten), 1.0);
}
