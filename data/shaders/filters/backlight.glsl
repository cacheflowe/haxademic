// from: https://www.shadertoy.com/view/MdX3z7//

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
vec3 getsample(vec2 coord, inout float isback)
{
	vec3 back=vec3(38.,122.,23.)/256.;
	vec3 pix=texture2D(iChannel0,coord/2.+vec2(.5)).rgb;
	if (dot(normalize(pix),normalize(back))>.91 && pix.g>.3)
    {isback=1.;}
	else
    { isback=0.;}
	return pix;
}


float rand(vec2 co){
	return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main(void)
{
	
	vec2 tcoord = vertTexCoord.xy;
	vec2 coord = tcoord*2.-vec2(1.);
	float dith=rand(coord*iGlobalTime)*.1;
	vec3 p=vec3(coord.xy,-20.+dith);
	vec3 lightpos;
	if (iMouse.z<0.5) {
		lightpos=vec3(-1,0.7,4.);
	} else {
		lightpos=vec3(iMouse.xy/iResolution.xy*2.-vec2(1.),4.);
	}
	float isback=0.;
	vec3 pix=getsample(coord, isback);
	pix*=pow(length(pix),2.);
	pix=mix(pix,vec3(.55,.5,.5),0.5)*(1.-isback)*.6;
	pix+=(vec3(.25,.2,.1)+vec3(1,1,.9)*smoothstep(1.,0.,length(lightpos.xy-p.xy)/2.))*isback;
	float vol=0.;
	for (int i=0; i<210; i++ ) {
		p.z+=.1;
		vec3 vdist=lightpos-p;
		float ldist=length(vdist);
		vec2 isect=p.xy-vdist.xy*p.z/vdist.z;
		if (p.z>0.) isect=p.xy;
		float hit=0.;
		vec3 test=getsample(isect, hit);
		hit=clamp(hit+smoothstep(0.,1.,abs(p.z)*.05),0.,1.);
		vol+=smoothstep(1.,0.,ldist/20.)*hit;
    }
	pix=mix(pix,vec3(1.,1.,.93),vol*.016);
	gl_FragColor = vec4(pix,1.0);
	
}