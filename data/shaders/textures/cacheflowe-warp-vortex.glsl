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

void main()
{
    float timeAdjust = time / 2.;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    float rads = atan(uv.x, uv.y);                   				// get radians to center
    float dist = length(uv);										// store distance to center
    float spinAmp = 4.;												// set spin amplitude
    float spinFreq = 2. + sin(timeAdjust) * 0.5;							// set spin frequency
    rads += sin(timeAdjust + dist * spinFreq) * spinAmp;					// wave based on distance + time
    float radialStripes = 10.;										// break the circle up
    float col = 0.5 + 0.5 * sin(rads * radialStripes);				// oscillate color around the circle
    col = smoothstep(0.5,0.6, col);									// remap color w/smoothstep to remove blurriness
    //col -= dist / 2.;												// vignette - reduce color w/distance
    gl_FragColor = vec4(vec3(col), 1.);
}
