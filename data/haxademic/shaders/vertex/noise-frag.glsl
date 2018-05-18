#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_COLOR_SHADER

uniform float time;

varying vec3 vert;
varying vec3 norm;
varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertLightDir;

uniform vec3 lightDir = vec3(0.2, 0.2, 0.9);
uniform vec3 lightCol = vec3(1., 0.5, 1.);
uniform vec3 lightAmbient = vec3(0.1, 0.1, 0.5);
uniform int lightsOn = 1;
const vec3 center = vec3(0.);


/////////////////////////////////////////////////////////////////////
// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

// Simplex Noise (http://en.wikipedia.org/wiki/Simplex_noise), a type of gradient noise
// that uses N+1 vertices for random gradient interpolation instead of 2^N as in regular
// latice based Gradient Noise.

vec2 hash( vec2 p ) {
	p = vec2( dot(p,vec2(127.1,311.7)),
			  dot(p,vec2(269.5,183.3)) );
	return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

float noise( in vec2 p ) {
    const float K1 = 0.366025404; // (sqrt(3)-1)/2;
    const float K2 = 0.211324865; // (3-sqrt(3))/6;
	vec2 i = floor( p + (p.x+p.y)*K1 );
    vec2 a = p - i + (i.x+i.y)*K2;
    vec2 o = (a.x>a.y) ? vec2(1.0,0.0) : vec2(0.0,1.0); //vec2 of = 0.5 + 0.5*vec2(sign(a.x-a.y), sign(a.y-a.x));
    vec2 b = a - o + K2;
	vec2 c = a - 1.0 + 2.0*K2;
    vec3 h = max( 0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );
	vec3 n = h*h*h*h*vec3( dot(a,hash(i+0.0)), dot(b,hash(i+o)), dot(c,hash(i+1.0)));
    return dot( n, vec3(70.0) );
}
/////////////////////////////////////////////////////////////////////



void main() {
  // generative material color
  // float r = 0.5 + 0.5 * sin(vert.x * 0.1);
  // float g = 0.5 + 0.5 * sin(vert.y * 0.1);
  // float b = 0.5 + 0.5 * sin(vert.z * 0.1);
  // vec3 materialColor = vec3(r, g, b);

  // generative material color - use distance from center to oscillate colors
  // vec3 materialColor = vec3(0.5 + 0.5 * sin(time + 0.8 * distance(center, vert)));
  // vec3 materialColor = vec3(sin(0.1 * distance(center.x, vert.x)), sin(0.1 * distance(center.y, vert.y)), sin(0.1 * distance(center.z, vert.z)));

  // plasma-y?
  vec3 point1 = vec3(sin(time) * 130., sin(time * 2.) * 200., sin(time * 4.) * 250.);
  vec3 point2 = vec3(sin(time * 2.) * 700., sin(time * 2.) * 610., sin(time * 3.) * 500.);
  vec3 point3 = vec3(sin(time * 3.) * 290., sin(time) * 240., sin(time * 1.) * 670.);
  // vec3 point4 = vec3(sin(time * 2.) * 90., sin(time * 8.) * 1240., sin(time * 2.) * 630.);
  float minDist = min(distance(point1, vert), distance(point2, vert));
  minDist = min(minDist, distance(point3, vert));
  float avgDist = (distance(point1, vert) + distance(point2, vert) + distance(point3, vert)) / 3.;
  vec3 materialColor = vec3(
    0.5 + 0.5 * sin(minDist * 0.1)
  );
  materialColor = vec3(
    0.5 + 0.5 * sin(distance(point1, vert)/120.), 
    0.5 + 0.5 * sin(distance(point2, vert)/120.), 
    0.5 + 0.5 * sin(distance(point3, vert)/120.)
  );

  // simplex noise rgb
  // float r = noise(vec2(vert.x, vert.y)/500.);
  // float g = noise(vec2(vert.y, vert.z)/500.);
  // float b = noise(vec2(vert.x, vert.z)/500.);
  // vec3 materialColor = vec3(r, g, b);

  // apply basic lights
  materialColor = lightAmbient + (materialColor * lightCol);

  // lights don't really work on a flat sheet, so we can exclude the light bounce calcs
  if(lightsOn == 1) {
    // http://www.opengl-tutorial.org/beginners-tutorials/tutorial-8-basic-shading/#vertex-normals
    // float cosTheta = dot( norm, lightDir );
    // diffuse lighting
    // let's look at this next: https://github.com/stackgl/glsl-lighting-walkthrough
    float cosTheta = clamp( dot( norm, lightDir ), 0, 1 );
    // float cosTheta = abs( dot( norm, lightDir ) );
    float dist = vert.z;
    materialColor = materialColor * cosTheta;// / (dist*dist);
  }
  gl_FragColor = vec4(materialColor, 1.0);
}
