#ifdef GL_ES
precision highp float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;

uniform vec2 divider;

void main() {
	// get tex coord
	vec2 uv = vertTexCoord.xy;

	// fix uv to even pixels
	vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);     // resolution is actual pixel dimensions. texOffset is pixel dimensions converted to normalized value.
	if(mod(uv.x * resolution.x, 2.) > 1.) uv.x -= texOffset.x;  // if sampling from an odd pixel, sample from the even pixel
	if(mod(uv.y * resolution.y, 2.) > 1.) uv.y -= texOffset.y;

	// pixelate sampler
	uv = floor(uv * divider) / divider;
	gl_FragColor = texture2D(texture, uv);
}
