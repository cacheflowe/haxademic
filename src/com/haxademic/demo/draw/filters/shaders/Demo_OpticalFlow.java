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
	
	// UI
	protected String uDecayLerp = "uDecayLerp";
	protected String uForce = "uForce";
	protected String uOffset = "uOffset";
	protected String uLambda = "uLambda";
	protected String uThreshold = "uThreshold";
	protected String uInverseX = "uInverseX";
	protected String uInverseY = "uInverseY";
	
	protected String preBlurAmp = "preBlurAmp";
	protected String preBlurSigma = "preBlurSigma";
	
	protected String resultFlowDisplaceAmp = "resultFlowDisplaceAmp";
	protected String resultFlowDisplaceIters = "resultFlowDisplaceIters";
	protected String resultBlurAmp = "resultBlurAmp";
	protected String resultBlurSigma = "resultBlurSigma";

	protected String cameraLerp = "cameraLerp";
	protected String cameraDisplaceAmp = "cameraDisplaceAmp";
	protected String cameraDisplaceIters = "cameraDisplaceIters";

	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 480 );
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
		
		// add textures to debug panel
		DebugView.setTexture("camDisplaced", camDisplaced);
		DebugView.setTexture("opFlowResult", opticalFlow.resultBuffer());
		DebugView.setTexture("getDepthImage()", realSenseWrapper.getDepthImage());
		
		// build UI
		UI.addTitle("OF: Calculation");
		UI.addSlider(uDecayLerp, 0.02f, 0f, 1f, 0.001f, false);
		UI.addSlider(uForce, 0.75f, 0f, 10f, 0.01f, false);
		UI.addSlider(uOffset, 8f, 0f, 100f, 0.01f, false);
		UI.addSlider(uLambda, 0.012f, 0f, 1f, 0.0001f, false);
		UI.addSlider(uThreshold, 0.1f, 0f, 1f, 0.001f, false);
		UI.addSlider(uInverseX, -1f, -1f, 1f, 1f, false);
		UI.addSlider(uInverseY, -1f, -1f, 1f, 1f, false);
		
		UI.addTitle("OF: Pre blur");
		UI.addSlider(preBlurAmp, 20f, 0f, 100f, 0.1f, false);
		UI.addSlider(preBlurSigma, 20f, 0f, 100f, 0.1f, false);
		
		UI.addTitle("OF: Flow result displace");
		UI.addSlider(resultFlowDisplaceAmp, 0.2f, 0f, 1f, 0.001f, false);
		UI.addSlider(resultFlowDisplaceIters, 1f, 0f, 10f, 1f, false);
		UI.addSlider(resultBlurAmp, 20f, 0f, 100f, 0.1f, false);
		UI.addSlider(resultBlurSigma, 20f, 0f, 100f, 0.1f, false);

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
		updateOpticalFlowProps();
		opticalFlow.update(realSenseWrapper.getDepthImage(), true);
		opticalFlow.drawDebugLines();
		applyFlowToRgbCamera();
		drawToScreen();
	}
	
	protected void updateOpticalFlowProps() {
		opticalFlow.uDecayLerp(UI.value(uDecayLerp));
		opticalFlow.uForce(UI.value(uForce));
		opticalFlow.uOffset(UI.value(uOffset));
		opticalFlow.uLambda(UI.value(uLambda));
		opticalFlow.uThreshold(UI.value(uThreshold));
		opticalFlow.uInverseX(UI.value(uInverseX));
		opticalFlow.uInverseY(UI.value(uInverseY));
				
		opticalFlow.preBlurAmp(UI.valueInt(preBlurAmp));
		opticalFlow.preBlurSigma(UI.value(preBlurSigma));
				
		opticalFlow.resultFlowDisplaceAmp(UI.value(resultFlowDisplaceAmp));
		opticalFlow.resultFlowDisplaceIters(UI.valueInt(resultFlowDisplaceIters));
		opticalFlow.resultBlurAmp(UI.valueInt(resultBlurAmp));
		opticalFlow.resultBlurSigma(UI.value(resultBlurSigma));
	}
	
	protected void applyFlowToRgbCamera() {
		// copy camera rgb to buffer and mix it slightly into the displaced buffer
		ImageUtil.cropFillCopyImage(realSenseWrapper.getRgbImage(), curRgbFrame, true);
		BlendTowardsTexture.instance(p).setSourceTexture(curRgbFrame);
		BlendTowardsTexture.instance(p).setBlendLerp(UI.value(cameraLerp));
		BlendTowardsTexture.instance(p).applyTo(camDisplaced);
		
		// flow the displaced camera buffer
		PGraphics opFlowResult = opticalFlow.resultBuffer();
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
		ImageUtil.drawImageCropFill(opticalFlow.debugBuffer(), p.g, true);	
	}
	
}
