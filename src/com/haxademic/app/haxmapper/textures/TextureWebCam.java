package com.haxademic.app.haxmapper.textures;

import processing.opengl.PShader;
import processing.video.Capture;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.system.FileUtil;

public class TextureWebCam
extends BaseTexture {

	protected Capture _webCam;
	protected PShader _threshold;

	public TextureWebCam() {
		super();
		initWebCam();
		_threshold = P.p.loadShader( FileUtil.getHaxademicDataPath() + "shaders/filters/blackandwhite.glsl");
	}
	
	void initWebCam() {
		String[] cameras = Capture.list();
		if (cameras.length == 0) {
			DebugUtil.printErr("Couldn't find a webcam");
			buildGraphics(100, 100);
		} else {
			_webCam = new Capture(P.p, cameras[6]);
			_webCam.start();
			buildGraphics(100, 100); 
		}
	}

	public void update() {
		super.update();

		if( _texture != null && _webCam != null && _webCam.available() == true ) { 
			if( _texture.width != _webCam.width && _webCam.width > 100 ) {
				buildGraphics( _webCam.width, _webCam.height ); 
			}
			
			_webCam.read(); 

			if( _texture != null ) {
				_texture.beginDraw();
				_texture.image( _webCam.get(), 0, 0 );
				_texture.endDraw();
				_texture.filter( _threshold );
			}
		} else {
			_texture.beginDraw();
			_texture.clear();
			_texture.endDraw();
		}
	}
}
