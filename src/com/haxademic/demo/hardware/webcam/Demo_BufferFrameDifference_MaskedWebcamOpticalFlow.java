package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.OpticalFlow;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BufferFrameDifference_MaskedWebcamOpticalFlow 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// optical flow effect & visual
	protected OpticalFlow opticalFlow;
	protected PGraphics curRgbFrame;
	protected PGraphics camDisplaced;

	protected BufferFrameDifference bufferFrameDifference;
	
	protected PGraphics diffBufferSmoothed;
	protected PGraphics knockoutWebCam;

	// frame diff
	protected String diffFalloffBW = "diffFalloffBW";
	protected String diffThresh = "diffThresh";
	protected String diffSmoothThresh = "diffSmoothThresh";
	
	// optical flow
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
		Config.setAppSize(1280, 720);
//		Config.setProperty(AppSettings.PG_WIDTH, 3438 );
//		Config.setProperty(AppSettings.PG_HEIGHT, 1080 );
		Config.setProperty(AppSettings.FULLSCREEN, false);
//		Config.setProperty(AppSettings.SCREEN_X, 0);
//		Config.setProperty(AppSettings.SCREEN_Y, 0);
	}
		
	protected void firstFrame () {
		// init webcam
//		WebCam.instance().setDelegate(this).set720p(1);
		WebCam.instance().setDelegate(this).selectCamGstreamer(640, 480, 60, 2);
		
		// ui
		UI.addSlider(diffFalloffBW, 0.4f, 0, 1, 0.01f, false);
		UI.addSlider(diffThresh, 0.1f, 0, 1, 0.001f, false);
		UI.addSlider(diffSmoothThresh, 0.66f, 0, 1, 0.001f, false);
		
		
		// build optical flow object
		opticalFlow = new OpticalFlow(1024, 1024);
		
		// add textures to debug panel
		DebugView.setTexture("camDisplaced", camDisplaced);
		DebugView.setTexture("opFlowResult", opticalFlow.resultBuffer());

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
		UI.addSlider(resultFlowDisplaceAmp, 0.6f, 0f, 1f, 0.001f, false);
		UI.addSlider(resultFlowDisplaceIters, 1f, 0f, 10f, 1f, false);
		UI.addSlider(resultBlurAmp, 20f, 0f, 100f, 0.1f, false);
		UI.addSlider(resultBlurSigma, 20f, 0f, 100f, 0.1f, false);

		UI.addTitle("Final comp: Use the flow");
		UI.addSlider(cameraLerp, 0.1f, 0f, 1f, 0.01f, false);
		UI.addSlider(cameraDisplaceAmp, 0.6f, 0f, 1f, 0.01f, false);
		UI.addSlider(cameraDisplaceIters, 1, 0f, 10f, 1f, false);

	}

	protected void drawApp() {
		// set up context
		if(p.frameCount < 10) p.background(0);	// clear screen up front, but then stop
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		
		
		// set difference mask on webcam image
		if(knockoutWebCam != null) {
			knockoutWebCam.mask(diffBufferSmoothed);
			pg.beginDraw();
			ImageUtil.drawImageCropFill(knockoutWebCam, pg, true);
//			pg.image(knockoutWebCam, 0, 0);
			pg.endDraw();
			
			applyFlow(pg);
		}
		
		p.image(pg, 0, 0);
		ImageUtil.cropFillCopyImage(pg, p.g, false);
	}
	
	protected void doOpticalFlow(PImage frame) {
		// copy camera frames to buffers
		updateOpticalFlowProps();
		opticalFlow.update(frame, true);
		opticalFlow.drawDebugLines(opticalFlow.resultBuffer());
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

	protected void applyFlow(PGraphics pg) {
		// copy camera rgb to buffer and mix it slightly into the displaced buffer
//		ImageUtil.cropFillCopyImage(realSenseWrapper.getRgbImage(), curRgbFrame, true);
//		BlendTowardsTexture.instance(p).setSourceTexture(curRgbFrame);
//		BlendTowardsTexture.instance(p).setBlendLerp(UI.value(cameraLerp));
//		BlendTowardsTexture.instance(p).applyTo(camDisplaced);
		
		// flow the displaced camera buffer
		PGraphics opFlowResult = opticalFlow.resultBuffer();
		DisplacementMapFilter.instance(P.p).setMap(opFlowResult);
		DisplacementMapFilter.instance(P.p).setMode(10);
		DisplacementMapFilter.instance(P.p).setAmp(UI.value(cameraDisplaceAmp));
		for (int i = 0; i < UI.valueInt(cameraDisplaceIters); i++) {
			DisplacementMapFilter.instance.applyTo(pg);	
		}
		
		
		BrightnessStepFilter.instance(p).setBrightnessStep(-1/255f);
		BrightnessStepFilter.instance(p).applyTo(pg);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// lazy init graphics based on webcam size
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
			diffBufferSmoothed = PG.newPG(frame.width, frame.height);
			knockoutWebCam = PG.newPG(frame.width, frame.height);
		}
		
		// copy webcam to buffer
		ImageUtil.copyImageFlipH(frame, knockoutWebCam);
		
		// update difference buffer on last webcam frame
		bufferFrameDifference.falloffBW(UI.value(diffFalloffBW));
		bufferFrameDifference.diffThresh(UI.value(diffThresh));
		bufferFrameDifference.update(frame);
		
		// copy to diff buffer smoothed version
		ImageUtil.copyImageFlipH(bufferFrameDifference.differenceBuffer(), diffBufferSmoothed);
		ThresholdFilter.instance(p).setCutoff(UI.value(diffSmoothThresh));
		ThresholdFilter.instance(p).applyTo(diffBufferSmoothed);
		BlurProcessingFilter.instance(p).setBlurSize(10);
		for(int i=0; i < 5; i++) BlurProcessingFilter.instance(p).applyTo(diffBufferSmoothed);
		
		// debug webcam view
		DebugView.setTexture("webcam", frame);
		DebugView.setTexture("diffBufferSmoothed", diffBufferSmoothed);
		
		doOpticalFlow(knockoutWebCam);
	}

}
