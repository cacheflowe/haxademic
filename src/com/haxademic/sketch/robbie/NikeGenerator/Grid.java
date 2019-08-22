package com.haxademic.sketch.robbie.NikeGenerator;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.Shapes;

import processing.core.PGraphics;
import processing.core.PImage;

public class Grid {
	
	protected int tileSize;
	protected int gridColor;
	protected int strokeWeight;
	protected float dash;
	protected float dotSize;
	protected Boolean isDashed;
	protected PGraphics tile;
	protected TiledTexture tiledImg;
	
	public Grid(int tileSize, int gridColor, int strokeWeight) {	
		this.tileSize = tileSize;
		this.gridColor = gridColor;
		this.strokeWeight = strokeWeight;
		isDashed = false;
		tile = PG.newPG(tileSize, tileSize);
		drawTile(false);
		tiledImg = new TiledTexture(tile);
	}
	
	public Grid(int tileSize, int gridColor, int strokeWeight, int dash) {	
		isDashed = true;
		this.tileSize = tileSize;
		this.gridColor = gridColor;
		this.strokeWeight = strokeWeight;
		this.dash = tileSize/dash;
		dotSize = strokeWeight*2;
		tile = PG.newPG(tileSize, tileSize);
		drawTile(true);
		tiledImg = new TiledTexture(tile);
	}
	
	public PImage drawTile(boolean isDashed) {
		if (isDashed) {
			tile.beginDraw();
			tile.stroke(gridColor);
			tile.strokeWeight(strokeWeight);

			// top
			Shapes.drawDashedLine(tile, 0, strokeWeight/2, 0, tile.width + strokeWeight, strokeWeight/2, 0, dash, true);
			// left
			tile.strokeWeight(strokeWeight*2);
			Shapes.drawDashedLine(tile, 0, 0, 0, 0, tile.height + strokeWeight, 0, dash, true);
			// dot
			tile.fill(gridColor);
			tile.noStroke();
			tile.square(tile.width/2 - dotSize/2, tile.height/2 - dotSize/2, dotSize);
			tile.endDraw();
		} else {
			tile.beginDraw();
			tile.stroke(gridColor);
			tile.strokeWeight(strokeWeight);
			// top
			tile.line(0, strokeWeight/2, tile.width + strokeWeight, strokeWeight/2);
			// left
			if (strokeWeight == 1)
				tile.line(strokeWeight, 0, strokeWeight, tile.height + strokeWeight);
			else
				tile.line(strokeWeight/2, 0, strokeWeight/2, tile.height + strokeWeight);
			tile.endDraw();
		}
		
		return tile;
	}

	public void draw(PGraphics pg, float x, float y, int cols, int rows) {
		// draw tiles
		pg.pushMatrix();
		pg.translate((tileSize * cols)/2f + x, (tileSize * rows)/2f + y);
		tiledImg.setSource(tile);
		tiledImg.setOffset(cols % 2 == 0 ? -0.5f : 0 , rows % 2 == 0 ? -0.5f : 0);
		tiledImg.setSize(1, 1);
		tiledImg.update();
		tiledImg.drawCentered(pg, cols * tileSize, rows * tileSize);
		
		// draw edge lines
		if (isDashed) {
			pg.popMatrix();
			pg.stroke(gridColor);
			pg.strokeWeight(strokeWeight);
			// bot
			Shapes.drawDashedLine(pg, x, y -strokeWeight/2 + rows*tileSize, 0, x + tileSize*cols, y -strokeWeight/2 + rows*tileSize, 0, dash, true);
			// right
			Shapes.drawDashedLine(pg, x + cols*tileSize-strokeWeight/2, y, 0, x + cols*tileSize-strokeWeight/2, y + tileSize*rows, 0, dash, true);
		} else {			
			pg.stroke(gridColor);
			pg.strokeWeight(strokeWeight);
			// bot
			pg.line(-(tileSize*cols)/2, (tileSize*rows)/2, (tileSize*cols)/2 + strokeWeight/2, (tileSize*rows)/2);
			// right
			pg.line((tileSize*cols)/2, -(tileSize*rows)/2, (tileSize*cols)/2, (tileSize*rows)/2 + strokeWeight/2);
			pg.popMatrix();
		}
		
	}
	
	

}
