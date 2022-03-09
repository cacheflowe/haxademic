package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.video.Movie;

public class Demo_PShape_textureMappedCylinderOfSpheres 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape s;
	protected Movie movie;

	protected void config() {
		Config.setAppSize(1280, 1280);
	}

	protected void firstFrame() {
		// load video 
		movie = DemoAssets.movieFractalCube(); // new Movie(P.p, FileUtil.getPath("images/_sketch/_mario-ecg.mp4"));	// 
		movie.loop();
		DebugView.setTexture("movie", movie);
		
		
		/// build shape
		p.sphereDetail(4);
		s = createShape(P.GROUP);
		
		float numRows = 80;
		float numCols = 24;
		float rowSpacing = p.height * 0.01f;
		float staticLightsH = numRows * rowSpacing;
		float startY = -staticLightsH / 2f;
		float segRads = P.TWO_PI / numCols;
		float staticRadius = p.width * 0.1f;
		
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				float colRads = segRads * i;
				float lightX = P.cos(colRads) * staticRadius;
				float lightZ = P.sin(colRads) * staticRadius;
				float lightY = startY + j * rowSpacing;
				PShape subShape = PShapeUtil.createSphere(6, lightX, lightY, lightZ, p.color(255), p.color(0), 0);
				subShape = subShape.getTessellation();
				float lightU = 1f - i / numCols;
				float lightV = j / numRows;
				P.out(lightU, lightV, subShape.getVertexCount());
				addTextureSingleUV(subShape, movie, lightU, lightV);
				s.addChild(subShape);
			}
		}
	}

	protected void drawApp() {
		background(0);
		
		pg.beginDraw();
		pg.background(0);
		PG.setCenterScreen(pg);
		PG.basicCameraFromMouse(pg, 1.3f);
//		PG.setBetterLights(pg);
//		pg.lights();
		pg.fill(0);
		Shapes.drawCylinder(pg, 36, pg.width * 0.0999f, pg.width * 0.0999f, p.height, true);
		pg.shape(s);
		pg.endDraw();
		
		// run bloom on off-screen buffer
		int bloomBlendMode = P.round(p.frameCount / 200f) % 3;
		BloomFilter.instance(p).setStrength(3f);
		BloomFilter.instance(p).setBlurIterations(16);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
		DebugView.setValue("Bloom blend mode", bloomBlendMode);

		
		// draw to screen
		p.image(pg, 0, 0);
	}
	
	
	public static PVector util = new PVector();
	public static void addTextureSingleUV(PShape shape, PImage img, float u, float v) {
		shape.setStroke(false);
		shape.setTextureMode(P.NORMAL);
		
		for (int i = 0; i < shape.getVertexCount(); i++) {
			shape.setTextureUV(i, u, v);
		}
			
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			addTextureSingleUV(subShape, img, u, v);
		}
		
		if(img != null) shape.setTexture(img);
	}


}