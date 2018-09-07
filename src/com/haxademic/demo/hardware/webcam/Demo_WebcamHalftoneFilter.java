package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneCamoFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamHalftoneFilter 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected ImageGradient imageGradient;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 18 ); // 18
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	public void setupFirstFrame () {
		// capture webcam frames
		p.webCamWrapper.setDelegate(this);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// p.webCamWrapper.getImage()
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = p.createGraphics(1152, 768, PRenderers.P2D);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		p.debugView.setTexture(flippedCamera);
	}

	public void drawApp() {
		// set up context
		p.background( 0 );
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);

		// show camera & colorize
		if(flippedCamera != null) {
			float osc = P.sin(p.frameCount * 0.01f);
			ImageUtil.cropFillCopyImage(flippedCamera, p.g, true);
//			ContrastFilter.instance(p).setContrast(1.4f);
//			ContrastFilter.instance(p).applyTo(p);
			HalftoneCamoFilter.instance(p).setTime(P.PI + osc);
			HalftoneCamoFilter.instance(p).setScale(1.5f + 0.5f * osc);
			HalftoneCamoFilter.instance(p).applyTo(p);
		}
	}
	
}
