package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PGraphics;

public class Demo_BloomEffectVanilla
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics glowBuffer;

	protected void firstFrame() {
		glowBuffer = p.createGraphics(pg.width, pg.height, PRenderers.P2D);
	}

	protected void drawApp() {
		// draw cube to buffer
		pg.beginDraw();
		pg.clear();
//		pg.background(0);
		PG.setCenterScreen(pg);
		PG.setBetterLights(pg);
		pg.fill(180 + 55f * P.sin(p.frameCount * 0.02f), 180 + 55f * P.sin(p.frameCount * 0.03f), 180 + 55f * P.sin(p.frameCount * 0.04f), 255);
		pg.stroke(0);
		pg.rotateX(p.frameCount * 0.01f);
		pg.rotateY(p.frameCount * 0.02f);
		pg.box(200 + 170f * P.sin(p.frameCount * 0.04f), 200 + 50f * P.sin(p.frameCount * 0.01f), 200 + 50f * P.sin(p.frameCount * 0.02f));
		pg.endDraw();
		
		// copy image & create glow version
		ImageUtil.copyImage(pg, glowBuffer);
		LeaveWhiteFilter.instance().setCrossfade(0.3f);
		LeaveWhiteFilter.instance().applyTo(glowBuffer);
		BlurHFilter.instance().setBlurByPercent(Mouse.xNorm * 5f, glowBuffer.width);
		BlurVFilter.instance().setBlurByPercent(Mouse.yNorm * 5f, glowBuffer.height);
		for (int i = 0; i < 10; i++) {
			BlurHFilter.instance().applyTo(glowBuffer);
			BlurVFilter.instance().applyTo(glowBuffer);
		}

		// debug display
		p.background(0);
		p.image(pg, 0, 0, 320, 240);
		p.image(glowBuffer, 320, 0, 320, 240);
		p.image(pg, 0, 240, 320, 240);
		p.blendMode(PBlendModes.MULTIPLY);
		p.blendMode(PBlendModes.DARKEST);
		p.blendMode(PBlendModes.SCREEN);
		p.image(glowBuffer, 0, 240, 320, 240);
		p.blendMode(PBlendModes.BLEND);
		
		// draw back to source buffer
		pg.beginDraw();
		pg.blendMode(PBlendModes.SCREEN);
		pg.image(glowBuffer, 0, 0);
		pg.blendMode(PBlendModes.BLEND);
		pg.endDraw();
		
		p.image(pg, 320, 240, 320, 240);

		
		// post 
		GrainFilter.instance().setTime(p.frameCount * 0.01f);
		GrainFilter.instance().setCrossfade(0.03f);
		GrainFilter.instance().applyTo(p);
	}
}
