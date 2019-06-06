package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class TextRepeatDeformMultiline 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics textBuffer;
	protected PGraphics multilineBuffer;
	protected PImage[] textsCropped;
	protected PFont fontBig; 
	protected TiledTexture tiledImg;

	
	protected void overridePropsFile() {
		int FRAMES = 280;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}
	
	protected void setupFirstFrame() {
		drawText();
	}
	
	public void drawApp() {
		background(20);
		
		// draw tiled texture
		PG.setCenterScreen(p);
		p.rotateX(0.93f + 0.02f * P.sin(p.loop.progressRads()));//0.95f); // p.mousePercentY()
//		p.rotateX(0.95f);
		
//		tiledImg.setRotation(0.01f * P.sin(p.loop.progressRads()));
//		tiledImg.setRotation(0f);
		tiledImg.setOffset(0, -1f * p.loop.progress());
		float sizeOsc = 0.01f * P.sin(p.loop.progressRads());
		tiledImg.setSize(0.5f + sizeOsc, 0.5f + sizeOsc);
		tiledImg.update();
		tiledImg.drawCentered(p.g, p.width * 5f, p.height * 15f);
		
		// post effects
		postProcess();
	}

	protected void drawText() {
		// create buffer & font
		textBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		fontBig = p.createFont(FileUtil.getFile("fonts/_sketch/HelveticaNeueLTStd-Blk.ttf"), 100);
		
		// create cropped texts
		textsCropped = new PImage[] {
				createCroppedText("UNITY"),
				createCroppedText("ETHICS"),
				createCroppedText("INTEGRITY"),
				createCroppedText("TRUTH"),
				createCroppedText("RESPECT"),
				createCroppedText("JUSTICE"),
				createCroppedText("EQUALITY"),
				createCroppedText("HONESTY"),
		}; 
		
		// get total height
		int textH = 0;
		for (int i = 0; i < textsCropped.length; i++) {
			textH += textsCropped[i].height;
		}
		
		// create multiline buffer and paste texts into it
		int paddingX = 50;
		multilineBuffer = p.createGraphics(textsCropped[1].width + paddingX, textH, PRenderers.P2D);
		multilineBuffer.beginDraw();
		int textY = 0;
		for (int i = 0; i < textsCropped.length; i++) {
			multilineBuffer.image(textsCropped[i], paddingX/2, textY, multilineBuffer.width - paddingX, textsCropped[i].height);
			textY += textsCropped[i].height;
		}
		multilineBuffer.endDraw();
		p.debugView.setTexture(textsCropped[0]);
		p.debugView.setTexture(multilineBuffer);
		
		// create tiled texture
		tiledImg = new TiledTexture(multilineBuffer);
	}
	
	protected PImage createCroppedText(String txt) {
		PImage textCropped = p.createImage(100, 100, P.ARGB);
		
		// draw text
		textBuffer.beginDraw();
		textBuffer.clear();
		textBuffer.fill(235);
		textBuffer.textAlign(P.CENTER, P.CENTER);
		textBuffer.textFont(fontBig);
		textBuffer.textSize(fontBig.getSize());
		textBuffer.text(txt, 0, 0, textBuffer.width, textBuffer.height);
		textBuffer.endDraw();
		
		// crop text
		ImageUtil.imageCroppedEmptySpace(textBuffer, textCropped, ImageUtil.EMPTY_INT, false, new int[] {20, 0, 20, 0}, new int[] {0, 0, 0, 0}, p.color(0, 0));
		return textCropped;
	}
	
	protected void postProcess() {
		BlurHFilter.instance(p).setBlurByPercent(0.7f, p.width);
		BlurHFilter.instance(p).applyTo(p);
		BlurVFilter.instance(p).setBlurByPercent(0.7f, p.height);
		BlurVFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.9f);
		VignetteFilter.instance(p).applyTo(p);
	}
		
}