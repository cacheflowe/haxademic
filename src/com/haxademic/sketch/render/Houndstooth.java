package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.camera.CameraUtil;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class Houndstooth 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics bwTexture;
	protected PGraphics houndsTooth4x4;
	protected HoundstoothCell[] cells;
	protected TiledTexture finalPatternTileRepeat;
	protected EasingFloat tileSize = new EasingFloat(1, 0.085f);
	protected EasingFloat tileRot = new EasingFloat(0, 0.085f);

	protected int[][] houndstoothPattern = { 
		{2, 4, 0, 0},
		{5, 5, 2, 4},
		{5, 5, 4, 2},
		{4, 2, 0, 0},
	};
	protected int[][] bigHalfPattern = { 
		{0, 0, 0, 1},
		{0, 0, 1, 5},
		{0, 1, 5, 5},
		{1, 5, 5, 5},
	};
	protected int[][] tinyHalfPattern = { 
		{1, 1, 1, 1},
		{1, 1, 1, 1},
		{1, 1, 1, 1},
		{1, 1, 1, 1},
	};
	protected int[][] diamondsPattern = {
		{3, 0, 0, 2},
		{0, 1, 4, 0},
		{0, 2, 3, 0},
		{4, 0, 0, 1},
	};
	protected int[][] grid45Pattern = {
		{3, 2, 3, 2},
		{4, 1, 4, 1},
		{3, 2, 3, 2},
		{4, 1, 4, 1},
	};
	protected int[][] curDisplays = houndstoothPattern;

	protected void overridePropsFile() {
		int FRAMES = 600;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1024);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}
	
	protected void setupFirstFrame() {
		// create rainbow buffer source
		drawBwTexture();
		buildGrid();
		finalPatternTileRepeat = new TiledTexture(houndsTooth4x4);
	}
	
	protected void drawBwTexture() {
		bwTexture = p.createGraphics(512, 512, P.P2D);
		bwTexture.beginDraw();
		bwTexture.noStroke();
		bwTexture.background(0);
		
		bwTexture.fill(255);
		bwTexture.beginShape();
		bwTexture.vertex(0, 0);
		bwTexture.vertex(bwTexture.width, 0);
		bwTexture.vertex(0, bwTexture.height);
		bwTexture.endShape();
		
		bwTexture.endDraw();
		
		p.debugView.setTexture(bwTexture);
	}
	
	protected void buildGrid() {		
		houndsTooth4x4 = p.createGraphics(pg.width, pg.height, P.P2D);
		cells = new HoundstoothCell[16];
		int cellSize = houndsTooth4x4.width / 4;
		for (int i = 0; i < cells.length; i++) {
			int colIndex = i % 4;
			int rowIndex = P.floor(i/4);
			cells[i] = new HoundstoothCell(rowIndex, colIndex, cellSize);
		}
		p.debugView.setTexture(houndsTooth4x4);
	}
	
	public void keyPressed() {
		super.keyPressed();
	}
		
	public void drawApp() {
//		defaultDisplays[0][0] = 3; 
//		defaultDisplays[0][1] = 2; 
//		defaultDisplays[0][2] = 3; 
//		defaultDisplays[0][3] = 2; 
//		defaultDisplays[1][0] = 4; 
//		defaultDisplays[1][1] = 1; 
//		defaultDisplays[1][2] = 4; 
//		defaultDisplays[1][3] = 1; 
//		defaultDisplays[2][0] = 3; 
//		defaultDisplays[2][1] = 2; 
//		defaultDisplays[2][2] = 3; 
//		defaultDisplays[2][3] = 2; 
//		defaultDisplays[3][0] = 4; 
//		defaultDisplays[3][1] = 1; 
//		defaultDisplays[3][2] = 4; 
//		defaultDisplays[3][3] = 1;

		
		int displays[][] = null;
		if(p.loop.progress() > 0.8f) {
			displays = bigHalfPattern;
			tileSize.setTarget(1);
			tileRot.setTarget(0);
		} else if(p.loop.progress() > 0.6f) {
			displays = grid45Pattern;
			tileSize.setTarget(2f);
			tileRot.setTarget(-P.HALF_PI);
		} else if(p.loop.progress() > 0.4f) {
			displays = diamondsPattern;
			tileSize.setTarget(3.5f);
			tileRot.setTarget(-P.QUARTER_PI);
		} else if(p.loop.progress() > 0.2f) {
			displays = houndstoothPattern;
			tileSize.setTarget(4f);
			tileRot.setTarget(0);
		} else {
			displays = tinyHalfPattern;
			tileSize.setTarget(4.5f);
			tileRot.setTarget(0);
		}
		
//		displays = defaultDisplays;
		if(curDisplays != displays) {
//		if(p.frameCount % 100 == 0) {
			curDisplays = displays;
			for (int i = 0; i < cells.length; i++) {
				cells[i].setDisplay(curDisplays);
			}
		}
		
		// draw 8x8 pattern
		houndsTooth4x4.beginDraw();
		for (int i = 0; i < cells.length; i++) {
			cells[i].update();
		}
		houndsTooth4x4.endDraw();
		
		// set up context
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		
		// draw single image
		pg.image(houndsTooth4x4, 0, 0);
		
		// draw repeating image 
		DrawUtil.setCenterScreen(pg);
		tileSize.update(true);
		tileRot.update(true);
		finalPatternTileRepeat.drawCentered(pg, pg.width, pg.height);
		finalPatternTileRepeat.setRotation(tileRot.value());
		finalPatternTileRepeat.setSize(tileSize.value(), tileSize.value());
		
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
	}
	
	public class HoundstoothCell {
		
		protected int col;
		protected int row;
		protected float size;
		protected int displayInt = 0;
		protected TiledTexture bwCellTile;
		
		protected EasingFloat easedRot = new EasingFloat(0, 0.15f);
		protected EasingFloat easedSize = new EasingFloat(0, 0.15f);
		protected EasingFloat easedOffsetX = new EasingFloat(0, 0.15f);
		protected EasingFloat easedOffsetY = new EasingFloat(0, 0.15f);
		protected PVector offsetTarget = new PVector();
		protected PVector offsetWhite = new PVector(-0.25f, -0.25f);
		protected PVector offsetBlack = new PVector(0.25f, 0.25f);

		public HoundstoothCell(int col, int row, float size) {
			this.col = col;
			this.row = row;
			this.size = size;
			bwCellTile = new TiledTexture(bwTexture);
		}
		
		protected float rotByDisplay(float index) {
			if(index == 0) return 0;
			else if(index == 1) return 0;
			else if(index == 2) return P.HALF_PI * 1f;
			else if(index == 3) return P.HALF_PI * 2f;
			else if(index == 4) return P.HALF_PI * 3f;
			else if(index == 5) return P.HALF_PI * 4f;
			return 0;
		}
		
		protected float sizeByDisplay(float index) {
			if(index == 0) return 0.5f;
			else if(index == 5) return 0.5f;
			else return 0.5f;
		}
		
		protected PVector offsetByDisplay(float index) {
			if(index == 0) return offsetWhite;
			else if(index == 5) return offsetBlack;
			else return offsetTarget;
		}
		
		public void setDisplay(int displays[][]) {
			displayInt = displays[col][row];
			int delay = 10 * (row + col);
			easedRot.setTarget(rotByDisplay(displayInt));
			easedRot.setDelay(delay);
			easedSize.setTarget(sizeByDisplay(displayInt));
			easedSize.setDelay(delay);
			easedOffsetX.setTarget(offsetByDisplay(displayInt).x);
			easedOffsetX.setDelay(delay);
			easedOffsetY.setTarget(offsetByDisplay(displayInt).y);
			easedOffsetY.setDelay(delay);
		}

		public void update() {
			// lerp values
			easedRot.updateRadians();
			easedSize.update(true);
			easedOffsetX.update(true);
			easedOffsetY.update(true);
			
			// draw tiled texture
			houndsTooth4x4.fill(255);
			bwCellTile.setOffset(easedOffsetX.value(), easedOffsetY.value());
			bwCellTile.setSize(0.75f, 0.75f);
			bwCellTile.setRotation(easedRot.value());
			houndsTooth4x4.pushMatrix();
			houndsTooth4x4.translate(size * col + size/2, size * row + size/2);
			bwCellTile.drawCentered(houndsTooth4x4, size, size);
			
			// debug text
//			houndsTooth4x4.fill(255,0,0);
//			houndsTooth4x4.text(col+", "+row+" - "+displayInt, 0, 0);
			
			houndsTooth4x4.popMatrix();
		}
	}
}