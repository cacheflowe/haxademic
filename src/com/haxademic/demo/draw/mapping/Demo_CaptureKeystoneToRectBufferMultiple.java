package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.CaptureKeystoneToRectBuffer;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_CaptureKeystoneToRectBufferMultiple
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage sourceTexture;
	protected PGraphics sourceBuffer;
	protected CaptureKeystoneToRectBuffer[] mappedCapture;
	protected boolean debug = true;
	protected int captureIndex = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
//		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 6 );
	}

	protected void setupFirstFrame() {
		// load source image
		sourceTexture = DemoAssets.justin();
		sourceBuffer = ImageUtil.imageToGraphics(sourceTexture);
		mappedCapture = new CaptureKeystoneToRectBuffer[] {
				new CaptureKeystoneToRectBuffer(sourceBuffer, 450, 200, "text/keystoning/capture-map-demo.txt"),
				new CaptureKeystoneToRectBuffer(sourceBuffer, 450, 200, "text/keystoning/capture-map-demo-2.txt"),
		};
		setActiveRect();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debug = !debug;
		if(p.key == 'r') mappedCapture[captureIndex].resetCorners();
		if(p.key == ' ') {
			captureIndex++;
			if(captureIndex >= mappedCapture.length) captureIndex = 0;
			setActiveRect();
		}
	}
	
	protected void setActiveRect() {
		for (int i = 0; i < mappedCapture.length; i++) {
			mappedCapture[i].setActive(i == captureIndex);
		}
	}

	public void drawApp() {
		p.background(0);
		
		// update mapped source to captured buffer
		mappedCapture[captureIndex].update();
		
		// draw mapping UI
		if(p.webCamWrapper != null) sourceTexture = p.webCamWrapper.getImage();	// draw webcam if exists
		ImageUtil.cropFillCopyImage(sourceTexture, sourceBuffer, true);			// reset source buffer image
		mappedCapture[captureIndex].drawDebug(sourceBuffer, true);				// then draw mapping UI on top
		
		// draw mapped source & result
		p.image(sourceBuffer, 0, 0);
		p.image(mappedCapture[captureIndex].mappedBuffer(), 0, sourceBuffer.height);
	}

}
