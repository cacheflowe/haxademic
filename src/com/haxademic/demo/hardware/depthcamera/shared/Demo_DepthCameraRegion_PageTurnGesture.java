package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.TimePlot;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.StringUtil;
import com.haxademic.core.ui.IUIControl;

import processing.core.PFont;

public class Demo_DepthCameraRegion_PageTurnGesture
extends Demo_DepthCameraRegion {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// gesture trackers
	protected FloatBuffer gestureHoriz = new FloatBuffer(60);
	protected float xDelta = 0;
	protected float lastUserX = 0;
	
	// graph
	protected TimePlot timePlot;

	
	protected void firstFrame() {
		super.firstFrame();
		timePlot = new TimePlot(IUIControl.controlW - 20, 80, -1, 1);
		DebugView.setTexture("timePlot", timePlot.image());
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') timePlot.clear();
	}
	
	protected void drawDepthImage() {
		PG.setPImageAlpha(p.g, 0.4f);
		ImageUtil.drawImageCropFill(DepthCamera.instance().camera.getDepthImage(), p.g, false);
		PG.resetPImageAlpha(p.g);
	}
	
	protected void calcDepthGesture() {
		xDelta = region.controlX() - lastUserX;
		xDelta = (P.abs(xDelta) > 0.1f) ? 0 : xDelta; // limit when starting in a different place
		lastUserX = region.controlX(); // userX.value();
		gestureHoriz.update(xDelta);
	}
	
	protected void updateTimePlot() {
		timePlot.update(gestureHoriz.sum());
	}
	
	protected void drawTimePlot() {
		p.blendMode(PBlendModes.SCREEN);
		ImageUtil.drawImageCropFill(timePlot.image(), p.g, false);
		p.blendMode(PBlendModes.BLEND);
	}
	
	protected void printTextDebug() {
		PFont font = FontCacher.getFont(DemoAssets.fontMonospacePath, 30);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text("Frame delta: " + StringUtil.roundToPrecision(xDelta, 3, true), 20, p.height - 100);
		p.text("Motion sum:  " + StringUtil.roundToPrecision(gestureHoriz.sum(), 3, true), 20, p.height - 70);
}
	
	protected void drawApp() { 
		super.drawApp();
		drawDepthImage();
		calcDepthGesture();
		updateTimePlot();
		drawTimePlot();
		printTextDebug();
	}
		
}
