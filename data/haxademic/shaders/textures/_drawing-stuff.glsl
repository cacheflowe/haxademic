// converted from: https://www.shadertoy.com/view/MtlGRn
// Reference: http://www.crytek.com/download/Sousa_Graphics_Gems_CryENGINE3.pdf on slide 36
// Implemented as GLSL example by Benjamin 'BeRo' Rosseaux - CC0
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

const int NUM_CIRCLES = 50;
const int NUM_COLORS = 75;

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time;
uniform vec2 mouse;
uniform float locations[NUM_CIRCLES];
uniform float colors[NUM_COLORS];



float distance2(vec2 point1, vec2 point2) {
	return abs(point1.x - point2.x) + abs(point1.y - point2.y);
}

vec4 bgColorGenerator() {
	int numberOfObjects = NUM_CIRCLES/2;
	int closestIndex = -1;
	float closestDistance = 1000000.0;
	
	for( int i = 0 ; i < numberOfObjects ;i++) {
		float myDistance = distance2(gl_FragCoord.xy, vec2(locations[i*2],locations[i*2+1]));
		if(myDistance < closestDistance) {
			closestDistance = myDistance;
			closestIndex = i;
		}
	}
	return vec4(colors[closestIndex*3],colors[closestIndex*3+1],colors[closestIndex*3+2],1.);
} 


vec4 circle(vec2 center, float radius, int colorIndex) {
    float stroke = radius * 0.1;
    float dist = distance(center, gl_FragCoord.xy);
	vec4 color;
	if(dist > radius + stroke / 2.0) {
    	color = bgColorGenerator();
    } else if(dist > radius - stroke / 2.0) {
    	color = vec4(colors[colorIndex], colors[colorIndex+1], colors[colorIndex+2],1);
    } else {
    	color = vec4(colors[colorIndex], colors[colorIndex+1], colors[colorIndex+2],0.5);
    }
	return color;
}


void main( void ) {
    vec4 color;
    vec4 color2;
    vec2 center = vec2(0, 0);
	// float x = gl_FragCoord.x / 32.0;
    // float y = gl_FragCoord.y / 32.0 + time;
    float radius = 30.0 + sin(time) * 20.0;
    int colorIndex = 0;
    for(int i=0; i < NUM_CIRCLES; i+=2) {
    	color += circle(vec2(locations[i], locations[i+1]), radius, colorIndex);
    	colorIndex += 3;
    }
    // color2 = circle(mouse * 2.0, radius);
        	color = bgColorGenerator();
    
    gl_FragColor = color;
}



















/*
void main( void ) {
    vec4 color;
    vec2 center = vec2(0, 0);
	// float x = gl_FragCoord.x / 32.0;
    // float y = gl_FragCoord.y / 32.0 + time;
    float radius = 100.0 + sin(time) * 100.0;
    float stroke = radius * 0.1;
    float dist = distance(mouse, gl_FragCoord.xy);
    
    if(dist > radius + stroke / 2.0) {
    	color = vec4(0.0,1.0,0.0,1.0);
    } else if(dist > radius - stroke / 2.0) {
    	color = vec4(1.0,0.0,0.0,1.0);
    } else {
    	color = vec4(1.0,1.0,0.0,1.0);
    }
    gl_FragColor = color;
}
*/