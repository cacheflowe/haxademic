package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class GridHelper {

	public String id;
	public static String GRID_ROWS = "GRID_ROWS_";
	public static String GRID_COLS = "GRID_COLS_";
	public static String GRID_BRIGHTNESS = "GRID_BRIGHTNESS_";
	public static String GRID_ALPHA = "GRID_ALPHA_";
	
	public GridHelper(String id) {
		this.id = id;
		
		UI.addTitle("Grid :: " + id);
		UI.addSlider(GRID_COLS + id, 20, 1, 100, 1, false);
		UI.addSlider(GRID_ROWS + id, 14, 1, 100, 1, false);
		UI.addSlider(GRID_BRIGHTNESS + id, 255, 0, 255, 1, false);
		UI.addSlider(GRID_ALPHA + id, 255, 0, 255, 1, false);
	}
	
	public void draw(PGraphics pg) {
		PG.drawGrid(pg, 0x00000000, P.p.color(UI.value(GRID_BRIGHTNESS + id), UI.value(GRID_ALPHA + id)), UI.value(GRID_COLS + id), UI.value(GRID_ROWS + id), 1, false);
	}
	
}
