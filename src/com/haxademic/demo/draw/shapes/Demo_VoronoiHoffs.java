package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.ui.UI;

public class Demo_VoronoiHoffs 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// from: https://openprocessing.org/sketch/352817/
	// with some adjustments to work in newer Processing
	
	protected float hoffOrthoFactor;
	protected String NUM_CELLS = "NUM_CELLS";
	protected String CELL_DETAIL = "CELL_DETAIL";
	protected int MAX_CELLS = 1000;
	protected Cell cells[] = new Cell[1000];

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 1280 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}

	protected void firstFrame() {
		UI.addTitle("VORONOI");
		UI.addSlider(NUM_CELLS, 30, 3, MAX_CELLS, 1, false);
		UI.addSlider(CELL_DETAIL, 30, 3, 100, 1, false);
		
		//		WebCam.instance().setDelegate(this);
//		pg = PG.newPG(p.width, p.height, false, false);
//		pg.noSmooth();
		
		for (int i = 0; i < MAX_CELLS; i++) {
			cells[i] = new Cell(
				random(width), random(height), 
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
//			pg.fill(i % 2 == 0 ? 0 : 255);
			cells[i].show();
			cells[i].advance();
		}
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
	}


	public class Cell {
		protected float x, y, sx, sy;

		Cell(float x, float y, float sx, float sy) {
			this.x = x;
			this.y = y; 
			this.sx = sx; 
			this.sy = sy;
		}

		void show() {
			pg.pushMatrix();
			pg.translate(this.x, this.y, 0);
			float rads = 0;
			float segmentRads = P.TWO_PI / UI.value(CELL_DETAIL);
			float rFix = hoffOrthoFactor * 0.5f;
			pg.beginShape();
			for (int i = 0; i < UI.value(CELL_DETAIL); i++) {
//				pg.fill(sin(this.x/70 + i/3) * 127 + 127, sin(this.y/70 + i/3+1) * 127 + 127, sin(this.y/70 + i/1+2) * 127 + 127);

				rads = segmentRads * i;
				pg.vertex(0, 0, -rFix);
				pg.vertex(hoffOrthoFactor * cos(rads), hoffOrthoFactor * sin(rads), -hoffOrthoFactor);
				pg.vertex(hoffOrthoFactor * cos(rads + segmentRads), hoffOrthoFactor * sin(rads + segmentRads), -hoffOrthoFactor);
			}
			pg.endShape();
			pg.fill(0);
//			pg.ellipse(0, 0, 5, 5);
			pg.popMatrix();
		}

		void advance() {
//			this.sx = 0;
//			this.sy = 1;
			this.x += this.sx;
			this.y += this.sy;
			if (this.x < 0 || this.x > pg.width) {
				this.sx = -this.sx;
			}     
//			if (this.y < 0 || this.y > pg.height * 2) {
			if (this.y > pg.height * 2) {
//				this.sy = -this.sy;
				this.y = -pg.height;
				this.sy = p.random(0.5f, 6f);
				this.sx = p.random(-1f, 1f);
			}
		}
	}
}
