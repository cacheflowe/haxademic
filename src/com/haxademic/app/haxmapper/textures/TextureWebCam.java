package com.haxademic.app.haxmapper.textures;

import processing.video.Capture;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;

public class TextureWebCam
extends BaseTexture {

	Capture _webCam;

	public TextureWebCam() {
		super();
		initWebCam();
	}
	
	void initWebCam() {
		String[] cameras = Capture.list();
		if (cameras.length == 0) {
			DebugUtil.printErr("Couldn't find a webcam");
			buildGraphics(1, 1);
		} else {
			_webCam = new Capture(P.p, cameras[0]);
			_webCam.start();
			buildGraphics(_webCam.width, _webCam.height);
		}      
	}

	public void update() {
		if(P.p.frameCount < 3) return;
		if( _texture != null && _webCam != null && _webCam.available() == true ) { 
			_webCam.read(); 

			if( _texture != null ) {
				_texture.beginDraw();
				_texture.image( _webCam.get(), 0, 0, _webCam.width, _webCam.height );
				_texture.endDraw();
			}
		}
	}
}
