package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.sketch.hardware.kinect_openni.KinectFaceRecorder;

import processing.core.PGraphics;

public class TextureKinectFaceRecording 
extends BaseTexture {

	protected int _eqIndex;
	protected float bright = 0;

	public TextureKinectFaceRecording( int width, int height ) {
		super(width, height);
	}
	
	public PGraphics texture() {
		return KinectFaceRecorder.curSessionTexture;
	}

	// overridden!!!!
	public void update() {
		resetUseCount(); // this should be the last thing that happens in a frame, to help with texture pool optimization
		postProcess();
		
//		if(_texture != null) {
//			BrightnessFilter.instance(P.p).setBrightness(bright);
//			BrightnessFilter.instance(P.p).applyTo(_texture);
//			if(bright < 1.0f) bright += 0.02f;
//			P.println("bright",bright);
//		}
	}

//	public void setActive( boolean isActive ) {
//		boolean wasActive = _active;
//		super.setActive( isActive );
//		if(wasActive == false && isActive == true) {
//			bright = 0;
//		}
//	}

	
//	public void setActive( boolean isActive ) {
//		super.setActive(isActive);
//		_eqIndex = MathUtil.randRange(3, 31);
//	}
//	
//	public void updateDraw() {
//		_texture.clear();
//		
//		_texture.fill( _colorEase.colorInt(), P.constrain( P.p.audioIn.getEqAvgBand( _eqIndex ) * 255, 0, 255 ) );
//		_texture.rect(0, 0, width, height );
//	}
}
