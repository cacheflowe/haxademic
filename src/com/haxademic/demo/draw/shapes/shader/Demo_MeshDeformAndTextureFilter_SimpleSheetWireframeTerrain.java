package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_MeshDeformAndTextureFilter_SimpleSheetWireframeTerrain 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float cols = 25;
	protected float rows = 25;

	protected PShape shape;
	protected PGraphics texture;
	protected SimplexNoiseTexture displaceTexture;

	protected int FRAMES = 360;
	protected LinearFloat displaceAmpMult = new LinearFloat(0, 0.015f); 

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}

	protected void firstFrame() {
		int sheetW = P.round(cols * 80);
		int sheetH = P.round(cols * 80);
		
		// displace texture
		displaceTexture = new SimplexNoiseTexture(sheetW, sheetH);
		
		// create wireframe texture
		texture = p.createGraphics(sheetW, sheetH, P.P2D);
		
		// build sheet mesh
		shape = Shapes.createSheet((int) cols, (int) rows, texture);
		
		// debug view
		DebugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		DebugView.setTexture("texture", texture);
		DebugView.setTexture("displaceTexture", displaceTexture.texture());
	}

	protected void drawApp() {
		// ease amplitude
		if(FrameLoop.loopCurFrame() == 2) displaceAmpMult.setTarget(1);
		if(FrameLoop.loopCurFrame() == FRAMES - 80) displaceAmpMult.setTarget(0);
		displaceAmpMult.update();
		float easedAmp = Penner.easeInOutQuad(displaceAmpMult.value());
		
		// update texture
		texture.beginDraw();
		texture.background(0);
		texture.noFill();
		texture.stroke(255);
		texture.strokeWeight(3f);
		float cellW = (float) texture.width / cols;
		float cellH = (float) (texture.height) / rows;
		for (int x = 0; x <= texture.width; x += cellW) {
			float curX = P.min(x, texture.width - 1);
			texture.line(curX, 0, curX, texture.height);
		}
		for (int y = 0; y <= texture.height; y += cellH) {
			float curY = P.min(y, texture.height - 1);
			texture.line(0, curY, texture.height, curY);
		}
		
		// fade out
		texture.noStroke();
		for (int i = 0; i < 3; i++) {
			texture.beginShape();
			texture.fill(0);
			texture.vertex(0, 0);
			texture.vertex(texture.width, 0);
			texture.fill(0,0);
			texture.vertex(texture.width, texture.height * 0.65f);
			texture.vertex(0, texture.height * 0.65f);
			texture.endShape();
		}
		
		texture.endDraw();
		
		// context & camera
		background(0);
		//p.image(texture, 0, 0);
		PG.setCenterScreen(p.g);
		// PG.basicCameraFromMouse(p.g);
		p.g.rotateX(1.1f);

		// deform mesh
		displaceTexture.offsetY(p.frameCount * 0.01f);
		displaceTexture.zoom(5f);
		MeshDeformAndTextureFilter.instance().setDisplacementMap(displaceTexture.texture());
		MeshDeformAndTextureFilter.instance().setDisplaceAmp(easedAmp * 190f);
		MeshDeformAndTextureFilter.instance().setSheetMode(true);
		MeshDeformAndTextureFilter.instance().setOnContext(p);

		// draw mesh
		p.shape(shape);
		p.resetShader();
		

		// post effects
//		BloomFilter.instance().setStrength(2.11f);
//		BloomFilter.instance().setBlurIterations(1);
//		BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance().applyTo(p);
//		BloomFilter.instance().applyTo(pg);
		
		VignetteFilter.instance().setDarkness(0.49f);
		VignetteFilter.instance().applyTo(p);

//		GrainFilter.instance().setTime(p.frameCount * 0.01f);
//		GrainFilter.instance().setCrossfade(0.12f);
//		GrainFilter.instance().applyTo(p);

	}
		
}