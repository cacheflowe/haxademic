package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.ColorizeFromTexture;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamColorizeFromTexture 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected ImageGradient imageGradient;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3 ); // 18
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	public void setupFirstFrame () {
		// build palette
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
		
		// capture webcam frames
		p.webCamWrapper.setDelegate(this);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// p.webCamWrapper.getImage()
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = p.createGraphics(800, 600, PRenderers.P2D);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		p.debugView.setTexture(flippedCamera);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			imageGradient.randomGradientTexture();
		}
	}

	public void drawApp() {
		// set up context
		p.background( 0 );
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);

		// show camera & colorize
		if(flippedCamera != null) {
			ImageUtil.cropFillCopyImage(flippedCamera, p.g, true);
			ColorizeFromTexture.instance(p).setTexture(imageGradient.texture());
			ColorizeFromTexture.instance(p).setLumaMult(p.mousePercentX() > 0.5f);
			ColorizeFromTexture.instance(p).setCrossfade(p.mousePercentY());
			ColorizeFromTexture.instance(p).applyTo(p);
		}
	}
	
}
