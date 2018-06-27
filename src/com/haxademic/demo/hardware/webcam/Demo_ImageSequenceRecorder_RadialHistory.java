package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.SaturationFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageSequenceRecorder_RadialHistory 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageSequenceRecorder recorder;
	protected PGraphics camBuffer;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3 );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty(AppSettings.FILLS_SCREEN, true );
	}

	public void setupFirstFrame () {
		camBuffer = p.createGraphics(540, 960, PRenderers.P2D);
		recorder = new ImageSequenceRecorder(camBuffer.width, camBuffer.height, 100);
		p.webCamWrapper.setDelegate(this);
	}

	public void drawApp() {
		p.background( 0 );

		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		for (int i = 0; i < recorder.images().length; i++) {
			int shaderFrame = (recorder.frameIndex() + i) % recorder.images().length;

			float imageScale = MathUtil.scaleToTarget(camBuffer.height, p.height - 10 * i);
			p.image(recorder.images()[shaderFrame], 0, 0, camBuffer.width * imageScale, camBuffer.height * imageScale);
			
		}
		p.popMatrix();

		DrawUtil.setDrawCorner(p);
		recorder.drawDebug(p.g);
	}

	@Override
	public void newFrame(PImage frame) {
		// set recorder frame - use buffer as intermediary to fix aspect ratio
//		ImageUtil.copyImageFlipH(p.webCamWrapper.getImage(), camBuffer);
		ImageUtil.cropFillCopyImage(p.webCamWrapper.getImage(), camBuffer, true);
		ImageUtil.flipH(camBuffer);
		recorder.addFrame(camBuffer);
		// do some post-processing
		SaturationFilter.instance(p).setSaturation(0);
		SaturationFilter.instance(p).applyTo(recorder.getCurFrame());
		// set debug staus
		p.debugView.setValue("Last WebCam frame", p.frameCount);
	}
}