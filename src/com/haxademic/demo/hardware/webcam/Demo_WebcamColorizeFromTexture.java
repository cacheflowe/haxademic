package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamColorizeFromTexture
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected ImageGradient imageGradient;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame () {
		// build palette
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);

		// capture webcam frames
		WebCam.instance().setDelegate(this);
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = p.createGraphics(800, 600, PRenderers.P2D);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		DebugView.setTexture("webcam", flippedCamera);
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
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);

		// show camera & colorize
		if(flippedCamera != null) {
			ImageUtil.cropFillCopyImage(flippedCamera, p.g, true);
			ColorizeFromTexture.instance(p).setTexture(imageGradient.texture());
			ColorizeFromTexture.instance(p).setLumaMult(Mouse.xNorm > 0.5f);
			ColorizeFromTexture.instance(p).setCrossfade(Mouse.yNorm);
			ColorizeFromTexture.instance(p).applyTo(p);
		}
	}

}
