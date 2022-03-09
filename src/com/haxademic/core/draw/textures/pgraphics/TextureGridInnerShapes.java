package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;

import processing.core.PGraphics;

public class TextureGridInnerShapes
extends BaseTexture {

	protected int cols = 4;
	protected int rows = 4;
	protected float gridW = 100;
	protected float gridH = 100;
	protected float cellW = 100;
	protected float cellH = 100;
	protected float gridCellSpacing = 10;
	protected ArrayList<GridCell> gridCells;
	
	protected EasingFloat speed = new EasingFloat(1f, 0.1f);
	protected float frames = 0;
	protected EasingFloat wobbleFreq = new EasingFloat(0.01f, 0.1f);
	protected EasingFloat wobbleAmp = new EasingFloat(0.1f, 0.1f);
	protected EasingFloat spacing = new EasingFloat(50, 0.1f);
	protected EasingFloat lineWeight = new EasingFloat(2, 6);

	protected int colorGroup = 0;
	
	public TextureGridInnerShapes( int width, int height ) {
		super(width, height);
		buildGrid();
	}
	
	protected void buildGrid() {
		// layout calcs
		gridW = width * 0.9f;
		gridH = height * 0.9f;
		gridCellSpacing = P.min(height, width) * 0.05f;
		cellW = (gridW - gridCellSpacing * (cols - 1)) / cols; 
		cellH = (gridH - gridCellSpacing * (rows - 1)) / rows; 
		
		// create cells
		gridCells = new ArrayList<GridCell>();
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float cellX = width/2 - gridW/2 + x * (cellW + gridCellSpacing);
				float cellY = height/2 - gridH/2 + y * (cellH + gridCellSpacing);
				gridCells.add(new GridCell(cellX, cellY, cellW, cellH));
			}
		}
	}
	
	public void preDraw() {
		colorGroup = 15; // 0, 13, 14, 15, 20
		// draw grid
		for (int i = 0; i < gridCells.size(); i++) {
			gridCells.get(i).drawPre();
		}
	}
	
	public void updateDraw() {
		// draw transition result to texture
		_texture.background(0);
		_texture.stroke(255);
//		lineWeight.update();
//		_texture.strokeWeight(lineWeight.value());
		
		// context & camera
//		PG.setCenterScreen(_texture);
//		PG.setDrawCenter(_texture);
//		_texture.ortho();
		
		// hexagon tilt
//		_texture.rotateZ(P.PI * 0.25f); // Mouse.yNorm

//		speed.update(true);
//		frames += speed.value();
//		wobbleAmp.update(true);
//		wobbleFreq.update(true);
//		spacing.update(true);
		
		// draw grid
		for (int i = 0; i < gridCells.size(); i++) {
			gridCells.get(i).draw();
		}
	}
	
	public void updateTiming() {
		for (int i = 0; i < 1; i++) {
			gridCells.get(MathUtil.randIndex(gridCells.size())).advance();
		}
	}
	
	public void updateTimingSection() {
		wobbleFreq.setTarget(MathUtil.randRangeDecimal(0.001f, 0.01f));
		wobbleAmp.setTarget(MathUtil.randRangeDecimal(-0.2f, 0.2f));
		spacing.setTarget(MathUtil.randRangeDecimal(30, 70));
	}
	
	public void newLineMode() {
		lineWeight.setTarget(MathUtil.randRange(1, 12));
	}

	
	public class GridCell {
		
		public float x, y, w, h;
		protected ArrayList<CellShape> shapes;
		protected int curShapeIndex = 0;
		protected LinearFloat curProgress = new LinearFloat(0, 0.025f);
		protected int numShapes = 10;
		public PGraphics cellPG;
		protected int lastAdvanceTime = 0;

		public GridCell(float x, float y, float w, float h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			cellPG = PG.newPG((int)w, (int)h);
			
			shapes = new ArrayList<CellShape>();
			for (int i = 0; i < numShapes; i++) {
				float progress = i / (numShapes + 1);
				shapes.add(new CellShape(this, i));
			}
		}
		
		public void resetAll() {
			for (int i = 0; i < shapes.size(); i++) {
				shapes.get(i).reset();
			}
		}
		
		public void advance() {
			if(MathUtil.randBooleanWeighted(0.05f)) {
				resetAll();
				lastAdvanceTime = -9999; // make sure we animate onto screen
			}
			
			int curTime = P.p.millis();
			if(curTime < lastAdvanceTime + 1500) return;
			lastAdvanceTime = curTime;

			curProgress.setCurrent(1);
			curProgress.setTarget(0);
			
			// cycle to next shape
			curShapeIndex = (curShapeIndex + 1) % numShapes;
			shapes.get(curShapeIndex).reset();
			// tell all shapes to advance
			for (int i = 0; i < shapes.size(); i++) {
				shapes.get(i).advance();
			}
		}
		
		public void drawPre() {
			curProgress.update();
			cellPG.beginDraw();
			cellPG.background(0);
			for (int i = 0; i < shapes.size(); i++) {
				// draw oldest to newest
				int loopIndex = ((curShapeIndex + 1) + i) % shapes.size();
				shapes.get(loopIndex).draw();
			}
			cellPG.endDraw();
		}
		
		public void draw() {
			for (int i = 0; i < shapes.size(); i++) {
				_texture.image(cellPG, x, y);
				
				// thickening stroke
//				_texture.noFill();
//				_texture.stroke(ColorsHax.colorFromGroupAt(colorGroup, 1));
//				_texture.strokeWeight(2 + curProgress.value() * 6f);
//				_texture.strokeWeight(2);
//				_texture.rect(x, y, w, h);
			}
		}
	}
	
	public class CellShape {
		
		protected GridCell cell;
		protected int index = 0; 
		protected EasingFloat x = new EasingFloat(0, 0.15f); 
		protected EasingFloat y = new EasingFloat(0, 0.15f); 
		protected EasingFloat w = new EasingFloat(0, 0.15f); 
		protected EasingFloat h = new EasingFloat(0, 0.15f); 
		protected int shapeMode = 0;
		
		public CellShape(GridCell cell, int index) {
			this.cell = cell;
			this.index = index;
			reset();
		}
		
		public void reset() {
			float newX = MathUtil.randRange(0, 2) * (cell.w / 2);
			float newY= MathUtil.randRange(0, 2) * (cell.h / 2);
			x.setCurrent(newX).setTarget(newX);
			y.setCurrent(newY).setTarget(newY);
			w.setCurrent(0).setTarget(0);
			h.setCurrent(0).setTarget(0);
			shapeMode = MathUtil.randRange(0, 1);
		}
		
		public void advance() {
			if(MathUtil.randBooleanWeighted(0.5f)) {
				x.setTarget(MathUtil.randRange(0, 2) * (cell.w / 2));
				y.setTarget(MathUtil.randRange(0, 2) * (cell.h / 2));
				// when first appearing, don't slide around, mostly
				if(w.target() == 0) { // && MathUtil.randBooleanWeighted(0.75f)) {
					x.setCurrent(x.target());
					y.setCurrent(y.target());
				}
			}
			if(MathUtil.randBooleanWeighted(0.75f)) {
				w.setTarget(w.target() + cell.w/2);
				h.setTarget(h.target() + cell.h/2);
			}
		}
		
		public void draw() {
			// update lerping numbers
			x.update(true);
			y.update(true);
			w.update(true);
			h.update(true);
			
			
			// draw shape 1 - circle
			PGraphics pg = cell.cellPG;
			if(shapeMode == 0) {
				pg.push();
				PG.setDrawCenter(pg);
				pg.fill(ColorsHax.colorFromGroupAt(colorGroup, index));
				pg.noStroke();
				pg.translate(x.value(), y.value());
				pg.ellipse(0, 0, w.value(), h.value());
				pg.pop();
			} else {
				// draw shape 1 - rect
				pg.push();
				PG.setDrawCenter(pg);
				pg.fill(ColorsHax.colorFromGroupAt(colorGroup, index));
				pg.noStroke();
				pg.translate(x.value(), y.value());
				pg.rect(0, 0, w.value(), h.value());
				pg.pop();
			}
		}
	}
}
