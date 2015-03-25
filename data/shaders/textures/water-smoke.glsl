// converted from: http://glsl.heroku.com/e#17620.0
// water turbulence effect by joltz0r 2013-07-04, improved 2013-07-07
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

//	vec2 uv = vertTexCoord.xy - vec2(.5,.5);
//  vec2 position = vertTexCoord.xy + vec2(.5,.5);
//varying vec2 surfacePosition;


#define MAX_ITER 25
void main( void ) {
    
//	vec2 p = surfacePosition*4.0;
    vec2 p = vertTexCoord.xy * 4.0;
	vec2 i = p;
	float c = 0.0;
	float inten = 1.0;
    
	for (int n = 0; n < MAX_ITER; n++) {
		float t = time * 0.04 * (1.0 - (1.0 / float(n+1)));
		i = p + vec2(
                     cos(t - i.x) + sin(t + i.y),
                     sin(t - i.y) + cos(t + i.x)
                     );
		c += 1.0/length(vec2(
                             1.0 / (sin(i.x+t)/inten),
                             1.0 / (cos(i.y+t)/inten)
                             )
                        );
	}
	c /= float(MAX_ITER);
	
	gl_FragColor = vec4(2.5 * vec3(pow(c, 1.5)), 1.0);
}