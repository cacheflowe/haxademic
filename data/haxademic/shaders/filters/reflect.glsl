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
uniform float reflectPosition = 0.5;

void main() {
	vec2 uv = vertTexCoord.xy;
	if(horizontal == true) {
		if(reflectPosition >= 0.5) {
		  if( uv.x > reflectPosition ) {
				uv.x = reflectPosition - (uv.x - reflectPosition);
		  }
		} else {
			if( uv.x < reflectPosition ) {
				uv.x = reflectPosition - (uv.x - reflectPosition);
		  }
		}
	} else {
		if(reflectPosition >= 0.5) {
		  if( uv.y > reflectPosition ) {
				uv.y = reflectPosition - (uv.y - reflectPosition);
		  }
		} else {
			if( uv.y < reflectPosition ) {
				uv.y = reflectPosition - (uv.y - reflectPosition);
		  }
		}
	}
	gl_FragColor = texture2D(texture, uv);
}
