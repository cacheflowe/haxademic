// from: https://www.shadertoy.com/view/MlyGWR by ollj
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;

//a simpler tiny (well documented) variant of this is
//https://www.shadertoy.com/view/ltjXDW
//but that one flickers more.

//fractal noise, modified by ollj
//to work nicely with modifiable constants:
//to allow lower and higher detail, varying #layers
//to have "roughness" independend from #layers
//to visualize a chosable fractal pattern via //#define showMultiversePattern
//to remove unnecessary distortions, less 2d rotation.
//
//based on https://www.shadertoy.com/view/Msf3Wr
//by mu6k
//License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

//known errors
//brightness oscillates a bit (over time)
//more noticable with few [layers], barely with layers>6
//because f(x)=x-x*x: is not an ideal averaging curve for "shepard tone fadeing"
//but for a good performance it is close enough.

//beware:
//while this can be used to zoom infinitely
//its fractalness means that
//the same valleys/hills re-appear within the same angle
//and if you stand at the center
//the horizon is "a perfect fit"
//over infinitely many valleys and mountains.
//which is noticeable rather quickly
//even if you map this on a sphere.
//A workaround for this is to use this noise
//multiple times to create interferrence patterns.

//type constrains:
//because [i] [layers] and [pattern]
//make little sense as floats
//i made them explicitly type int
//for user friendly constrains
//even though type conversion may slow shit down.
//See yourself how it looks with [pattern] type float

////irrational numbers, do not touch!
const float pi =asin(1.)*2.;   //3.14..=fullRotationDistance/radius
const float tau=asin(1.)*4.;   //6.28..=fullRotationDistance/diameter
const float phi=sqrt(5.)*.5+.5;//1.61..=goldenRatio; 1/phi=1-phi=0.61...
//they are not essential here, but i like to include them EVERYWHERE
////irrational numbers.end


////modifiable constants:
//extreme values likely lead to extreme artefacs.

//number if iterations , level of detail
const int layers=8;
//layers=8 is a good compromise bettwen
//perfortmance and not seeing large squares.

//roughness >1. smaller values are smoother noise.
//roughness becomes irrelevant on small [layers]
const float roughness=8.;//=.7*tau*phi;

//z sets a fractal pattern, it is ALWAYS noticable.
//some are just less diagonal and blend in more.
//values near 50 tend to blend in best.
//values likely >100 cause various issues
//due to low precision for small abs(p.y) (you will see a "horizon" in the noie itself)
const int pattern=41;

#define HideMultiversePattern
//comment out to visualize [pattern]
//better with layers>9
//a better [pattern] appears more uniformly grey
//=41 is pretty decent at that.
//a "worse" pattern will create more noticeable concentric
//lines or rings of valleys and hills.
//an ideal pattern may look like a fibonacci spiral?


////modifiable constants.end


//there are likely better hashes for this one
//but for this demo the fract(sin()) hash is pretty irrelevant?
float hash(float x){
 return fract(sin(x*9801.)*99.);
 //return fract(sin(cos(x*99.)*99.)*99.);
 //return fract(sin(cos(x*12.13)*19.123)*17.321);
}

float ss01(float x){return smoothstep(0.,1.,x);}
//this noise function still works recursivey.
float noise(vec2 p){
  const float z=float(pattern);
  //const float z=float(pattern)*1.61; //bad idea
  vec2 m=fract(p),o=floor(p);o.y*=z;o.y+=o.x;
  float a=hash(o.y     );float b=hash(o.y+1.  );
  float c=hash(o.y+   z);float d=hash(o.y+1.+z);//begs for simplification
  return mix(mix(a,b,ss01(m.x)),mix(c,d,ss01(m.x)),ss01(m.y));
}

void main() {
  float o=roughness/sqrt(float(layers));
  //dividing roughness by /sqrt(layers)
  //keeps the zooming speed relatively constant for most layer counts;
  float t=-2.*time/o;
  vec2 uv = vertTexCoord.xy - vec2(.5,.5);
	uv.x *= texOffset.y/texOffset.x; // fix aspect ratio
  float w=0.;
  for(int i=0;i<layers;i++) {
    float j=float(i);
    float m=float(layers);
    #ifndef HideMultiversePattern
      j /= o;
    #endif
    float u=mod(t+j,m);
    float e=pow(o,u);
    float l=u-t;
    float z=u/m;
    w-=noise(uv*e+cos(vec2(l)))*(z-z*z);
    //*(.5-abs(f-.5))*.5;
  }

  //tweaky values need to be optimized out.
  //so far this linear interpolation works fine (a in,it is visible)
  //for a STATIC range of layers, range [2 to 20]
  w=mix(w+.7,w+1.5,float(layers)*.1-.3);
  gl_FragColor = vec4(vec3(w),1.);

}
