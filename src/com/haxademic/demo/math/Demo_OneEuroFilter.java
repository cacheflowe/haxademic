package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.TimePlot;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.OneEuroFilter;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

public class Demo_OneEuroFilter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// default setup operates with normalized input (0-1)
	// lower cutoff/beta values = smoother, but slower lerp
	protected OneEuroFilter f;
	protected float frequency = 60;
	protected float mincutoff = 1f;
	protected float beta = 1f;
	protected float dcutoff = 1f;   

	// graphics plots
	protected int plotW = 512;
	protected int plotH = 128;
	protected TimePlot plotYInput;
	protected TimePlot plotYSmooth;

	// UI
	protected String FREQUENCY = "FREQUENCY";
	protected String MINCUTOFF = "MINCUTOFF";
	protected String BETA = "BETA";
	protected String DCUTOFF = "DCUTOFF";
	protected String NOISE_ADD = "NOISE_ADD";

	protected void config() {
		Config.setAppSize(960, 600);
	}

	protected void firstFrame() {
		// init filter
		try {
			f = new OneEuroFilter(frequency, mincutoff, beta, dcutoff);
			P.out("""
				#SRC OneEuroFilter.java
				#CFG {'beta': %f, 'freq': %f, 'dcutoff': %f, 'mincutoff': %f}
				#LOG timestamp, signal, noisy, filtered
				""".formatted(beta, frequency, dcutoff, mincutoff)
			);
		} catch (Exception e) { e.printStackTrace(); }

		// build plots
		plotYInput = new TimePlot(plotW, plotH, 0, 1);
		plotYSmooth = new TimePlot(plotW, plotH, 0, 1);

		// add UI
		UI.addTitle("OneEuroFilter");
		UI.addSlider(FREQUENCY, 60, 1, 240, 1, false);
		UI.addSlider(MINCUTOFF, 1, 0.01f, 100, 0.01f, false);
		UI.addSlider(BETA, 1, 0.01f, 100, 0.01f, false);
		UI.addSlider(DCUTOFF, 1, 0.01f, 100, 0.01f, false);
		UI.addSlider(NOISE_ADD, 0.2f, 0, 1, 0.01f, false);
	}

	protected void drawApp() {
		background(0);

		// process input
		try {
			f.setBeta(UI.value(BETA));
			f.setDerivateCutoff(UI.value(DCUTOFF));
			f.setFrequency(UI.value(FREQUENCY));
			f.setMinCutoff(UI.value(MINCUTOFF));

			float addedNoise = p.random(-UI.value(NOISE_ADD), UI.value(NOISE_ADD));
			// if(FrameLoop.frameModLooped(30)) addedNoise *= 10; // add intermittent really bad noise - doesn't work well
			float input = (1f - Mouse.yNorm) + addedNoise;
			float smoothedInput = f.filter(input, p.frameCount / UI.value(FREQUENCY)); // 60hz by default
			
			plotYInput.update(input);
			plotYSmooth.update(smoothedInput);
		} catch (Exception e) { e.printStackTrace(); }

		// draw plots
		PG.setCenterScreen(p.g);
		p.text("Noisy input", -plotW / 2, -200);
		p.text("Filtered input", -plotW / 2, 20);
		PG.setDrawCenter(p.g);
		DemoAssets.setDemoFont(p.g);
		p.image(plotYInput.image(), 0, -120);
		p.image(plotYSmooth.image(), 0, 100);
	}

}
