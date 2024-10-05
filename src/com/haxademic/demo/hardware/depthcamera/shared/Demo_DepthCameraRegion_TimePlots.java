package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.TimePlot;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.StringUtil;

import processing.core.PFont;

public class Demo_DepthCameraRegion_TimePlots
extends Demo_DepthCameraRegion {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// gesture trackers
	protected FloatBuffer gestureHoriz = new FloatBuffer(60);
	protected float xDelta = 0;
	protected float lastUserX = 0;
	
	// graph
	protected TimePlot timePlot;
	protected TimePlot timePlotX;
	protected TimePlot timePlotY;
	protected TimePlot timePlotZ;

	
	protected void config() {
		super.config();
		Config.setAppSize(640, 880);
		RealSenseWrapper.setSmallStream();
	}

	protected void firstFrame() {
		super.firstFrame();
		timePlot = new TimePlot(p.width, 100, -1, 1);
		timePlotX = new TimePlot(p.width, 100, -1, 1);
		timePlotY = new TimePlot(p.width, 100, -1, 1);
		timePlotZ = new TimePlot(p.width, 100, -1, 1);
		DebugView.setTexture("timePlot", timePlot.image());
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') timePlot.clear();
	}
	
	protected void drawDepthImage() {
		p.image(DepthCamera.instance().camera.getDepthImage(), 0, 0);
		
		// p.blendMode(PBlendModes.ADD);
		p.image(regionFlatDebug, 0, 0);
		p.blendMode(PBlendModes.BLEND);

		PG.drawBorder(p.g, 0xff000000, 2);
	}
	
	protected void calcDepthGesture() {
		xDelta = region.controlX() - lastUserX;
		xDelta = (P.abs(xDelta) > 0.1f) ? 0 : xDelta; // limit when starting in a different place
		lastUserX = region.controlX(); // userX.value();
		gestureHoriz.update(xDelta);
	}
	
	protected void updateTimePlot() {
		timePlot.update(gestureHoriz.sum());
		timePlotX.update(region.controlX());
		timePlotY.update(region.controlY());
		timePlotZ.update(region.controlZ());
	}
	
	protected void drawTimePlot() {
		p.image(timePlot.image(),0, 480);
		p.image(timePlotX.image(),0, 580);
		p.image(timePlotY.image(),0, 680);
		p.image(timePlotZ.image(),0, 780);
	}
	
	protected void printTextDebug() {
		PFont font = FontCacher.getFont(DemoAssets.fontMonospacePath, 30);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text("Frame delta: " + StringUtil.roundToPrecision(xDelta, 3, true), 20, p.height - 100);
		p.text("Motion sum:  " + StringUtil.roundToPrecision(gestureHoriz.sum(), 3, true), 20, p.height - 70);
}
	
	protected void drawApp() { 
		super.drawApp();
		p.background(0);
		drawDepthImage();
		calcDepthGesture();
		updateTimePlot();
		drawTimePlot();
		// printTextDebug();
	}
		
}
