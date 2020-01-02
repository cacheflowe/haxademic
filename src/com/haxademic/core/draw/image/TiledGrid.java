package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;

import processing.core.PConstants;
import processing.core.PGraphics;

public class TiledGrid {

	protected PGraphics gridCell;

	protected int tileSize;
	protected int colorBg;
	protected int colorStroke;
	protected float strokeWeight;
	protected float offsetX = 0;
	protected float offsetY = 0;
	
	public TiledGrid(int tileSize, int colorBg, int colorStroke, float strokeWeight) {
		this.tileSize = tileSize;
		this.colorBg = colorBg;
		this.colorStroke = colorStroke;
		this.strokeWeight = strokeWeight;
		
		// draw repeating grid cell
		gridCell = PG.newPG(tileSize, tileSize, false, true);
		gridCell.beginDraw();
		gridCell.noStroke();
		gridCell.background(colorBg);
		gridCell.fill(colorStroke);
		gridCell.rect(0, 0, gridCell.width, strokeWeight);
		gridCell.rect(0, 0, strokeWeight, gridCell.height);
		gridCell.endDraw();
	}
	
	public void draw(PGraphics pg, float cols, float rows) {
		draw(pg, cols, rows, false);
	}
	
	public void draw(PGraphics pg, float cols, float rows, boolean drawOutline) {
		int prevRectMode = pg.rectMode;
		PG.setTextureRepeat(pg, true);
		pg.pushMatrix();
		float drawW = cols * tileSize + 1;
		float drawH = rows * tileSize + 1;
		pg.noStroke();
		pg.beginShape();
		pg.textureMode(P.IMAGE);
		pg.texture(gridCell);
		if(prevRectMode == PConstants.CENTER) pg.translate(P.round(-drawW/2), P.round(-drawH/2));
		pg.vertex(0, 0, 0,					offsetX * tileSize + 0, 			offsetY * tileSize + 0);
		pg.vertex(drawW + 1, 0, 0, 			offsetX * tileSize + drawW + 1, 	offsetY * tileSize + 0);
		pg.vertex(drawW + 1, drawH + 1, 0, 	offsetX * tileSize + drawW + 1, 	offsetY * tileSize + drawH + 1);
		pg.vertex(0, drawH + 1, 0, 			offsetX * tileSize + 0, 			offsetY * tileSize + drawH + 1);
		pg.endShape();
		
		if(drawOutline) {
			pg.rectMode(PConstants.CORNER); // make sure rect is drawing from the same top left
			pg.fill(colorStroke);
			pg.rect(0, 0, drawW, strokeWeight);	// top
			pg.rect(0, drawH + 1 - strokeWeight, drawW, strokeWeight);	// bottom
			pg.rect(0, 0, strokeWeight, drawH);	// left
			pg.rect(drawW + 1 - strokeWeight, 0, strokeWeight, drawH);	// right
			pg.rectMode(prevRectMode);		// reset rect mode to whatever it was before
		}
		
		pg.popMatrix();
	}
	
	// public
	
	public int tileSize() {
		return tileSize;
	}
	
	public float offsetX() {
		return offsetX;
	}
	
	public TiledGrid offsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}
	
	public float offsetY() {
		return offsetY;
	}
	
	public TiledGrid offsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}
	
}
