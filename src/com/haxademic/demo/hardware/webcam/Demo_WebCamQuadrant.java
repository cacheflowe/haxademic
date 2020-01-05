package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;

import processing.core.PImage;

public class Demo_WebCamQuadrant 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void drawApp() {
		p.background( 0 );
		PG.setDrawCorner(p);
		PG.resetPImageAlpha(p);

		WebCam.instance().setDelegate(this);
		PImage camFrame = WebCam.instance().image();
		
		if(Mouse.xNorm < 0.333f) {
			
			PG.setDrawCenter(p);
			PG.setCenterScreen(p);
			p.image(camFrame, 0, 0);
		} else if(Mouse.xNorm < 0.666f) {
		
			// draw sequence
			int frameAdjusted = P.floor(p.frameCount / (Mouse.yNorm * 10f));
			int cameraIndex = frameAdjusted % 6;
			if(cameraIndex == 0) {
				p.copy(camFrame, 0, 0, 960, 540, 0, 0, p.width, p.height);
			} else if(cameraIndex == 1) {
				p.copy(camFrame, 0, 540, 960, 540, 0, 0, p.width, p.height);
			} else if(cameraIndex == 2) {
				p.copy(camFrame, 960, 0, 960, 540, 0, 0, p.width, p.height);
			} else if(cameraIndex == 3) {
				p.copy(camFrame, 960, 540, 960, 540, 0, 0, p.width, p.height);
			} else if(cameraIndex == 4) {
				p.copy(camFrame, 960, 0, 960, 540, 0, 0, p.width, p.height);
			} else if(cameraIndex == 5) {
				p.copy(camFrame, 0, 540, 960, 540, 0, 0, p.width, p.height);
			}
			
		} else {
			PG.setPImageAlpha(p, 0.25f);
			float imgScale = MathUtil.scaleToTarget(960, p.width);
			p.image(camFrame, 0, 0, camFrame.width * imgScale, camFrame.height * imgScale);
			p.image(camFrame, -p.width, 0, camFrame.width * imgScale, camFrame.height * imgScale);
			p.image(camFrame, 0, -p.height, camFrame.width * imgScale, camFrame.height * imgScale);
			p.image(camFrame, -p.width, -p.height, camFrame.width * imgScale, camFrame.height * imgScale);
			
		}
	}

	@Override
	public void newFrame(PImage frame) {
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}

}
