package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.printer.PrintPageDirect;
import com.haxademic.core.math.MathUtil;

import processing.core.PShape;

public class Snowflake 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 400;
	protected ArrayList<PShape> shapes;
	protected int curShapeIndex = 0;
	protected int frameRand = 0;
	
	protected PrintPageDirect printDirect;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1080);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1080);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void setupFirstFrame() {
		printDirect = new PrintPageDirect(false);
		pg = P.p.createGraphics(PrintPageDirect.PRINT_W, PrintPageDirect.PRINT_H, PRenderers.P3D);
	}
	
	public void setup() {
		super.setup();
		
		shapes = new ArrayList<PShape>();
		shapes.add( p.loadShape( FileUtil.getFile("svg/cacheflowe-logo.svg")).getTessellation());
		shapes.add( p.loadShape( FileUtil.getFile("svg/heart.svg")).getTessellation());
		shapes.add( p.loadShape( FileUtil.getFile("svg/diamond.svg")).getTessellation());
		shapes.add( p.loadShape( FileUtil.getFile("svg/cursor.svg")).getTessellation());
		shapes.add( p.loadShape( FileUtil.getFile("svg/pot-leaf.svg")).getTessellation());
		shapes.add( p.loadShape( FileUtil.getFile("svg/eye.svg")).getTessellation());
		shapes.add( p.loadShape( FileUtil.getFile("svg/x.svg")).getTessellation());
		shapes.add( p.loadShape( FileUtil.getFile("svg/smiley.svg")).getTessellation());
		
		// normalize shapes
		for (PShape shape : shapes) {
			shape.disableStyle();
//			PShapeUtil.setMaterialColor(shape, p.color(255));
		}
	}


	public void drawApp() {
		// set context
		pg.beginDraw();
		pg.ortho();
		pg.background(0);
		pg.noFill();
		pg.stroke(255);
		pg.strokeWeight(2f);
		DrawUtil.setCenterScreen(pg);
		DrawUtil.setDrawCenter(pg);
		pg.rotate(-P.HALF_PI);
		
		// draw!
		float radius = 30f;
		float frameRand = p.frameCount;
		while(radius < pg.width * 0.6f) {
//			float rowSize = 10f + p.noise(P.sin(p.frameCount * 0.02f) * 0.2f + radius * 0.002f) * 100f;
			float rowSize = (p.width * 0.05f) + p.noise(frameRand * 0.001f + radius * 0.002f) * (p.width * 0.3f);
//			if(radius + rowSize > p.width * 0.4f) rowSize = p.width * 0.4f - radius;
//			if(rowSize < 20) rowSize = 20;
			radius += rowSize;
			int numShapes = P.round(radius / (55f + 30f * P.sin(radius / 30f))); // P.sin(p.frameCount * 0.01f));
			while((int) numShapes % 4 != 0) numShapes++; // keep it even
			if(numShapes < 4) numShapes = 4;
			drawRow(radius, rowSize, numShapes);
		}
		
		// context end
		pg.endDraw();
		
		// post process
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.12f);
		GrainFilter.instance(p).applyTo(pg);
		
		BloomFilter.instance(p).setStrength(1f);
//		BloomFilter.instance(p).setBlurIterations(5);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance(p).applyTo(pg);
		
//		VignetteFilter.instance(p).setDarkness(0.4f);
//		VignetteFilter.instance(p).applyTo(pg);

		InvertFilter.instance(p).applyTo(pg);
		
		// draw to screen
		ImageUtil.cropFillCopyImage(pg, p.g, true);
