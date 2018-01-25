package com.haxademic.sketch.hardware.kinect_v2_windows;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.SaturationFilter;
import com.haxademic.core.draw.filters.shaders.SharpenFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class Kinect2SilhouetteRD
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectPV2 kinect;
	protected PGraphics buffer;
	protected PShader leaveBlackShader;
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
		
		leaveBlackShader = p.loadShader(FileUtil.getFile("shaders/filters/leave-black.glsl"));
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
		if(p.frameCount < 10  || p.frameCount % 200 == 0) p.background(20);
		
//		p.debugView.setTexture(kinect.getColorImage());
//		p.debugView.setTexture(kinect.getInfraredImage());
//		p.debugView.setTexture(kinect.getDepthImage());
//		p.debugView.setTexture(kinect.getBodyTrackImage());
		
		if(kinect.getBodyTrackImage().width > 10 && buffer == null) createBuffer();
		if(buffer != null) {
			buffer.beginDraw();
			buffer.clear();
			buffer.image(kinect.getBodyTrackImage(), 0, 0);
			buffer.endDraw();
			buffer.filter(leaveBlackShader);
			p.debugView.setTexture(buffer);
			
			ImageUtil.cropFillCopyImage(buffer, p.g, true);
//			p.image(buffer, 0, 0);
			
			applyRD();
			
			// ensure black & white
			SaturationFilter.instance(p).setSaturation(0); 
			SaturationFilter.instance(p).applyTo(p); 
		}
	}

}
