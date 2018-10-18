package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.SimplexNoiseTexture;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_Shapes_createStrip
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics texture;
	protected PShape shape;
	protected SimplexNoiseTexture noiseTexture;
	protected boolean nativeTriangles = false;
	
	protected PGraphics textBuffer;
	protected PFont fontBig;
	protected PImage textCropped;
	protected TiledTexture tiledImg;

	protected void overridePropsFile() {
		int FRAMES = 280;
		p.appConfig.setProperty(AppSettings.WIDTH, 1280);
		p.appConfig.setProperty(AppSettings.HEIGHT, 720);
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 4 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 5 );
	}

	protected void setupFirstFrame() {
		texture = p.createGraphics(p.width * 8, 150, P.P2D);
		DrawUtil.setTextureRepeat(texture, false);
		noiseTexture = new SimplexNoiseTexture(texture.width, 40);
		noiseTexture.update(0.07f, 0, 0, 0);
		
		if(nativeTriangles == false) {
			// create PShape
			shape = Shapes.createStrip(texture.width, texture.height, texture.width / 3);
			shape.setTexture(texture);
		} else {
			// create PShape for native triangle drawing
			shape = Shapes.createStrip(texture.width, texture.height, 100).getTessellation();
			PShapeUtil.repairMissingSVGVertex(shape);
		}
		
		buildText();
	}
	
	public void buildText() {
		// create buffer & font
		textBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		fontBig = p.createFont(FileUtil.getFile("fonts/_sketch/HelveticaNeueLTStd-Blk.ttf"), 150);
		textCropped = p.createImage(100, 100, P.ARGB);
		
		// draw text
		textBuffer.beginDraw();
		textBuffer.clear();
		textBuffer.background(0, 0);
		textBuffer.fill(255);
		textBuffer.textAlign(P.CENTER, P.CENTER);
		textBuffer.textFont(fontBig);
		textBuffer.textSize(fontBig.getSize());
		textBuffer.text("WAVY", 0, 0, textBuffer.width, textBuffer.height); 
		textBuffer.endDraw();
		
		// crop text
		ImageUtil.imageCroppedEmptySpace(textBuffer, textCropped, ImageUtil.EMPTY_INT, false, new int[] {20, 20, 20, 20}, new int[] {0, 0, 0, 0}, p.color(0, 0));
		
		// create tiled texture
		tiledImg = new TiledTexture(textCropped);
	}

	public void drawApp() {
		p.pushMatrix();
		
		// update displacement noise
		noiseTexture.update(0.07f, 0, P.cos(p.loop.progressRads()) * 0.2f, P.sin(p.loop.progressRads()) * 0.2f);
		
		// update text texture
		texture.beginDraw();
		texture.noStroke();
		texture.background(0, 0, 0);
		texture.translate(texture.width / 2, texture.height / 2);
		
		tiledImg.setOffset(4f * p.loop.progress(), 0f);
		tiledImg.setSize(1f, 1f);
		tiledImg.update();
		tiledImg.drawCentered(texture, texture.width, texture.height);

		texture.endDraw();
		
		p.debugView.setTexture(textCropped);
		p.debugView.setTexture(texture);
		
		// set main app context
		background(0);
		DrawUtil.setCenterScreen(p);
		DrawUtil.basicCameraFromMouse(p.g);
		
		// draw shape
		if(nativeTriangles == false) {
			// deform mesh
			MeshDeformAndTextureFilter.instance(p).setDisplacementMap(noiseTexture.texture());
			MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(texture.height * 2.5f);
			MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
			MeshDeformAndTextureFilter.instance(p).setYAxisOnly(true);
			MeshDeformAndTextureFilter.instance(p).applyTo(p);
	
			p.shape(shape);
			
			p.resetShader();
		} else {
			// draw strip
			shape.disableStyle();
			p.noStroke();
			p.fill(255);
			PShapeUtil.drawTriangles(p.g, shape, texture, 1f);
		}
		
		p.popMatrix();
	}

}
