package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.Pixelate2Filter;
import com.haxademic.core.draw.filters.pshader.PixelateFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

import processing.core.PGraphics;


public class Demo_DepthSilhouetteSmoothed_PixelEffect 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;
	protected PGraphics pgColor;
    protected SimplexNoise3dTexture noiseTexture;

	protected void config() {
	    Config.setAppSize(1920, 1080);
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		// init depth cam
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 10);
		depthSilhouetteSmoothed.buildUI(false);
		
		// build FBOs
		pgColor = PG.newPG(pg.width, pg.height);

		// add noise texture
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, false);
		
		// add camera images to debugview
		DebugView.setTexture("depthBuffer", depthSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", depthSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", depthSilhouetteSmoothed.image());
		DebugView.setTexture("pg", pg);
	}
	
	protected void drawApp() {
		p.background(0);
		
		// update camera
		depthSilhouetteSmoothed.update();
		
		// update noise
	      // update perlin texture
        noiseTexture.update(
                3,
                0,
                0,
                p.frameCount * 0.005f,
                p.frameCount * 0.01f,
                false,
                false
        );

		
		// draw to pg
		pg.beginDraw();
		// post fx
		BlurProcessingFilter.instance(p).setBlurSize(20);
		BlurProcessingFilter.instance(p).setSigma(20);
		BlurProcessingFilter.instance(p).applyTo(pg);
		BrightnessStepFilter.instance(p).setBrightnessStep(-3/255f);
		BrightnessStepFilter.instance(p).applyTo(pg);
		
		// draw camera on top
		pg.blendMode(PBlendModes.ADD);
		ImageUtil.drawImageCropFill(depthSilhouetteSmoothed.image(), pg, true, false);
		pg.endDraw();
		
		// copy to color pg
		pgColor.beginDraw();
		ImageUtil.copyImage(pg, pgColor);

		// draw noise lightly on top
		pgColor.push();
		pgColor.blendMode(PBlendModes.ADD);
		PG.setPImageAlpha(pgColor, 0.4f);
		ImageUtil.drawImageCropFill(noiseTexture.texture(), pgColor, true, false);
		PG.resetPImageAlpha(pgColor);
		pgColor.pop();
		
		// colorize
		ColorizeFromTexture.instance(p).setTexture(ImageGradient.RAINBOWISH());
		ColorizeFromTexture.instance(p).applyTo(pgColor);
		// draw silhouette on top
//		pgColor.push();
//		pgColor.blendMode(PBlendModes.ADD);
//		ImageUtil.drawImageCropFill(depthSilhouetteSmoothed.image(), pgColor, true, false);
//		pgColor.pop();
        
		pgColor.endDraw();

		// pixelate
		Pixelate2Filter.instance(p).setDivider(4);
//		Pixelate2Filter.instance(p).applyTo(pgColor);
		PixelateFilter.instance(p).setDivider(30, pg.width, pg.height);
		PixelateFilter.instance(p).applyTo(pgColor);
		pg.endDraw();
		
		
		// copy to screen
		ImageUtil.cropFillCopyImage(pgColor, p.g, false);
	}
	
}
