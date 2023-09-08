package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;

public class Demo_DepthCameraRegion_Stop_Test
extends Demo_DepthCameraRegion {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void firstFrame() {
		super.firstFrame();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
    		DepthCamera.instance().camera.stop();
    		P.out("Camera stopped");
		}
	}
	
	protected void drawApp() { 
		super.drawApp();
	}
		
}
