#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float spreadX;
uniform float spreadY;

void main() {
    vec4 color = texture2D( texture, vertTexCoord.xy );
    if(vertTexCoord.x < spreadX) {
    	color.rgb *= smoothstep( 0.0, spreadX, vertTexCoord.x );
    } 
	if(vertTexCoord.x > 1.0 - spreadX) {
    	color.rgb *= smoothstep( 1.0, 1.0 - spreadX, vertTexCoord.x );
    } 
	if(vertTexCoord.y < spreadY) {
    	color.rgb *= smoothstep( 0.0, spreadY, vertTexCoord.y );
    } 
	if(vertTexCoord.y > 1.0 - spreadY) {
    	color.rgb *= smoothstep( 1.0, 1.0 - spreadY, vertTexCoord.y );
    }
    gl_FragColor = color;
}
