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
    float timeAdjusted = time * 1.2;									// adjust time
    // vec2 uv = (2. * fragCoord - iResolution.xy) / iResolution.y;	// center coordinates
    // vec2 uv = (2. * (vertTexCoord.xy - 0.5)) / vertTexCoord.y;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv *= 1.2;
    float rot = 2. * sin(uv.x + uv.y + timeAdjusted);						// oscillate rotation
    uv = fract(uv * (5. + sin(uv.x + uv.y + timeAdjusted))) - vec2(0.5);	// repeating grid
    uv *= mat2(cos(rot), sin(rot), -sin(rot), cos(rot));			// rotate
    float curRads = atan(uv.x, uv.y);								// get current pixel's angle to center
    curRads *= 6.;													// number of oscillations
    float radProgress = fract(curRads / TWO_PI);					// get progress through current segment
    radProgress += 0.14 * cos(radProgress * TWO_PI);				// add osc to shape of asterisk
    float osc = 0.25 + 0.15 * (sin(radProgress * TWO_PI));			// set radius of asterisk
    float radius = length(osc * vec2(sin(curRads), cos(curRads)));	// calc current radius
    float col = 0.;													// default black
    if(length(uv) > radius) col = 1.;								// outside asterisk is white
	  gl_FragColor = vec4(vec3(col),1.0);
}
