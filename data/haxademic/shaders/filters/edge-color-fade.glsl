#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 texOffset;
varying vec4 vertTexCoord;

uniform float spreadX;
uniform float spreadY;
uniform vec3 edgeColor;
uniform float crossfade = 1.;

float map(float value, float inMin, float inMax, float outMin, float outMax) {
  return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

void main() {
	// original color, uv
	vec2 uv = vertTexCoord.xy;
  float aspect = (texOffset.y / texOffset.x);
	vec4 color = texture2D( texture, uv.xy);
	vec4 replaceColor = vec4(edgeColor, 1.);

	// check distance from edges to add edge amplitude
	float distX = min(abs(uv.x), abs(1. - uv.x));
	float distY = min(abs(uv.y), abs(1. - uv.y));
	float edgeAmpX = map(distX, 0., spreadX, 1., 0.);
	float edgeAmpY = map(distY, 0., spreadY, 1., 0.);
	edgeAmpX = clamp(edgeAmpX, 0., 1.);
	edgeAmpY = clamp(edgeAmpY, 0., 1.);

	// creates diagonal edges, which is slightly better than the odd diagonal lines from:
	// https://stackoverflow.com/questions/48792209/coloring-rectangle-in-function-of-distance-to-nearest-edge-produces-weird-result
	float edgeAmp = 0.;
	edgeAmp += edgeAmpX;
	edgeAmp += edgeAmpY;

	// diagonal artifacts
	float edgeAmp2 = max(edgeAmpX, edgeAmpY);

	// mix the 2 edge calcs...
	// this is lame, but looks better
	float finalEdgeAmp = mix(edgeAmp, edgeAmp2, edgeAmp2);
	// finalEdgeAmp = edgeAmp2;

  /*
	// ----------------------------------------------------
	// TODO - a better solutionwould be to translate coordinates:
	// from: https://stackoverflow.com/questions/48792209/coloring-rectangle-in-function-of-distance-to-nearest-edge-produces-weird-result
	vec2 uvn = abs(uv - 0.5) * 2.0;

	vec2 distV = uvn;
	float maxDist = max(abs(distV.x), abs(distV.y));
	float circular = length(distV);
	float square = maxDist;

	vec4 mate = mix(color, replaceColor, mix(circular, square, maxDist * crossfade));
	gl_FragColor = mate;
	// ----------------------------------------------------
	*/
	
	gl_FragColor = mix(color, replaceColor, finalEdgeAmp *  crossfade);
	// gl_FragColor = vec4(finalEdgeAmp);
}
