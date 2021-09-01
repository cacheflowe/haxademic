package com.haxademic.demo.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;

public class Demo_FrameLoop_frameMod
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	
	
	protected StringBufferLog logOut = new StringBufferLog(4);

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.LOOP_FRAMES, 180 );
		Config.setProperty( AppSettings.LOOP_TICKS, 16 );
	}

	protected void firstFrame() {
	}

	protected void drawApp() {
		// context
		background(0);
		p.noStroke();
		p.fill(255);
		
		// draw
		drawShapes();
		drawText();
		drawEventLog();
	}
	
	protected void drawShapes() {
		// draw oscillations & indications
		float barW = (p.width - 40);
		float noiseZoom = 0.3f;
		p.rect(20, 20, barW * FrameLoop.progress(), 20);
		p.rect(20, 50, barW * Penner.easeInOutQuart(FrameLoop.progress()), 20);
		p.rect(20, 80, barW/2 + barW/2 * P.cos(FrameLoop.progressRads()), 20);
		p.rect(20, 110, FrameLoop.oscRads(FrameLoop.progressRads(), 40, barW - 40), 20);
		p.rect(20, 140, barW * FrameLoop.noiseLoop(noiseZoom, 0.00f), 4);
		p.rect(20, 146, barW * FrameLoop.noiseLoop(noiseZoom, 0.10f), 4);
		p.rect(20, 152, barW * FrameLoop.noiseLoop(noiseZoom, 0.20f), 4);
		p.rect(20, 158, barW * FrameLoop.noiseLoop(noiseZoom, 0.30f), 4);
	}
	
	protected void drawText() {
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 18);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1.2f, PTextAlign.LEFT, PTextAlign.TOP);

		// draw debug output
		p.text(
				"FrameLoop.count() :: " + (int) FrameLoop.count() + FileUtil.NEWLINE + 
				"FrameLoop.count(0.1f) :: " + (int) FrameLoop.count(0.1f) + FileUtil.NEWLINE + 
				"FrameLoop.progress() :: " + FrameLoop.progress() + FileUtil.NEWLINE + 
				"FrameLoop.curTick() :: " + FrameLoop.curTick() + FileUtil.NEWLINE + 
				"FrameLoop.loopCurFrame() :: " + FrameLoop.loopCurFrame() + " / " + FrameLoop.loopFrames() + FileUtil.NEWLINE + 
				"FrameLoop.frameMod(100) :: " + FrameLoop.frameModLooped(100) + FileUtil.NEWLINE + 
				"FrameLoop.timeSpeed() :: " + FrameLoop.timeAmp() + FileUtil.NEWLINE + 
				"FrameLoop.deltaTime() :: " + FrameLoop.deltaTime() + FileUtil.NEWLINE + 
				""
		, 20, 300);
	}
	
	protected void drawEventLog() {
		// draw frame mod events 
		if(FrameLoop.frameModLooped(90)) logOut.update("FrameLoop.frameMod(90) " + p.frameCount);
		if(FrameLoop.frameModSeconds(2)) logOut.update("frameModSeconds(2) " + p.frameCount);
		if(FrameLoop.frameModSeconds(5)) logOut.update("frameModSeconds(5) " + p.frameCount);
		if(FrameLoop.frameModMinutes(0.5f)) logOut.update("frameModMinutes(0.5) " + p.frameCount);
		if(FrameLoop.frameModHours(1f/120f)) logOut.update("frameModHours(1/120) " + p.frameCount);
		logOut.printToScreen(p.g, 20, p.height - 70);

	}
}
