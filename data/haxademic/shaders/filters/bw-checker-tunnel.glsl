// from: https://www.shadertoy.com/view/MdfGDf
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

#define M_PI 3.1415926535897932384626433832795

vec3 path( float timer )
{
	return vec3( 0.4*cos(timer*0.444), 0.5*sin(timer*0.333), 3.3*timer);
	
}

void main(void)
{
	vec2 p = vertTexCoord.xy - vec2(.5,.5);
	float rollz = 1.6*sin(0.13*time);
    
	vec3 ro = path( time+0.0 );
	vec3 ta = path( time+0.3 );
	ta.y *= 0.35 + 0.25*sin(0.09*time);
	
	vec3 cw = normalize(ta-ro);
	vec3 cp = vec3(sin(rollz), -cos(rollz),0.0);
	vec3 cu = normalize(cross(cw,cp));
	vec3 cv = normalize(cross(cu,cw));
    
	vec3 rd = normalize( p.x*cu + p.y*cv + 2.1*cw );
    
    float AapBb = ro.x*rd.x + ro.y*rd.y;
    float A2pB2 = rd.x*rd.x + rd.y*rd.y;
    float d = AapBb*AapBb-A2pB2*(ro.x*ro.x + ro.y*ro.y-1.0);
    
	gl_FragColor.rgb = vec3(0.1, 0.3, 0.5);
    
//    if (d < 0.0) {
//        gl_FragColor.rgb = vec3(0.0, 0.0, 1.0);
//    } else {
        float s=sqrt(d);
        float inv_A2pB2=1.0/A2pB2;
        float t1=(-AapBb-s)*inv_A2pB2;
        float t2=(-AapBb+s)*inv_A2pB2;
        
        vec3  oosPos = ro + rd*t2;
        float u = fract(oosPos.z*0.2);
        float v = (atan(oosPos.y, oosPos.x) + M_PI) / (2.0*M_PI);
        
        float att = 1.0 - t2/10.0;
        if (att < 0.0) {
            gl_FragColor.rgb = vec3(0.0, 0.0, 0.0);
        } else {
            if (att > 1.0) att = 1.0;
			
			vec2 uv = vec2(u, v);
			vec3 col = texture2D(texture, uv, -0.5).xyz * att;
            
			col = col*0.3 + 0.7*col*col*(3.0-2.0*col);
			col *= 1.2*vec3(1.0, 1.05, 1.0);
            gl_FragColor.rgb = col;
        }
//    }
}
