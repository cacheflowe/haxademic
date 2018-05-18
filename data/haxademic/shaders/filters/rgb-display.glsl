// from: https://www.shadertoy.com/view/4dX3DM

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

int CELL_SIZE = 6;
float CELL_SIZE_FLOAT = float(CELL_SIZE);
int RED_COLUMNS = int(CELL_SIZE_FLOAT/3.);
int GREEN_COLUMNS = CELL_SIZE-RED_COLUMNS;

void main()
{
    
	vec2 p = floor(gl_FragCoord.xy / CELL_SIZE_FLOAT)*CELL_SIZE_FLOAT;
	int offsetx = int(mod(gl_FragCoord.x,CELL_SIZE_FLOAT));
	int offsety = int(mod(gl_FragCoord.y,CELL_SIZE_FLOAT));
    
	vec4 sum = texture2D(texture, p / texOffset.xy);
	
	gl_FragColor = vec4(0.,0.,0.,1.);
	if (offsety < CELL_SIZE-1) {
		if (offsetx < RED_COLUMNS) gl_FragColor.r = sum.r;
		else if (offsetx < GREEN_COLUMNS) gl_FragColor.g = sum.g;
		else gl_FragColor.b = sum.b;
	}
	
}