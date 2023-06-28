package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PShape;

public class Demo_VoronoiHoffsCachedShapes 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// from: https://openprocessing.org/sketch/352817/
	// with some adjustments to work in newer Processing
	
	protected float hoffOrthoFactor;
	protected String NUM_CELLS = "NUM_CELLS";
	protected String CELL_DETAIL = "CELL_DETAIL";
	protected int MAX_CELLS = 6000;
	protected Cell cells[] = new Cell[MAX_CELLS];

	protected void config() {
		int scaleUp = 1;
		Config.setAppSize( 1920 * scaleUp, 1080 * scaleUp );
		Config.setPgSize( 1920 * scaleUp, 1080 * scaleUp );
		Config.setAppSize(1920, 1080);
		Config.setPgSize(1080 * 2, 1920);
//		Config.setProperty( AppSettings.FULLSCREEN, true );
//		Config.setProperty( AppSettings.SCREEN_X, 0 );
//		Config.setProperty( AppSettings.SCREEN_Y, 0 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
		Config.setProperty( AppSettings.LOOP_FRAMES, 600 );
	}

	protected void firstFrame() {
		UI.addTitle("VORONOI");
		UI.addSlider(NUM_CELLS, 1200, 3, MAX_CELLS, 10, false);
		UI.addSlider(CELL_DETAIL, 60, 3, 100, 1, false);
		
		for (int i = 0; i < MAX_CELLS; i++) {
			cells[i] = new Cell(
				random(pg.width), random(pg.height), 
				random(-1, 1), random(-1, 1)
			);
		}
	}

	protected void drawApp() {
		
		// set up context & draw voronoi to pg
		pg.beginDraw();
//		PG.setDrawFlat2d(pg, true);
		pg.background(127);
		pg.noStroke();
		pg.ortho();		
		hoffOrthoFactor = dist(0, 0, pg.width, pg.height);
		for (int i = 0; i < UI.valueInt(NUM_CELLS); i++) {
			pg.fill(sin(i) * 127 + 127, sin(i+1) * 127 + 127, sin(i+2) * 127 + 127);
			
			// grid
			if(KeyboardState.keyOn('1')) {
    			int gridRes = P.floor(P.sqrt(UI.valueInt(NUM_CELLS)));
    			float spacingH = pg.width / gridRes * 1.1f;
    			float spacingV = pg.height / gridRes * 1.1f;
    			float xIndex = MathUtil.gridXFromIndex(i, gridRes);
    			float yIndex = MathUtil.gridYFromIndex(i, gridRes);
    			if(xIndex % 2 == 0) {
    				xIndex -= 0.5f;
    				yIndex += 0.5f;
    			}
    			if(xIndex % 3 == 0) {
    				xIndex += 0.5f;
    				yIndex += 0.5f;
    			}
    			cells[i].setPosition(xIndex * spacingH, yIndex * spacingV);
			}
			
			
			// spiral
	        if(KeyboardState.keyOn('2')) {
    			 float segmentRads = P.TWO_PI / 36;
    			 float rads = p.frameCount * 0.004f + i * segmentRads; 
    			 float radius = i * 0.5f;
    			 if(i%2 == 0) {
    			 	cells[i].setPosition(pg.width/2 + P.cos(rads) * radius, pg.height/2 + P.sin(rads) * radius);
    			 } else {
    			 	cells[i].setPosition(pg.width/2 + P.cos(-rads) * radius*1.1f, pg.height/2 + P.sin(-rads) * radius*1.1f);
    			 }
	         }

			// circles
	        if(KeyboardState.keyOn('3')) {
    			 float numVertices = 28;
    			 float segmentRads = P.TWO_PI / numVertices;
    			 float rads = i * segmentRads; 
    			 float radius = P.floor(i / numVertices) * segmentRads * 250f;
    			 float offsetX = FrameLoop.noiseLoop(0.9f, i) * 0.1f;
    			 float offsetY = FrameLoop.noiseLoop(0.9f, i) * 0.1f;
    			 float offsetScale = FrameLoop.noiseLoop(0.9f, i*2) * 0.4f;
    			 radius *= 1f + offsetScale;
    			 cells[i].setPosition(pg.width/2 + P.cos(rads + offsetX) * radius, pg.height/2 + P.sin(rads + offsetY) * radius);
	        }
			
			cells[i].show();
			cells[i].advance();
		}
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
	}


	public class Cell {
		protected float x, y, sx, sy;
		protected PShape shape;

		Cell(float x, float y, float sx, float sy) {
			this.x = x;
			this.y = y; 
			this.sx = sx; 
			this.sy = sy;
			randomDirection();
		}
		
		protected void randomDirection() {
			this.sy = p.random(0.25f, 1f);
			this.sx = 0; //p.random(-0.5f, 0.5f);
//			this.sx = P.map(this.x, 0, pg.width, -1f, 1f);
		}
		
		public Cell setPosition(float x, float y) {
			this.x = x;
			this.y = y; 
			return this;
		}
		public float x() { return x; }
		public float y() { return y; }

		public void show() {
			if(shape == null) buildShape();
			pg.pushMatrix();
			pg.translate(this.x, this.y, 0);
			pg.shape(shape);
			pg.popMatrix();
		}

		protected void buildShape() {
			float rads = 0;
			float segmentRads = P.TWO_PI / UI.value(CELL_DETAIL);
			float rFix = hoffOrthoFactor * 0.5f;

			shape = p.createShape();
			shape.beginShape();
			for (int i = 0; i < UI.value(CELL_DETAIL); i++) {
				rads = segmentRads * i;
				shape.vertex(0, 0, -rFix);
				shape.vertex(hoffOrthoFactor * cos(rads), hoffOrthoFactor * sin(rads), -hoffOrthoFactor);
				shape.vertex(hoffOrthoFactor * cos(rads + segmentRads), hoffOrthoFactor * sin(rads + segmentRads), -hoffOrthoFactor);
			}
			shape.endShape();
			shape.disableStyle();
		}

		public void advance() {
//			this.sx = 0;
//			this.sy = 1;
			this.x += this.sx;
			this.y += this.sy;
//			if(this.y > 0) this.sy += this.y * 0.00015f;
			if (this.x < 0 || this.x > pg.width) {
				this.sx = -this.sx;
			}     
//			if (this.y < 0 || this.y > pg.height * 2) {
			if (this.y > pg.height * 2) {
//				this.sy = -this.sy;
				this.y = -pg.height * 0.1f;
				randomDirection();
			}
		}
	}
}