//		p.image(pg, 0, 0);
	}
	
	protected void drawRow(float radius, float rowSize, float numShapes) {
		float segmentRadians = P.TWO_PI / numShapes;
		for (int i = 0; i < numShapes; i++) {
			// placement
			float curRadians = segmentRadians * (float) i;
			float curRadius = radius - rowSize/2f;
			float shapeX = P.cos(curRadians) * curRadius;
			float shapeY = P.sin(curRadians) * curRadius;
			
			
			// draw shape
			float shapeSize = rowSize * 0.95f;
			pg.pushMatrix();
			pg.translate(shapeX, shapeY);
			pg.rotate(curRadians);
			
			int shapeType = P.round(-0.4f + p.noise(0.0001f * (radius * rowSize)) * 25f);
			float sizeMult = 0.5f + 0.5f * p.noise(0.0003f * (radius * rowSize));
			shapeSize *= sizeMult;
			switch (shapeType) {
			case 0:
				pg.rect(0, 0, shapeSize, shapeSize);
				break;
			case 1:
				pg.ellipse(0, 0, shapeSize, shapeSize);
				break;
			case 2:
				pg.line(0, -shapeSize / 2f, 0, shapeSize / 2f);
				break;
			case 3:
				pg.line(-shapeSize / 2f, 0, shapeSize / 2f, 0);
				break;
			case 4:
				pg.rotate(P.QUARTER_PI);
				pg.line(0, -shapeSize / 2f, 0, shapeSize / 2f);
				pg.line(-shapeSize / 2f, 0, shapeSize / 2f, 0);
				break;
			case 5:
				if(sizeMult > 0.65f) pg.rotate(P.HALF_PI);
				pg.line(-shapeSize / 2f, -shapeSize / 2f, shapeSize / 2f, -shapeSize / 2f);
				pg.line(-shapeSize / 2f, 0, shapeSize / 2f, 0);
				pg.line(-shapeSize / 2f, shapeSize / 2f, shapeSize / 2f, shapeSize / 2f);
				break;
			case 6:
				
				Shapes.drawDisc3D(pg, shapeSize / 2f, shapeSize / 3f, 10, 4, p.color(255), p.color(0));
				break;
			case 7:
				Shapes.drawDisc3D(pg, shapeSize / 2f, shapeSize / 3f, 10, 6, p.color(255), p.color(0));
				break;
			case 8:
				Shapes.drawDisc3D(pg, shapeSize / 2f, shapeSize / 3f, 10, 36, p.color(255), p.color(0));
				break;
			case 9:
//				pg.fill(0, 255, 0);
				pg.rotate(-curRadians); // undo rotation from above
				pg.translate(P.cos(curRadians) * shapeSize / 2f, P.sin(curRadians) * shapeSize / 2f, 0);
				pg.rotate(curRadians);
				pg.rotateY(-P.HALF_PI);
				Shapes.drawPyramid(pg, shapeSize, shapeSize, true);
				break;
			case 10:
				if(sizeMult > 0.65f) {
					pg.rotate(-curRadians); // undo rotation from above
					pg.translate(P.cos(curRadians) * -shapeSize / 2f, P.sin(curRadians) * -shapeSize / 2f, 0);
					pg.rotate(curRadians);
					pg.rotateY(P.HALF_PI);
				}

				Shapes.drawPyramid(pg, shapeSize, shapeSize, true);
				break;
			case 11:
				drawSvg(shapes.get(0), shapeSize, false);				// cacheflowe
				break;
			case 12:
				drawSvg(shapes.get(1), shapeSize, false);				// heart
				break;
			case 13:
				drawSvg(shapes.get(2), shapeSize, false);				// diamond
				break;
			case 14:
				drawSvg(shapes.get(3), shapeSize, false);				// cursor
				break;
			case 15:
				drawSvg(shapes.get(4), shapeSize, false);				// pot-leaf
				break;
			case 16:
				drawSvg(shapes.get(5), shapeSize, false);				// eye
				break;
			case 17:
				drawSvg(shapes.get(6), shapeSize, false);				// x
				break;
			case 18:
				drawSvg(shapes.get(7), shapeSize, false);				// smiley
				break;

			default:
				Shapes.drawStar(pg, 5f, shapeSize / 2f, 0.5f, 10f, 0);
				break;
			}
			
			pg.popMatrix();
		}
	}
	
	protected void drawSvg(PShape shape, float shapeSize, boolean stroke) {
		if(stroke) setStroke();
		else setFill();
		pg.rotate(P.HALF_PI);
		float shapeScale = MathUtil.scaleToTarget(shape.height, shapeSize);
		pg.shape(shape, 0, 0, shape.width * shapeScale, shape.height * shapeScale);
		
		setStroke();
	}

	protected void setStroke() {
		pg.stroke(255);
		pg.noFill();
	}
	
	protected void setFill() {
		pg.noStroke();
		pg.fill(255);
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') printDirect.printImage(pg);
		if(p.key == 'r') frameRand = P.round(p.random(9999999));
	}

	
}