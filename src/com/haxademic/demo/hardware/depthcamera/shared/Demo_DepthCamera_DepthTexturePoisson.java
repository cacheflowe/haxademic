package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessToAlphaFilter;
import com.haxademic.core.draw.filters.pshader.PoissonFill;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

import processing.core.PGraphics;
import processing.core.PImage;


public class Demo_DepthCamera_DepthTexturePoisson 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage depthImg;
	protected PGraphics depthCopy;
	protected PoissonFill poisson;

	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthImg = depthCamera.getDepthImage();
		depthCopy = PG.newPG(depthImg.width, depthImg.height);
		poisson = new PoissonFill(depthImg.width, depthImg.height);
		DebugView.setTexture("depthImg", depthImg);
		DebugView.setTexture("depthCopy", depthCopy);
		DebugView.setTexture("output", poisson.output());
	}
	
	protected void drawApp() {
		p.background(0, 0, 0);
		ImageUtil.copyImage(depthImg, depthCopy);
		// BrightnessToAlphaFilter.instance().updateHotSwap();
		BrightnessToAlphaFilter.instance().setFlip(false);
		BrightnessToAlphaFilter.instance().setSmoothstepLow(0.0f);
		BrightnessToAlphaFilter.instance().setSmoothstepHigh(0.15f);
		BrightnessToAlphaFilter.instance().applyTo(depthCopy);

		poisson.applyTo(depthCopy);
		p.image(poisson.output(), 0, 0);
		// p.image(depthCopy, 0, 0);
		// ImageUtil.cropFillCopyImage(depthSilhouetteSmoothed.image(), p.g, false);
	}
	
}
