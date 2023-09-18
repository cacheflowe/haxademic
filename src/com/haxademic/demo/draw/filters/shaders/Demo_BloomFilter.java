package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_BloomFilter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics pgCopy;

	protected String BLOOM_STRENGTH = "BLOOM_STRENGTH";
	protected String BLOOM_ITERS = "BLOOM_ITERS";
	protected String BLOOM_BLEND_MODE = "BLOOM_BLEND_MODE";

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		pgCopy = PG.newPG(pg.width, pg.height);

		UI.addTitle("BLOOM");
		UI.addSlider(BLOOM_STRENGTH, 5, 0, 20, 0.01f, false);
		UI.addSlider(BLOOM_ITERS, 20, 0, 40, 1, false);
		UI.addSlider(BLOOM_BLEND_MODE, 0, 0, 3, 1, false);
	}

	protected void drawCubeToClearBuffer() {
		pg.beginDraw();
		pg.clear();
		// pg.background(0,0); // setting clear color will impact compositing!
		pg.background(255,0); // setting clear color will impact compositing! especially w/add blend mode
		PG.setCenterScreen(pg);
		PG.setBetterLights(pg);
		pg.fill(180 + 55f * P.sin(p.frameCount * 0.02f), 180 + 55f * P.sin(p.frameCount * 0.03f), 180 + 55f * P.sin(p.frameCount * 0.04f), 255);
		pg.strokeWeight(3);
		pg.stroke(0);
		pg.rotateX(p.frameCount * 0.01f);
		pg.rotateY(p.frameCount * 0.02f);
		pg.box(200 + 170f * P.sin(p.frameCount * 0.04f), 200 + 50f * P.sin(p.frameCount * 0.01f), 200 + 50f * P.sin(p.frameCount * 0.02f));
		pg.endDraw();

		// make a copy of the pg for extra compositing options later
		pgCopy.beginDraw();
		pgCopy.clear();
		ImageUtil.copyImage(pg, pgCopy);
		pgCopy.endDraw();

		DebugView.setTexture("pg", pg);
	}

	protected void doBloom() {
		BloomFilter.instance().setStrength(UI.value(BLOOM_STRENGTH));
		BloomFilter.instance().setBlurIterations(UI.valueInt(BLOOM_ITERS));
		BloomFilter.instance().setBlendMode(UI.valueInt(BLOOM_BLEND_MODE));
		BloomFilter.instance().applyTo(pg);

		DebugView.setValue("Bloom blend mode", UI.valueInt(BLOOM_BLEND_MODE));
		DebugView.setTexture("Bloom glow", BloomFilter.instance().glowTextureFor(pg));
	}

	protected void drawApp() {
		p.background(100, 0, 0);

		drawCubeToClearBuffer();
		doBloom();
		
		// draw to screen
		p.image(pg, 0, 0);

		PGraphics glowOnly = BloomFilter.instance().glowTextureFor(pg);
		// p.blendMode(PBlendModes.ADD);
		// p.image(glowOnly, 0, 0);
		// p.blendMode(PBlendModes.BLEND);
		// p.image(pgCopy, 0, 0);

		
		// post 
		GrainFilter.instance().setTime(p.frameCount * 0.01f);
		GrainFilter.instance().setCrossfade(0.03f);
		GrainFilter.instance().applyTo(p);
	}
}
