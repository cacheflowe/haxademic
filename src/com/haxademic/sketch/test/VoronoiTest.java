package com.haxademic.sketch.test;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;

import processing.core.PVector;

public class VoronoiTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// https://www.openprocessing.org/sketch/61968#

	ArrayList<Cell> cells = new ArrayList<Cell>();
	boolean use_manhattan = false;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 500 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 500 );
	}

	public void setup() {
		super.setup();	
		for(int i = 0; i < 5; ++i)
			cells.add(new Cell((int)random(0,width), (int)random(0,height), color(random(0,255), random(0,255), random(0,255))));

	}

	public void drawApp() {
		cells.get(0).loc.x = mouseX;
		cells.get(0).loc.y = mouseY;
		background(0);

		// Draw the voronoi cells
		drawVoronoi();

		// Draw the points
		for(int i = 0; i < cells.size(); ++i)
			ellipse(cells.get(i).loc.x, cells.get(i).loc.y, 5, 5);

	}

	class Cell
	{
		PVector loc;
		int c;
		Cell(int _x, int _y, int _c) {
			loc = new PVector(_x, _y);
			c = _c;
		}
	}

	void drawVoronoi()
	{
		loadPixels();
		int offset = 0;

		// Iterate over every pixel
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				float shortest = Integer.MAX_VALUE;
				int index = -1;

				// Find the closest cell
				for(int i = 0; i < cells.size(); ++i) {
					Cell cc = cells.get(i);
					float d;

					if(use_manhattan) {
						// Manhattan Distance
						d = abs(x - cc.loc.x) + abs(y - cc.loc.y);
					} else {
						// Euclidean distance, dont need to sqrt it since actual distance isnt important
						d = sq(x-cc.loc.x) + sq(y-cc.loc.y);
					}

					if(d < shortest) {
						shortest = d;
						index = i;
					}
				}
				// Set the pixel to the cells color
				pixels[offset++] = cells.get(index).c;
			}
		}
		updatePixels();
	}

	public void keyPressed() {
		if(key == 'a')
			cells.add(new Cell(mouseX, mouseY, color(random(0,255), random(0,255), random(0,255))));
		if(key == ' ')
			cells.subList(5, cells.size()).clear();
		if(key == 't')
			use_manhattan = !use_manhattan;
	}
}
