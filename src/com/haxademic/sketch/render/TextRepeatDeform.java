package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class TextRepeatDeform 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics textBuffer;
	protected PImage textCropped;
	protected PFont fontBig; 
	protected TiledTexture tiledImg;

	
	protected void overridePropsFile() {
		int FRAMES = 180;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}
	
	protected void setupFirstFrame() {
		drawText();
	}
	
	public void drawApp() {
		background(20);
		
		// draw tiled texture
		DrawUtil.setCenterScreen(p);
		p.rotateX(0.95f); // p.mousePercentY()
		
		tiledImg.setRotation(0.02f * P.sin(p.loop.progressRads()));
		tiledImg.setOffset(0, -1f * p.loop.progress());
		float sizeOsc = 0.03f * P.sin(p.loop.progressRads());
		tiledImg.setSize(0.6f + sizeOsc, 0.6f + sizeOsc);
		tiledImg.update();
		tiledImg.drawCentered(p.g, p.width * 5f, p.height * 15f);
		
		// post effects
		postProcess();
	}

	protected void drawText() {
		// create buffer & font
		textBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		fontBig = p.createFont(FileUtil.getFile("fonts/_sketch/HelveticaNeueLTStd-Blk.ttf"), 100);
		textCropped = p.createImage(100, 100, P.ARGB);
		
		// draw text
		textBuffer.beginDraw();
		textBuffer.clear();
		textBuffer.fill(255);
		textBuffer.textAlign(P.CENTER, P.TOP);
		textBuffer.textFont(fontBig);
		textBuffer.textSize(fontBig.getSize());
		textBuffer.text("HONESTY"+FileUtil.NEWLINE+"EQUALITY"+FileUtil.NEWLINE+"RESPECT"+FileUtil.NEWLINE+"JUSTICE", 0, 0, textBuffer.width, textBuffer.height); // + 
		textBuffer.endDraw();
		
		// crop text
		ImageUtil.imageCroppedEmptySpace(textBuffer, textCropped, ImageUtil.CLEAR_INT_PG, false, new int[] {10, 20, 40, 20}, new int[] {0, 0, 0, 0}, p.color(0, 0));
		
		// create tiled texture
		tiledImg = new TiledTexture(textCropped);
	}
	
	protected void postProcess() {
//		BrightnessFilter.instance(p).setBrightness(2.7f);
//		BrightnessFilter.instance(p).applyTo(p);
//		ContrastFilter.instance(p).setContrast(1.35f);
//		ContrastFilter.instance(p).applyTo(p);
		BlurHFilter.instance(p).setBlurByPercent(0.7f, p.width);
		BlurHFilter.instance(p).applyTo(p);
		BlurVFilter.instance(p).setBlurByPercent(0.7f, p.height);
		BlurVFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.7f);
		VignetteFilter.instance(p).applyTo(p);
	}
		
}