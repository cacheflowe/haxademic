package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.Fluid;
import com.haxademic.core.draw.image.OpticalFlow;

import processing.core.PGraphics;

public class Demo_OpticalFlowFluid 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int w = 640; 
	protected int h = 320; 
	protected OpticalFlow opticalFlow;

	protected Fluid fluid;
	protected PGraphics fluidBuff;
	protected int scale = 10;

	public void setupFirstFrame() {
		// set buffer size
		pg = p.createGraphics(w, h, PRenderers.P3D);
		
		// build optical flow
		float detectionScaleDown = 0.15f;
		opticalFlow = new OpticalFlow(pg, detectionScaleDown);
		
		// build fluid
		fluid = new Fluid(w/scale, h/scale, 0f, 0.000001f, 0.01f);
		fluid.scale(scale);
		fluidBuff = p.createGraphics(w, h, PRenderers.P3D);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		
		// draw mouse point to offscreen buffer
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		pg.fill(255);
		DrawUtil.setDrawCenter(pg);
		pg.ellipse(p.mouseX, p.mouseY, 40, 40);
		pg.endDraw();

		// update optical flow 
		opticalFlow.smoothing(0.02f);
		opticalFlow.update(pg);

		// draw input view to screen
		p.image(pg, 0, 0);
		// draw debug flow results
		opticalFlow.debugDraw(p.g, false);
		
		// check vector getter for a specific position
		updateFluid();
	}
	
	protected void updateFluid() {
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				float xNorm = (float) x / (float) w;
				float yNorm = (float) y / (float) h;
				float[] vecResult = opticalFlow.getVectorAt(xNorm, yNorm);
				fluid.addVelocity(x/fluid.scale(), y/fluid.scale(), vecResult[0] * 0.00015f, vecResult[1] * 0.00015f);
				fluid.addDensity(x/fluid.scale(), y/fluid.scale(), P.abs(vecResult[0]) * 1.0f + P.abs(vecResult[1]) * 1.0f);
			}
		}

		// calc & draw fluid
		fluid.step();
		fluid.fadeAmp(100f);
		fluidBuff.beginDraw();
		fluidBuff.background(255,0,0);
		//fluid.renderV(fluidBuff);
		fluid.renderD(fluidBuff);
		fluidBuff.endDraw();
		fluid.fadeD();
		
		// postprocessing
		BlurHFilter.instance(p).setBlurByPercent(2f, fluidBuff.width);
		BlurVFilter.instance(p).setBlurByPercent(2f, fluidBuff.height);
		BlurHFilter.instance(p).applyTo(fluidBuff);
		BlurVFilter.instance(p).applyTo(fluidBuff);
		BlurHFilter.instance(p).applyTo(fluidBuff);
		BlurVFilter.instance(p).applyTo(fluidBuff);
		
		// draw fluid buffer to screen
		p.image(fluidBuff, 0, 0);
	}

}
