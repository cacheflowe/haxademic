// from: http://glsl.heroku.com/e#15216.3
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
uniform int mode;



//高さを1.0として歪まないように正規化
vec2 ToPolarCoord(vec2 xy) {
	xy -= vec2(0.5, 0.5) ;
	xy.x *= texOffset.y / texOffset.x ; // fix aspect ratio
	float theta = atan(xy.y, xy.x);
	float r = length(xy);
	return vec2(theta, r);
}

void main( void ) {
	float pi = atan(-1.0) * 4.0;

    vec2 position = vertTexCoord.xy;// - vec2(.5,.5);
	vec2 positionPolar = ToPolarCoord(position) ;

	float color = 0.0 ;
	float circleGap = 0.10 ;
	int splitNum = 3 ;

	int num = int(positionPolar.y / circleGap) ;
    float timePlus = time * 3.0;
	float rotationOffset = float(num) * circleGap ;
	float rotationSpeed = (mod(float(num), 2.0) == 0.0 ? 1.0 : -1.0) / 4.0 / float(num + 1) ;

	if(mod(abs(positionPolar.x / pi + rotationOffset + rotationSpeed * timePlus), 2.0 / float(splitNum)) < 0.45 ) {
		color += mod(positionPolar.y, circleGap) > (circleGap / 1.6) ? 1.0 : 0.0 ;
	}

	gl_FragColor = vec4( vec3( color, color, color ), 1.0 );
}
