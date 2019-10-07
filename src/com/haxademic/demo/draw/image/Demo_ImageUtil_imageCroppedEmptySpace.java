package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageUtil_imageCroppedEmptySpace 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics textBuffer;
	protected PFont fontBig;
	protected PImage textCropped;
	protected TiledTexture tiledImg;
	
	protected void overridePropsFile() {
		int FRAMES = 140;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 582);
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}

	public void setupFirstFrame() {
		// create buffer & font
		textBuffer = p.createGraphics(p.width, p.height);	// NOTE: new version of Processing can't use `PRenderers.P3D` - leave it out
		fontBig = DemoAssets.fontHelloDenver(200);
		textCropped = p.createImage(128, 128, P.ARGB);
		
		// draw text
		textBuffer.beginDraw();
		textBuffer.clear();
		textBuffer.background(0, 0);
		textBuffer.fill(255);
		textBuffer.textAlign(P.CENTER, P.CENTER);
		textBuffer.textFont(fontBig);
		textBuffer.textSize(fontBig.getSize());
		textBuffer.text("HELLO", 0, 0, textBuffer.width, textBuffer.height); 
		textBuffer.endDraw();
		p.debugView.setTexture("textBuffer", textBuffer);
		
		// crop text
		ImageUtil.imageCroppedEmptySpace(textBuffer, textCropped, ImageUtil.EMPTY_INT, false, new int[] {10, 20, 40, 20}, new int[] {0, 0, 0, 0}, p.color(0, 255, 0, 0));
		
		// create tiled texture
		tiledImg = new TiledTexture(textCropped);
	}
	
	public void drawApp() {
		background(0);
		PG.setCenterScreen(p);
		
		// draw tiled texture
		float size = 1f + 0.2f * P.sin(p.loop.progressRads());
		tiledImg.setRotation(0.01f * P.sin(p.loop.progressRads()));
		tiledImg.setOffset(0, -1f * p.loop.progress());
		tiledImg.setSize(size, size);
		tiledImg.update();
		tiledImg.drawCentered(p.g, p.width, p.height);
	}
}
