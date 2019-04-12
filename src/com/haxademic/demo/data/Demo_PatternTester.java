package com.haxademic.demo.data;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.patterns.ISequencerPattern;
import com.haxademic.core.data.patterns.PatternInterval;
import com.haxademic.core.data.patterns.PatternNoise;
import com.haxademic.core.data.patterns.PatternRandom;
import com.haxademic.core.data.patterns.PatternSine;

public class Demo_PatternTester
extends PAppletHax { 
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean[][] steps;
	protected ISequencerPattern[] patterns;
	protected String[] patternNames;
	protected int NUM_GENERATORS = 5;
	protected int NUM_STEPS = 16;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, false );
	}

	public void setupFirstFrame() {
		// build steps
		steps = new boolean[NUM_GENERATORS][];
		for (int i = 0; i < steps.length; i++) {
			steps[i] = new boolean[NUM_STEPS];
			for (int j = 0; j < steps[i].length; j++) {
				steps[i][j] = false;
			}
		}
		
		// build pattern generators
		patterns = new ISequencerPattern[] {
			new PatternNoise(),
			new PatternSine(),
			new PatternInterval(),
			new PatternRandom(0.5f, 4),
			new PatternRandom(0.3f, 1),
		};
		
		// build pattern display names
		patternNames = new String[] {
			"PatternNoise",
			"PatternSine",
			"PatternInterval",
			"PatternRandom(0.5f, 4)",
			"PatternRandom(0.3f, 1)",
		};
	}
	
	protected void applyPatterns() {
		for (int i = 0; i < patterns.length; i++) {
			patterns[i].newPattern(steps[i]);
		}
	}

	public void drawApp() {
		background(0);
		
		// apply pattern generators
		if(p.frameCount % 100 == 10) applyPatterns();
		//		patterns[0].newPattern(steps[0]);
		//		patterns[1].newPattern(steps[1]);
		//		patterns[2].newPattern(steps[2]);
		
		// show values
		int stepSize = p.width / NUM_STEPS;
		for (int i = 0; i < patterns.length; i++) {
			int rowY = i * 100;
			int rowCellY = rowY + 20;
			int rowX = 0;
			p.stroke(255);
			
			// text
			p.fill(255);
			p.text(patternNames[i], rowX, rowY, p.width, 40);
			
			// draw grid
			for (int j = 0; j < steps[i].length; j++) {
				if(steps[i][j] == true) {
					p.fill(30,200,200);
				} else {
					p.fill(50);
				}
				p.rect(rowX, rowCellY, stepSize, stepSize);
				rowX += stepSize;
			}
			
			// draw source value
			p.fill(255, 127);
			p.noStroke();
			for (int j = 0; j < p.width; j += 1) {
				float sourceVal = patterns[i].valueForStep(P.map(j, 0, p.width, 0, NUM_STEPS), NUM_STEPS);
				p.rect(j - 1, rowCellY + stepSize - (stepSize * sourceVal) - 1, 2, 2);
			}
		}

	}
}
