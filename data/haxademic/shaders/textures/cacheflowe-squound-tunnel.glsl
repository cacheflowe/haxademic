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

#define PI 3.141592653589793
#define TAU 6.283185307179586

float polygon(vec2 p, int vertices, float size) {
    float a = atan(p.x, p.y) + 0.2;
    float b = 6.28319 / float(vertices);
    return cos(floor(0.5 + a / b) * b - a) * length(p) - size;
}

void main(void)
{
    float timeAdjusted = time * 0.25;
    //////////////////////////////////////////////////////
    // Create tunnel coordinates (p) and remap to normal coordinates (uv)
    // Technique from @iq: https://www.shadertoy.com/view/Ms2SWW
	  // and a derivative:   https://www.shadertoy.com/view/Xd2SWD
    // vec2 p = (-texOffset.xy + 2.0*gl_FragCoord.xy)/texOffset.y;		// normalized coordinates (-1 to 1 vertically)
    vec2 p = vertTexCoord.xy - 0.5;
    p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // p *= 6.; 														// zoom out
    vec2 uvOrig = p;
    // added twist by me ------------
    float rotZ = 1. - 0.07 * sin(1. * sin(length(p * 1.5)));
    p *= mat2(cos(rotZ), sin(rotZ), -sin(rotZ), cos(rotZ));
	  //-------------------------------
    float a = atan(p.y,p.x);												// angle of each pixel to the center of the screen
    float rSquare = pow( pow(p.x*p.x,4.0) + pow(p.y*p.y,4.0), 1.0/8.0 );	// modified distance metric (http://en.wikipedia.org/wiki/Minkowski_distance)
    float rRound = length(p);
    float r = mix(rSquare, rRound, 0.5 + 0.5 * sin(timeAdjusted * 2.)); 			// interp between round & rect tunnels
    vec2 uv = vec2( 0.3/r + timeAdjusted, a/3.1415927 );							// index texture by (animated inverse) radious and angle
    //////////////////////////////////////////////////////

    // subdivide to grid
    uv += vec2(0., 0.25 * sin(timeAdjusted + uv.x * 2.));			// pre-warp
    vec2 uvDraw = fract(uv * 7. + 5. * sin(timeAdjusted)) - 0.5;	// create grid

    // alter polygon direction by pre-rotating coords
    float rot = PI/2.;
    uvDraw *= mat2(cos(rot), sin(rot), -sin(rot), cos(rot));

    // draw arrow
    // float antialias = 1.;//8./texOffset.y;
    vec2 rectPos = uvDraw;// + vec2(0., -0.08);
    // float col = smoothstep(antialias, 0., polygon(rectPos, 4, 0.12)); 	 // antialiased rect
    float col = smoothstep(0.25, 0.27, polygon(rectPos, 6, 0.01));
    // vec2 triPos = uvDraw + vec2(-0.047, 0.15);
    // // col = max(col, smoothstep(antialias, 0., polygon(triPos, 3, 0.12))); // antialiased triangle
    // col = max(col, smoothstep(0.25, 0.35, polygon(triPos, 3, 0.12))); // antialiased triangle

    // darker towards center, light towards outer
    col = col * r * 1.0;
    col += 0.15 * length(uvOrig);
    gl_FragColor = vec4(vec3(col), 1.);
}
