// from: http://glsl.heroku.com/e#13589.2
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
uniform vec2 mouse;
uniform vec2 resolution;

//original function
vec2 superShape2D(float m, float n1, float n2, float n3, float val)
{
    float r;
    float t1, t2;
    float a = 0.7 + 0.3 * sin(time/1000.0); //1.0;
    float b = 0.7 + 0.3 * cos(time/750.0); //1.0;

    t1 = cos(m * val / 4.0) / a;
    t1 = abs(t1);
    t1 = pow(t1, n2);

    t2 = sin(m * val / 4.0) / b;
    t2 = abs(t2);
    t2 = pow(t2, n3);

    r = pow(t1 + t2, 1.0 / n1);

    r = 1.0 / r;
    vec2 xy = (abs(r) == 0.0) ? vec2(0.0,0.0) : vec2(r * cos(val),r * sin(val));

    return xy;
}

vec4 superShape2D_Alt(  float M, float N1, float N2, float N3,
                      float Scale, float Width,
                      float PolarMix, float CartesianMix, float PolarCartesianMix,
                      vec3 Color
                      )
{
	vec2 position = vertTexCoord.xy - vec2(.5,.5); //( gl_FragCoord.xy / resolution ) * 2.0 - 1.0;
  position.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio


	float phi = atan(position.x, position.y) + time*1.;
	float r = distance(vec2(0.0), position) + Scale;

	float polar = mix(phi, r, PolarMix);
	float cartesian = mix(position.x, position.y, CartesianMix);
	float polarCartesian = mix(polar, cartesian, PolarCartesianMix);

	vec2 point = superShape2D(M,N1,N2,N3,polarCartesian);

	float dist = distance(position, point);
	float distSmooth = smoothstep(Width,0.0,dist);

	return vec4(vec3(distSmooth) * Color, 1.0);
}

//main code
void main( void )
{

	gl_FragColor = superShape2D_Alt( 2.0, 1.0, 3.4, 8.0,
                                    2.0, 3.0,
                                    13.0, 2.0, 2.0,
                                    vec3(1.0,1.0,1.0)
                                    );
}
