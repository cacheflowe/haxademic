package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import ch.bildspur.realsense.type.ColorScheme;
import ch.bildspur.realsense.type.PersistencyIndex;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_RealSenseWrapper_DepthImageMask
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseWrapper realSenseWrapper;
	protected String NEAR = "NEAR";
	protected String FAR = "FAR";
	protected PImage thermalGradient;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
	}

	protected void firstFrame() {
		// get texture
		thermalGradient = P.getImage("haxademic/images/palettes/thermal-gradient.png");
		
		// build realsense wrapper
		RealSenseWrapper.METERS_FAR_THRESH = 2;
		RealSenseWrapper.COLOR_SCHEME = ColorScheme.BlackToWhite;
		realSenseWrapper = new RealSenseWrapper(p, true, true);
		realSenseWrapper.setMirror(true);
		
		// add extra camera config
		realSenseWrapper.camera().addSpatialFilter(1, 0.75f, 50, 1);
		realSenseWrapper.camera().addDecimationFilter(2);
		realSenseWrapper.camera().addDisparityTransform(true);
//		realSenseWrapper.camera().addHoleFillingFilter(HoleFillingType.NearestFromAround);
		realSenseWrapper.camera().addTemporalFilter(0.5f, 30, PersistencyIndex.ValidIn1_Last2);

		// store listener
		P.store.addListener(this);
	}

	protected void drawApp() {
		// start context
		p.background(255,0,0);
		p.noStroke();
		
		// copy realsense depth to buffer
		realSenseWrapper.update();
		ImageUtil.cropFillCopyImage(realSenseWrapper.getDepthImage(), pg, true);
		
		// preprocess
		ChromaColorFilter.instance(p).presetBlackKnockout();
		ChromaColorFilter.instance(p).applyTo(pg);
		ContrastFilter.instance(p).setContrast(2f);
		ContrastFilter.instance(p).applyTo(pg);
		
		// colorize
		ColorizeFromTexture.instance(p).setTexture(thermalGradient);
		ColorizeFromTexture.instance(p).applyTo(pg);
		
		// draw to screen
		ImageUtil.drawImageCropFill(DemoAssets.textureNebula(), p.g, true);
		ImageUtil.drawImageCropFill(realSenseWrapper.getRgbImage(), p.g, true);
		p.image(pg, 0, 0);
		p.blendMode(PBlendModes.BLEND);
	}
	
	// AppStore listeners

	public void updatedNumber(String key, Number val) {
		if(key.equals(NEAR) || key.equals(FAR)) {
			P.out("Update near/far", UI.value(NEAR), UI.value(FAR));
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
}
