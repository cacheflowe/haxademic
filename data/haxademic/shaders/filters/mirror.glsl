// by cacheflowe

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform bool horizontal = true;

void main() {
	vec2 uv = vertTexCoord.xy;
	if(horizontal == true) {
	  if( uv.x > 0.5 ) {
	      uv.x = 1.0 - uv.x;
	  }
	} else {
		if( uv.y > 0.5 ) {
	      uv.y = 1.0 - uv.y;
	  }
	}
	gl_FragColor = texture2D(texture, uv);
}
