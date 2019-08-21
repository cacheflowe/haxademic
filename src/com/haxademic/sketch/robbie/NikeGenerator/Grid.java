package com.haxademic.sketch.robbie.NikeGenerator;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.sketch.robbie.BasicApp.BasicApp.App;

import processing.core.PGraphics;
import processing.core.PImage;

public class Grid
implements IAppStoreListener {
	
	protected BasicApp p;
	protected PGraphics pg;
	
	protected PGraphics tile;
	protected TiledTexture tiledImg;
	
	protected int gridWidth;
	protected int gridHeight;
	protected int tileWidth;
	protected int tileHeight;
	protected int sw;
	protected int dash;
	protected int tileColor;
	protected int sqSize;
	
	public Grid(int tileWidth, int tileHeight, int tileColor, int strokeWeight, int dash, int sqSize) {
		p = (BasicApp) P.p;
		pg = p.pg;
		P.store.addListener(this);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.sw = strokeWeight;
		this.dash = dash;
		this.sqSize = sqSize;
		this.tileColor = tileColor;
		tile = PG.newPG(tileWidth - sw/2, tileHeight - sw/2);
		tiledImg = new TiledTexture(tile());
	}
	
	public void drawPre(int frameCount) {}
	
	public PImage tile() {
		tile.beginDraw();
		tile.background(255);
		tile.stroke(tileColor);
		tile.stroke(255, 0, 0);
		tile.strokeWeight(sw);

		// top
		Shapes.drawDashedLine(tile, 0, sw/2, 0, tileWidth + sw, sw/2, 0, dash, true);
		// left
		tile.stroke(tileColor);
		tile.strokeWeight(sw*2);
		Shapes.drawDashedLine(tile, 0, 0, 0, 0, tileHeight + sw, 0, dash, true);
		// mid
		tile.fill(tileColor);
		tile.noStroke();
		tile.square(tileWidth/2 - sqSize/2, tileHeight/2 - sqSize/2, sqSize);

		tile.endDraw();
		return tile;
	}

	public void draw(int frameCount) {
//		pg.background(255);
//		tileWidth = 200;
//		tileHeight = tileWidth;
		tile();
		tiledImg.setSource(tile);
		pg.pushMatrix();
		pg.translate(p.width/2, p.height/2);
		float size = 1;
		float offsetX = ((float)sw/2)/(float)tileWidth;
		float offsetY = ((float)sw/2)/(float)tileHeight;
		tiledImg.setOffset(-0.5f + offsetX, -0.5f + offsetY);
		tiledImg.setSize(size, size);
		tiledImg.update();
		tiledImg.drawCentered(pg, p.width, p.height);
		pg.popMatrix();
		
//		setTileWidth((int)(P.sin(p.frameCount * 0.01f)*50 + 100));
//		setTileHeight((int)(P.sin(p.frameCount * 0.01f)*50 + 100));
//		P.out(tileWidth);
	}
	
	public void setTileWidth(int _tileWidth) {
		this.tileWidth = _tileWidth;
	}
	public void setTileHeight(int _tileHeight) {
		this.tileHeight = _tileHeight;
	}
	



	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////
	
	@Override
	public void updatedNumber(String key, Number val) {
		if(key.equals(App.ANIMATION_FRAME_PRE)) drawPre(val.intValue());
		if(key.equals(App.ANIMATION_FRAME)) draw(val.intValue());
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
