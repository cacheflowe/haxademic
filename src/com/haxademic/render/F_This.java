package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class F_This
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics texture;
	protected PShape shape;
	
	protected PGraphics textBuffer;
	protected PFont fontBig;
	protected PImage textCropped;
	protected TiledTexture tiledImg;
	
	protected float textToTextureScale;

	protected void config() {
		int FRAMES = 60 * 8;
		Config.setProperty(AppSettings.WIDTH, 1280);
		Config.setProperty(AppSettings.HEIGHT, 1280);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
//		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE, true );
//		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1 + FRAMES * 1 );
//		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 1 + FRAMES * 2 );
	}

	protected void firstFrame() {
		texture = PG.newPG(p.width * 8, 150);
		PG.setTextureRepeat(texture, false);
		
		// create PShape
		shape = drawCurlyStrip(p.width * 0.6f, p.height * 4f, 2000, 6f, 300);
		shape.setTexture(texture);
		
		buildText();
	}
	
	public PShape drawCurlyStrip(float radius, float height, int detail, float rotations, float thickness) {
		P.p.textureMode(P.NORMAL); 
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUAD_STRIP);
		sh.noStroke();
		for (int i = 0; i <= detail; i++) {
			float progress = (float) i / (float) detail;
			float angle = P.TWO_PI / detail * rotations;
			float x = P.sin(i * angle);
			float z = P.cos(i * angle);
			float y = height * 0.5f - progress * height;
			float u = (float)i / detail;
//			sh.normal(x, 0, z);
			sh.vertex(x * radius, y - thickness/2, z * radius, u, 0);
			sh.vertex(x * radius, y + thickness/2, z * radius, u, 1);
		}
		sh.endShape();
		P.p.textureMode(P.IMAGE); 	// reset 
		return sh;
	}

	
	public void buildText() {
		// create buffer & font
		textBuffer = PG.newPG(p.width, p.height);
//		fontBig = p.createFont(FileUtil.getPath("fonts/_sketch/HelveticaNeueLTStd-Blk.ttf"), 150);
		fontBig = DemoAssets.fontOpenSans(150);
		textCropped = p.createImage(16, 16, P.ARGB);
		
		// draw text
		textBuffer.beginDraw();
		textBuffer.clear();
		textBuffer.background(0, 0);
		textBuffer.fill(255);
		textBuffer.textAlign(P.CENTER, P.CENTER);
		textBuffer.textFont(fontBig);
		textBuffer.textSize(fontBig.getSize());
		textBuffer.text("FUCK THIS SHIT", 0, 0, textBuffer.width, textBuffer.height); 
		textBuffer.endDraw();
		
		// crop text
		ImageUtil.imageCroppedEmptySpace(textBuffer, textCropped, ImageUtil.EMPTY_INT, false, new int[] {20, 20, 20, 20}, new int[] {0, 0, 0, 0}, p.color(0, 0));
		
		// create tiled texture
		tiledImg = new TiledTexture(textCropped);
		
		// calc x repeat between text size & texture size
		textToTextureScale = (float) textCropped.width / (float) texture.width;
		DebugView.setValue("textToTextureScale", textToTextureScale);
		
		// debug textures
		DebugView.setTexture("textCropped", textCropped);
		DebugView.setTexture("texture", texture);
	}

	protected void drawApp() {
		p.pushMatrix();
		
		// update text tiled texture
		texture.beginDraw();
		texture.noStroke();
		texture.clear();
		texture.background(0, 0, 0);
		texture.translate(texture.width / 2, texture.height / 2);
		
		tiledImg.setOffset(FrameLoop.progress() * 4f, 0f);
//		tiledImg.setOffset(AnimationLoop.progress(), 0f);
		tiledImg.setZoom(1f, 1f);
		tiledImg.update();
		tiledImg.draw(texture, texture.width, texture.height);

		texture.endDraw();
		
		// set main app context
		background(0);
		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		p.translate(0, 0, -width * 0.8f);
		
		
		p.rotateY(FrameLoop.progressRads() * 1f);

		//		PG.basicCameraFromMouse(p.g);
		
		// draw shape
		p.shape(shape);
		
		p.popMatrix();
		
		// post process
		VignetteFilter.instance(p).applyTo(p);
	}

}
