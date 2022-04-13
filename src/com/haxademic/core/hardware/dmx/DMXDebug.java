package com.haxademic.core.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class DMXDebug {

	protected int width = 960;
	protected int height = 540;
	protected PGraphics pg;
	
	public DMXDebug() {
		pg = PG.newPG(width, height);
	}
	
	public PGraphics buffer() {
		return pg;
	}
	
	public void updateRGB(int[] universe) {
		// 16 x 11 fits 171 rgb sets of 3 channels 
		int cols = 16;
		int rows = 11;
		int spacingX = width / cols;
		int spacingY = height / rows;
		
		pg.beginDraw();
		pg.background(0);
		for (int i = 0; i < universe.length; i+=3) {
			int gridX = MathUtil.gridXFromIndex(i/3, cols);
			int gridY = MathUtil.gridYFromIndex(i/3, cols);
			if(i < 510) {
				int cellX = gridX * spacingX;
				int cellY = gridY * spacingY;
				
				// draw color box
				pg.fill(
					universe[i + 0],
					universe[i + 1],
					universe[i + 2]
				);
				pg.rect(cellX, cellY, spacingX, spacingY);
				
				// draw label
				FontCacher.setFontOnContext(pg, DemoAssets.fontInter(13), P.p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
				pg.text(""+(i+1), cellX, cellY, spacingX, spacingY);
			}
		}
		PG.drawGrid(pg, 0x00000000, 0xff999999, cols, rows, 1, false);
		pg.endDraw();
	}
	
	public void updateSingleChannel(int[] universe) {
		// show all 512 channels
		int cols = 32;
		int rows = 16;
		int spacingX = width / cols;
		int spacingY = height / rows;
		
		pg.beginDraw();
		pg.background(0);
		for (int i = 0; i < universe.length; i++) {
			int gridX = MathUtil.gridXFromIndex(i, cols);
			int gridY = MathUtil.gridYFromIndex(i, cols);
			int cellX = gridX * spacingX;
			int cellY = gridY * spacingY;
			
			// draw color box
			pg.fill(universe[i]);
			pg.rect(cellX, cellY, spacingX, spacingY);
			
			// draw label
			FontCacher.setFontOnContext(pg, DemoAssets.fontInter(11), P.p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
			pg.text(""+(i+1), cellX, cellY, spacingX, spacingY);
		}
		PG.drawGrid(pg, 0x00000000, 0xff999999, cols, rows, 1, false);
		pg.endDraw();
	}
	
}
