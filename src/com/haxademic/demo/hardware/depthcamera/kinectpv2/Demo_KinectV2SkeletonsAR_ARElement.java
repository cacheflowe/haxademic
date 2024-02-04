package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.hardware.depthcamera.ar.ArElementPool;
import com.haxademic.core.hardware.depthcamera.ar.IArElement;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

public class Demo_KinectV2SkeletonsAR_ARElement
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectV2SkeletonsAR kinectSkeletonsAR;
	protected ArElementPool arPool;
	
	protected void config() {
		// Config.setProperty( AppSettings.WIDTH, 1024 );
	}
	
	protected void firstFrame() {
		arPool = Demo_KinectV2SkeletonsAR.buildArPool();
		kinectSkeletonsAR = new KinectV2SkeletonsAR(pg, arPool, true);
	}
	
	protected void drawApp() {
		// set up context
		p.background(0);
		PG.setCenterScreen(p);
		
		// draw objects
		for (int i = 0; i < arPool.elements().size(); i++) {
			IArElement arEl = arPool.elements().get(i);
			arEl.updatePre(p.g);
			if(i == P.round(Mouse.xNorm * (arPool.elements().size() - 1))) {
				arEl.setRotation(0, FrameLoop.osc(0.05f, -.1f, .1f), FrameLoop.osc(0.05f, -.1f, .1f));
				// arEl.setScale(curScale);
				arEl.drawOrigin(p.g);
				arEl.draw(p.g);
			}
		}
	}

}
