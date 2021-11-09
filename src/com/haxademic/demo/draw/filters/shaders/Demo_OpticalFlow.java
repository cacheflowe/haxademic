package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.OpticalFlow;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_OpticalFlow
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// optical flow effect & visual
	protected OpticalFlow opticalFlow;
	protected PGraphics curRgbFrame;
	protected PGraphics camDisplaced;

	// camera
	protected RealSenseWrapper realSenseWrapper;
	
	// ui
	protected String cameraLerp = "cameraLerp";
	protected String cameraDisplaceAmp = "cameraDisplaceAmp";
	protected String cameraDisplaceIters = "cameraDisplaceIters";

	
	protected void config() {
		Config.setProperty( AppSettings.RESIZABLE, true );
	}

	protected void firstFrame() {
		// init camera
		realSenseWrapper = new RealSenseWrapper(p, true, true);
		
		// create buffers
		curRgbFrame = PG.newPG(p.width, p.height);
		camDisplaced = PG.newPG32(p.width, p.height, true, false);

		// build optical flow object
		opticalFlow = new OpticalFlow(p.width, p.height);
		opticalFlow.buildUI();
		
		// add textures to debug panel
		DebugView.setTexture("camDisplaced", camDisplaced);
		DebugView.setTexture("opFlowResult", opticalFlow.resultBuffer());
		DebugView.setTexture("opFlowResultFlowed", opticalFlow.resultFlowedBuffer());
		DebugView.setTexture("getDepthImage()", realSenseWrapper.getDepthImage());

		// ui
		UI.addTitle("Final comp: Use the flow");
		UI.addSlider(cameraLerp, 0.1f, 0f, 1f, 0.01f, false);
		UI.addSlider(cameraDisplaceAmp, 0.2f, 0f, 1f, 0.01f, false);
		UI.addSlider(cameraDisplaceIters, 1, 0f, 10f, 1f, false);
	}

	protected void drawApp() {
		p.background(0);
		
		// update camera
		realSenseWrapper.update();
		
		// copy camera frames to buffers
		opticalFlow.updateOpticalFlowProps();
		opticalFlow.update(realSenseWrapper.getDepthImage(), true);
//		opticalFlow.drawDebugLines((FrameLoop.frameMod(200) < 100) ? opticalFlow.resultBuffer() : opticalFlow.resultFlowedBuffer());
		opticalFlow.drawDebugLines(opticalFlow.resultFlowedBuffer());
		applyFlowToRgbCamera();
		drawToScreen();
	}
	
	
	protected void applyFlowToRgbCamera() {
		// copy camera rgb to buffer and mix it slightly into the displaced buffer
		ImageUtil.cropFillCopyImage(realSenseWrapper.getRgbImage(), curRgbFrame, true);
		BlendTowardsTexture.instance(p).setSourceTexture(curRgbFrame);
		BlendTowardsTexture.instance(p).setBlendLerp(UI.value(cameraLerp));
		BlendTowardsTexture.instance(p).applyTo(camDisplaced);
		
		// flow the displaced camera buffer
		PGraphics opFlowResult = opticalFlow.resultFlowedBuffer();
		DisplacementMapFilter.instance(P.p).setMap(opFlowResult);
		DisplacementMapFilter.instance(P.p).setMode(10);
		DisplacementMapFilter.instance(P.p).setAmp(UI.value(cameraDisplaceAmp));
		for (int i = 0; i < UI.valueInt(cameraDisplaceIters); i++) {
			DisplacementMapFilter.instance.applyTo(camDisplaced);	
		}
	}
	
	protected void drawToScreen() {
		// draw flowed camera image
		ImageUtil.cropFillCopyImage(camDisplaced, p.g, true);
		
		// draw debug lines
		PG.setPImageAlpha(p.g, 0.9f);
		ImageUtil.drawImageCropFill(opticalFlow.debugBuffer(), p.g, true);	
		PG.resetPImageAlpha(p.g);
	}
	
}
