package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DepthCameraRegion_ForcedPerspective
extends Demo_DepthCameraRegion {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// cube texture
	protected PGraphics boxTex;

	
	
	protected void firstFrame() {
		super.firstFrame();
		boxTex = PG.newPG(1024, 1024);
	}
	
	public void keyPressed() {
		super.keyPressed();
	}
	
	protected void drawForcedPerspectiveBox() {
		// update box texture
		boxTex.beginDraw();
		boxTex.background(0);
//		ImageUtil.copyImage(DepthCamera.instance().camera.getRgbImage(), boxTex);
		ImageUtil.cropFillCopyImage(DepthCamera.instance().camera.getRgbImage(), boxTex, true);
		PG.drawGrid(boxTex, 0x00000000, 0x44ffffff, 20, 20, 4, false);
		boxTex.endDraw();
		
		// draw box based on user position
		float xOffset = region.easedX();
		CameraUtil.setCameraDistance(p.g, 100, 20000);
		p.push();
		p.rotateY(xOffset / 2f);
		PImage tex = boxTex;
		Shapes.drawTexturedCubeInside(
				p.g, p.width * 4, p.height* 4, p.height * 16, 
				tex, tex, tex, tex, tex, tex);
				// w, h, d, texture1, texture2, texture3, texture4, floor, ceiling
		p.pop();
	}
	
	protected void drawApp() { 
		super.drawApp();
		drawForcedPerspectiveBox();
	}
		
}
