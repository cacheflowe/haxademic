package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_Shapes_createStrip
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics texture;
	protected PShape shape;
	protected SimplexNoiseTexture noiseTexture;
	protected boolean nativeTriangles = true;
	
	protected PGraphics textBuffer;
	protected PFont fontBig;
	protected PImage textCropped;
	protected TiledTexture tiledImg;

	protected void config() {
		int FRAMES = 280;
		Config.setProperty(AppSettings.WIDTH, 1280);
		Config.setProperty(AppSettings.HEIGHT, 720);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 4 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 5 );
	}

	protected void firstFrame() {
		texture = PG.newPG(p.width * 6, 150);
		PG.setTextureRepeat(texture, false);
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
		textBuffer = PG.newPG(p.width, p.height);
		fontBig = DemoAssets.fontHelloDenver(textBuffer.height * 0.5f);
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

	protected void drawApp() {
		p.pushMatrix();
		
		// update displacement noise
		noiseTexture.update(0.07f, 0, P.cos(FrameLoop.progressRads()) * 0.2f, P.sin(FrameLoop.progressRads()) * 0.2f);
		
		// update text texture
		texture.beginDraw();
		texture.noStroke();
		texture.background(0, 0, 0);
		texture.translate(texture.width / 2, texture.height / 2);
		
		tiledImg.setOffset(4f * FrameLoop.progress(), 0f);
		tiledImg.setSize(2f, 2f);
		tiledImg.update();
		tiledImg.draw(texture, texture.width, texture.height);

		texture.endDraw();
		
		DebugView.setTexture("textCropped", textCropped);
		DebugView.setTexture("texture", texture);
		DebugView.setTexture("noiseTexture.texture()", noiseTexture.texture());
		
		// set main app context
		background(127);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// draw shape
		if(nativeTriangles == false) {
			// deform mesh
			MeshDeformAndTextureFilter.instance(p).setDisplacementMap(noiseTexture.texture());
			MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(texture.height * 2.5f);
			MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
			MeshDeformAndTextureFilter.instance(p).setYAxisOnly(true);
			MeshDeformAndTextureFilter.instance(p).applyTo(p.g);
	
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
