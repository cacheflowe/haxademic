package com.haxademic.sketch.hardware.kinect_v2_windows;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.LeaveBlackFilter;
import com.haxademic.core.draw.filters.shaders.SaturationFilter;
import com.haxademic.core.draw.filters.shaders.SharpenFilter;
import com.haxademic.core.draw.image.ImageUtil;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;

public class Kinect2SilhouetteRD
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectPV2 kinect;
	protected PGraphics buffer;
	protected int RD_ITERATIONS = 2;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		kinect = new KinectPV2(p);
		kinect.enableDepthImg(true);
		kinect.enableColorImg(true);
		kinect.enableDepthMaskImg(true);
		kinect.enableBodyTrackImg(true);
		kinect.enableInfraredImg(true);
		kinect.init();
	}

	protected void createBuffer() {
		buffer = p.createGraphics(kinect.getBodyTrackImage().width, kinect.getBodyTrackImage().height, PRenderers.P3D);
	}
	
	protected void applyRD() {
		// effect
		float blurAmp = P.map(p.mouseX, 0, p.width, 0.25f, 1.5f);
		float sharpAmp = P.map(p.mouseY, 0, p.height, 0.5f, 2f);
		// blurAmp = 0.5f;
		// sharpAmp = 1f;
		for (int i = 0; i < RD_ITERATIONS; i++) {			
			BlurHFilter.instance(p).setBlurByPercent(blurAmp, p.width);
			BlurHFilter.instance(p).applyTo(p);
			BlurVFilter.instance(p).setBlurByPercent(blurAmp, p.height);
			BlurVFilter.instance(p).applyTo(p);
			SharpenFilter.instance(p).setSharpness(sharpAmp);
			SharpenFilter.instance(p).applyTo(p);
		}
	}
	
	public void drawApp() {
		// only draw background at the start. also flash/reset screen every few seconds with almost-black (this still resolves to b&w from the RD functions);
		if(p.frameCount < 10 || p.frameCount % 200 == 0) p.background(20);
		
		// lazy-init buffer when camera is ready
		if(kinect.getBodyTrackImage().width > 10 && buffer == null) createBuffer();
		
		if(buffer != null) {
			// set kinect silhouette on buffer
			buffer.beginDraw();
			buffer.clear();
			buffer.image(kinect.getBodyTrackImage(), 0, 0);
			buffer.endDraw();
			
			// turn buffer white pixels to transparent 
			LeaveBlackFilter.instance(p).applyTo(buffer);
			p.debugView.setTexture(buffer);
			
			// fill transparent silhouette buffer to screen 
			ImageUtil.cropFillCopyImage(buffer, p.g, true);

			// do reaction-diffusion feedback
			applyRD();
			
			// ensure black & white
			SaturationFilter.instance(p).setSaturation(0); 
			SaturationFilter.instance(p).applyTo(p); 
		}
	}

}
