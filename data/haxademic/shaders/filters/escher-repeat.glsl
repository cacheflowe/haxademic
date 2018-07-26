// from: https://www.shadertoy.com/view/XdVXW3
// from: https://www.shadertoy.com/view/MsyXD3
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;

void main()
{
	/*
	vec2 uv = vertTexCoord.xy;
    vec2 z = texOffset.xy * 1.5 / vertTexCoord.xy;
    vec2 c = vertTexCoord.xy;
    c.xy = 0.75-abs(z-0.75);
	gl_FragColor = texture2D(texture,fract(z*exp2(ceil(-log2(min(c.y,c.x))))));
	*/	
	// vec2 uv = texOffset.xy * 1.5;
	vec2 z = vertTexCoord.xy;// / vertTexCoord.xy;
	// vec2 p = vertTexCoord.xy - 0.5;
    // vec4 color = texture2D(texture, p);
	gl_FragColor = texture2D(texture,fract(z*exp2(ceil(-log2(.5-abs(z.y-.5))))));
	
}

/*
void mainImage(out vec4 c,vec2 z)
{   
    z *= 1.5/iResolution.xy;
    c.xy = .75-abs(z-.75);
	c = texture2D(iChannel0,fract(z*exp2(ceil(-log2(min(c.y,c.x))))));
}
*/
/*
void mainImage(out vec4 c,vec2 z)
{   
    z /= iResolution.xy;
	c = texture2D(iChannel0,fract(z*exp2(ceil(-log2(.5-abs(z.y-.5))))));
}
*/