package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.image.OpticalFlow;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_RealSenseWrapper_MultipleOpticalFlow
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int camW;
	protected int camH;
	protected RealSenseWrapper camera1;
	protected PGraphics cam1LerpedFrame; 
	protected OpticalFlow cam1OpticalFlow;
	protected FloatBuffer cam1DirX = new FloatBuffer(60);
	protected FloatBuffer cam1DirY = new FloatBuffer(60);
	protected RealSenseWrapper camera2;
	protected PGraphics cam2LerpedFrame; 
	protected OpticalFlow cam2OpticalFlow;
	protected FloatBuffer cam2DirX = new FloatBuffer(60);
	protected FloatBuffer cam2DirY = new FloatBuffer(60);

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
	}

	protected void firstFrame() {
		RealSenseWrapper.METERS_FAR_THRESH = 5;
		camera1 = new RealSenseWrapper(p, true, true, "953122060282");
//		camera2 = new RealSenseWrapper(p, true, true, "851112060694");
		camera2 = new RealSenseWrapper(p, true, true, "821312062651");
		
		// set size of depth/motion processing buffers
		camW = RealSenseWrapper.CAMERA_W / 10;
		camH = RealSenseWrapper.CAMERA_H / 10;
		
		// build buffers
		cam1LerpedFrame = PG.newPG(camW, camH);
		cam1OpticalFlow = new OpticalFlow(cam1LerpedFrame, 1);
		DebugView.setTexture("cam1LerpedFrame", cam1LerpedFrame);

		cam2LerpedFrame = PG.newPG(camW, camH);
		cam2OpticalFlow = new OpticalFlow(cam2LerpedFrame, 1);
		DebugView.setTexture("cam2LerpedFrame", cam2LerpedFrame);
	}

	protected void drawApp() {
		// clear screen
		p.background(0);
		p.noStroke();
		
		// update cameras
		updateCamera(camera1, cam1LerpedFrame, cam1OpticalFlow, cam1DirX, cam1DirY, p.width * 0.33f, p.height/2);
		updateCamera(camera2, cam2LerpedFrame, cam2OpticalFlow, cam2DirX, cam2DirY, p.width * 0.66f, p.height/2);
		
		// draw debug vector
		p.push();
		p.stroke(255);
		p.strokeWeight(4);
		p.translate(p.width * 0.33f, p.height/2);
		p.line(0, 0, cam1DirX.average() * 20f, cam1DirY.average() * 20f);
		p.pop();

		p.push();
		p.stroke(255);
		p.strokeWeight(4);
		p.translate(p.width * 0.66f, p.height/2);
		p.line(0, 0, cam2DirX.average() * 20f, cam2DirY.average() * 20f);
		p.pop();

		// print out uptime
		if(FrameLoop.frameModHours(1)) P.out("Still running:", DebugView.uptimeStr());
	}

	protected void updateCamera(RealSenseWrapper camera, PGraphics lerpedBuffer, OpticalFlow opticalFlow, FloatBuffer dirX, FloatBuffer dirY, float cameraX, float cameraY) {
		// update realsense
		camera.update();

		// copy depth image
		BlendTowardsTexture.instance(p).setSourceTexture(camera.getDepthImage());
		BlendTowardsTexture.instance(p).setBlendLerp(0.5f);
		BlendTowardsTexture.instance(p).applyTo(lerpedBuffer);
		
		// run optical flow
		opticalFlow.update(lerpedBuffer);
		float cameraDirX = 0;
		float cameraDirY = 0;
		float opFlowThresh = 0;
		int flowValues = 0;
		for (int x = 0; x < camW; x++) {
			for (int y = 0; y < camH; y++) {
				float xNorm = (float) x / (float) camW;
				float yNorm = (float) y / (float) camH;
				float[] vecResult = opticalFlow.getVectorAt(xNorm, yNorm);
				if(P.abs(vecResult[0]) > opFlowThresh) cameraDirX += vecResult[0];
				if(P.abs(vecResult[1]) > opFlowThresh) cameraDirY += vecResult[1];
				flowValues++;
			}
		}
		
		// update averaged direction
		dirX.update(cameraDirX / flowValues);
		dirY.update(cameraDirY / flowValues);
		DebugView.setValue("camera1DirX", dirX.average());
		DebugView.setValue("camera1DirY", dirY.average());
		
		// draw debug
		p.pushMatrix();
		p.translate(cameraX, cameraY);
		opticalFlow.debugDraw(p.g);
		p.popMatrix();
	}
	
}
