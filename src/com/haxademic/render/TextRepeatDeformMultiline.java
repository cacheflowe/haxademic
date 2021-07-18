package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class TextRepeatDeformMultiline 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics textBuffer;
	protected PGraphics multilineBuffer;
	protected PImage[] textsCropped;
	protected PFont fontBig; 
	protected TiledTexture tiledImg;

	
	protected void config() {
		int FRAMES = 280;
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 768);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}
	
	protected void firstFrame() {
		drawText();
	}
	
	protected void drawApp() {
		background(20);
		
		// draw tiled texture
		PG.setCenterScreen(p);
		p.rotateX(0.93f + 0.02f * P.sin(FrameLoop.progressRads()));//0.95f); // Mouse.yNorm
//		p.rotateX(0.95f);
		
//		tiledImg.setRotation(0.01f * P.sin(AnimationLoop.progressRads()));
//		tiledImg.setRotation(0f);
		tiledImg.setOffset(0, -1f * FrameLoop.progress());
		float sizeOsc = 0.01f * P.sin(FrameLoop.progressRads());
		tiledImg.setZoom(0.5f + sizeOsc, 0.5f + sizeOsc);
		tiledImg.update();
		tiledImg.draw(p.g, p.width * 5f, p.height * 15f);
		
		// post effects
		postProcess();
	}

	protected void drawText() {
		// create buffer & font
		textBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		fontBig = p.createFont(FileUtil.getPath("fonts/_sketch/HelveticaNeueLTStd-Blk.ttf"), 100);
		
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
		DebugView.setTexture("textCropped", textsCropped[0]);
		DebugView.setTexture("multilineBuffer", multilineBuffer);
		
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