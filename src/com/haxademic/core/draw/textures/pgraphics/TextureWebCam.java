package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;

import processing.video.Capture;

public class TextureWebCam
extends BaseTexture {

	protected Capture _webCam;

	public TextureWebCam( int width, int height ) {
		super(width, height);
		
		initWebCam();
	}
	
	void initWebCam() {
		String[] cameras = Capture.list();
		if (cameras.length == 0) {
			DebugUtil.printErr("Couldn't find a webcam");
		} else {
			_webCam = new Capture(P.p, cameras[6]);
			_webCam.start();
		}
	}

	public void updateDraw() {
		if( _texture != null && _webCam != null && _webCam.available() == true ) { 			
			_webCam.read(); 
			_texture.image( _webCam.get(), 0, 0, width, height );
		}
	}
}
