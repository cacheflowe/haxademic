package com.haxademic.render;

import java.util.Arrays;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.FrameLoop;

public class WolframCA 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// ca props
	int w = 12;
	int[] cells;
	int cols;
	int generation = 0;
	int[] ruleset = new int[] {0, 1, 0, 1, 0, 1, 0, 0};
	int[] nextRow;
	int pixelScroll = 3;

	protected int FRAMES = 60 * 60;
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES);
	}
	
	protected void firstFrame() {
		// ca initial 1d cell values
		cols = P.floor(p.width / w);
		cells = new int[cols];
		nextRow = new int[cols];
		for (int i = 0; i < cells.length; i++) {
			cells[i] = (i % 10 == 0) ? 1 : 0;	// every ten pixels on
		}
		newCAValues();
		
		// context
		p.background(30);
		p.noStroke();
	}
	
	protected void drawApp() {
		// outro for render
		if(FrameLoop.loopCurFrame() > FRAMES - p.height/pixelScroll) {
			for (int i = 0; i < cells.length; i++) {
				cells[i] = 0;
			}
		}
		
		// set context
		for (int i = 0; i < pixelScroll; i++) {
			p.copy(0, 0, width, height, 0, -1, width, height);
		}

		for (int i = 0; i < cells.length; i++) {
			if (cells[i] == 1) {
				p.fill(235);
			} else {
				p.fill(30);
			}
			p.rect(i * w, height - 1, w, 1);
		}
		
		// build next row values when we've copied past the current row
		if(p.frameCount % (w/pixelScroll) == 1) {
			generateNextRow();
			checkIsBlankRow();
			if(generation % 8 == 0) newCAValues();
		}
	}
	
	protected void reSeed() {
		for (int i = 1; i < cells.length-1; i++) {
			cells[i] = (i % 10 == 0) ? 1 : 0;	// every ten pixels on
		}
		newCAValues();
	}

	protected void checkIsBlankRow() {
		  // if all black, change one to white
		  boolean allBlack = true;
		  for (int i = 1; i < cells.length-1; i++) if(cells[i] == 1) allBlack = false;
		  if(allBlack == true) {
			  reSeed();
		  }
		  
		  // if all white, change one to black
		  boolean allWhite = true;
		  for (int i = 1; i < cells.length-1; i++) if(cells[i] == 0) allWhite = false;
		  if(allWhite == true) {
			  reSeed();
		  }
	}

	protected void generateNextRow() {
	  // For every spot, determine new state by examining current state, and neighbor states
	  // Ignore edges that only have one neighbor
	  for (int i = 1; i < cells.length-1; i++) {
	    int left   = cells[i-1];   // Left neighbor state
	    int me     = cells[i];     // Current state
	    int right  = cells[i+1];   // Right neighbor state
	    nextRow[i] = rules(left, me, right); // Compute next generation state based on ruleset
	  }
	  // The current generation is the new generation
	  System.arraycopy(nextRow, 0, cells, 0, cells.length);
	  generation++;
	}
	
	protected void newCAValues() {
		  // every x rows, change the equation
//		  int genLoop = generation % ruleset.length;
		  // ruleset[genLoop] = (ruleset[genLoop] == 0) ? 1 : 0;
//		  if(generation % 1 == 0) ruleset[genLoop] = (Math.random() > 0.5) ? 1 : 0;
		
		// all random
		for (int i = 0; i < ruleset.length; i++) ruleset[i] = MathUtil.randRange(0, 1);
		// evolve 1
//			  int randIndex = MathUtil.randIndex(ruleset.length);
//			  ruleset[randIndex] = (ruleset[randIndex] + 1) % 2;
//			  P.out(Arrays.toString(ruleset));
		  // if(generation % 2 == 0) ruleset[genLoop] = (ruleset[genLoop] == 0) ? 1 : 0;
	}


	// Implementing the Wolfram rules
	// Could be improved and made more concise, but here we can explicitly see what is going on for each case
	protected int rules(int a, int b, int c) {
	  if (a == 1 && b == 1 && c == 1) return ruleset[0];
	  if (a == 1 && b == 1 && c == 0) return ruleset[1];
	  if (a == 1 && b == 0 && c == 1) return ruleset[2];
	  if (a == 1 && b == 0 && c == 0) return ruleset[3];
	  if (a == 0 && b == 1 && c == 1) return ruleset[4];
	  if (a == 0 && b == 1 && c == 0) return ruleset[5];
	  if (a == 0 && b == 0 && c == 1) return ruleset[6];
	  if (a == 0 && b == 0 && c == 0) return ruleset[7];
	  return 0;
	}


	
}