package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.OpticalFlowCPU;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PImage;

public class Demo_OpticalFlowCPU 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected OpticalFlowCPU opticalFlow;

	protected void firstFrame() {
		WebCam.instance().setDelegate(this);
	}
	
	protected void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();

		// draw mouse point to offscreen buffer
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		pg.fill(255);
		PG.setDrawCenter(pg);
		pg.ellipse(p.mouseX, p.mouseY, 40, 40);
		ImageUtil.cropFillCopyImage(WebCam.instance().image(), pg, true);
		pg.endDraw();

		// lazy-init color detection and update it with `pg`
		if(opticalFlow == null) {
			float detectionScaleDown = 0.15f;
			opticalFlow = new OpticalFlowCPU(pg, detectionScaleDown);
		}
		opticalFlow.smoothing(0.02f);
		opticalFlow.update(pg);

		// draw input view to screen
		p.image(pg, 0, 0);
		
		// draw debug flow results
		opticalFlow.debugDraw(p.g, false);
		opticalFlow.drawDataBuffer();
		
		// check vector getter for a specific position
		float[] vecResult = opticalFlow.getVectorAt(0.5f, 0.5f);
		DebugView.setValue("OpticalFlow.getVectorAt(0.5f, 0.5f) x", vecResult[0]);
		DebugView.setValue("OpticalFlow.getVectorAt(0.5f, 0.5f) y", vecResult[1]);
		vecResult = opticalFlow.getVectorAt(1f, 1f);
		DebugView.setValue("OpticalFlow.getVectorAt(1f, 1f) x", vecResult[0]);
		DebugView.setValue("OpticalFlow.getVectorAt(1f, 1f) y", vecResult[1]);
		vecResult = opticalFlow.getVectorAt(0f, 0f);
		DebugView.setValue("OpticalFlow.getVectorAt(0f, 0f) x", vecResult[0]);
		DebugView.setValue("OpticalFlow.getVectorAt(0f, 0f) y", vecResult[1]);
	}

	@Override
	public void newFrame(PImage frame) {
		// TODO Auto-generated method stub
		
	}

}
