package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;
import com.haxademic.core.hardware.webcam.WebCamWrapper;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_VideoFrameDifference 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics buffer1;
	protected PGraphics buffer2;
	protected PGraphics curBuffer;
	protected PGraphics lastBuffer;
	protected PShader differenceShader;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	public void setupFirstFrame () {
		WebCamWrapper.initWebCam(p, 6);
		WebCamWrapper.addWebCamCallback(this);
		
		buffer1 = p.createGraphics(p.width, p.height, PRenderers.P3D);
		buffer2 = p.createGraphics(p.width, p.height, PRenderers.P3D);
		curBuffer = buffer1;
		lastBuffer = buffer2;
		
		differenceShader = p.loadShader(FileUtil.getFile("shaders/filters/texture-difference.glsl"));
	}

	public void drawApp() {
		// set up context
		p.background( 0 );
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		
		// set debug info
		WebCamWrapper.update();
		p.debugView.setTexture(WebCamWrapper.getImage());
		
		p.filter(differenceShader);
	}

	@Override
	public void newFrame(PImage frame) {
		// copy webcam to current buffer
		curBuffer = (curBuffer == buffer1) ? buffer2 : buffer1;
		lastBuffer = (curBuffer == buffer1) ? buffer2 : buffer1;
		ImageUtil.cropFillCopyImage(WebCamWrapper.getImage(), curBuffer, true);
		
		// run difference shader & draw to screen
		differenceShader.set("tex1", curBuffer);
		differenceShader.set("tex2", lastBuffer);
	}

}

//		p.image(WebCamWrapper.getImage(), 0, 0);