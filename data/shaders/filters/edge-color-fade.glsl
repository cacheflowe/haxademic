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
uniform vec3 edgeColor;

float map(float value, float inMin, float inMax, float outMin, float outMax) {
  return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

void main() {
    vec4 color = texture2D( texture, vertTexCoord.xy );
    if(vertTexCoord.x < spreadX) {
    	color.rgba = vec4(
    		map(vertTexCoord.x, 0., spreadX, edgeColor.r, color.r ),
    		map(vertTexCoord.x, 0., spreadX, edgeColor.g, color.g ),
    		map(vertTexCoord.x, 0., spreadX, edgeColor.b, color.b ),
    		1.0
    	);
    }
	if(vertTexCoord.x > 1.0 - spreadX) {
		float upperSpreadX = 1.0 - spreadX;
    	color.rgba = vec4(
    		map(vertTexCoord.x, 1.0, upperSpreadX, edgeColor.r, color.r ),
    		map(vertTexCoord.x, 1.0, upperSpreadX, edgeColor.g, color.g ),
    		map(vertTexCoord.x, 1.0, upperSpreadX, edgeColor.b, color.b ),
    		1.0
    	);
    } 
	if(vertTexCoord.y < spreadY) {
    	color.rgba = vec4(
    		map(vertTexCoord.y, 0., spreadY, edgeColor.r, color.r ),
    		map(vertTexCoord.y, 0., spreadY, edgeColor.g, color.g ),
    		map(vertTexCoord.y, 0., spreadY, edgeColor.b, color.b ),
    		1.0
    	);
    } 
	if(vertTexCoord.y > 1.0 - spreadY) {
		float upperSpreadY = 1.0 - spreadY;
    	color.rgba = vec4(
    		map(vertTexCoord.y, 1.0, upperSpreadY, edgeColor.r, color.r ),
    		map(vertTexCoord.y, 1.0, upperSpreadY, edgeColor.g, color.g ),
    		map(vertTexCoord.y, 1.0, upperSpreadY, edgeColor.b, color.b ),
    		1.0
    	);
    }
    color.rgba = vec4(color.rgb, 1.0);
    
    gl_FragColor = color;
}
