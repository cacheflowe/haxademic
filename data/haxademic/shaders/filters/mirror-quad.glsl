// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D textureDupe;              // uv multiplication gets weird and buggy on original texture
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform float zoom = 1.;

void main()
{
	vec2 uv = vertTexCoord.xy - 0.5;            // center coords
    uv *= 2. * zoom;						    // scale down. use 2 as base to make fit quad (4x)
    // uv = fract(abs(uv));					    // always positive values for p coords (old/basic style)
    uv = fract(uv - 0.5);						// repeat
    uv = abs((uv - 0.5) * 2.);		            // remap to center of repeated coords
    gl_FragColor = texture2D(textureDupe, uv);	// grab texture pixels
}

