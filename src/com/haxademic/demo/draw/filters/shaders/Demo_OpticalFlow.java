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
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.video.MovieBuffer;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_OpticalFlow
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// optical flow effect & visual
	protected OpticalFlow opticalFlow;
	protected PGraphics curRgbFrame;	// just in case we want a separate color layer to blend in
	protected PGraphics curSourceFrame;
	protected PGraphics camDisplaced;

	// sources
	protected RealSenseWrapper realSenseWrapper;
	protected MovieBuffer video;
	
	// ui
	protected String sourceLerp = "sourceLerp";
	protected String sourceDisplaceAmp = "sourceDisplaceAmp";
	protected String sourceDisplaceIters = "sourceDisplaceIters";
	protected String showDebug = "showDebug";

	// realsense-specific
	protected String faceMeltConfig = "{ \"uDecayLerp\": 0.0069999974, \"uForce\": 4.3399997, \"uOffset\": 8.0, \"uLambda\": 0.008800002, \"uThreshold\": 0.056000013, \"uInverseX\": -1.0, \"uInverseY\": -1.0, \"preBlurAmp\": 14.500001, \"preBlurSigma\": 6.9, \"resultFlowDecayLerp\": 0.13499989, \"resultFlowDisplaceAmp\": 0.412, \"resultFlowDisplaceIters\": 1.0, \"resultBlurAmp\": 47.800003, \"resultBlurSigma\": 49.9, \"sourceLerp\": 1.0, \"sourceDisplaceAmp\": 0.01, \"sourceDisplaceIters\": 8.0, \"showDebug\": 0.0 }";
	protected String flowwwwwConfig = "{ \"uDecayLerp\": 0.02, \"uForce\": 0.75, \"uOffset\": 8.0, \"uLambda\": 0.012, \"uThreshold\": 0.1, \"uInverseX\": -1.0, \"uInverseY\": -1.0, \"preBlurAmp\": 20.0, \"preBlurSigma\": 20.0, \"resultFlowDecayLerp\": 0.9, \"resultFlowDisplaceAmp\": 0.213, \"resultFlowDisplaceIters\": 6.0, \"resultBlurAmp\": 20.0, \"resultBlurSigma\": 20.0, \"sourceLerp\": 0.05, \"sourceDisplaceAmp\": 0.17, \"sourceDisplaceIters\": 1.0, \"showDebug\": 0.0 }";
	
	protected void config() {
		Config.setAppSize( 1280, 720 );
		Config.setProperty( AppSettings.RESIZABLE, true );
	}

	protected void firstFrame() {
		buildVideoSource();
		buildBuffers();
		buildOpticalFlow();
		setDebugTextures();
		buildUI();
	}
	
	protected void buildVideoSource() {
		realSenseWrapper = new RealSenseWrapper(p, true, true);
//		video = new MovieBuffer(DemoAssets.movieKinectSilhouette());	// new MovieBuffer(FileUtil.getPath(DemoAssets.movieFractalCubePath));
//		video.movie.loop();
	}
	
	protected void buildBuffers() {
		curSourceFrame = PG.newPG32(p.width, p.height, true, false);
		curRgbFrame = PG.newPG(p.width, p.height);
		camDisplaced = PG.newPG32(p.width, p.height, true, false);
	}
	
	protected void buildOpticalFlow() {
		opticalFlow = new OpticalFlow(p.width, p.height);
		opticalFlow.buildUI();
	}

	protected void buildUI() {
		UI.addTitle("Final comp: Use the flow");
		UI.addSlider(sourceLerp, 0.1f, 0f, 1f, 0.01f, false);
		UI.addSlider(sourceDisplaceAmp, 0.2f, 0f, 1f, 0.01f, false);
		UI.addSlider(sourceDisplaceIters, 1, 0f, 10f, 1f, false);
		UI.addToggle(showDebug, true, false);
	}
	
	protected void setDebugTextures() {
		DebugView.setTexture("camDisplaced", camDisplaced);
		DebugView.setTexture("opFlowResult", opticalFlow.resultBuffer());
		DebugView.setTexture("opFlowResultFlowed", opticalFlow.resultFlowedBuffer());
		DebugView.setTexture("source video", curSourceFrame);
		DebugView.setTexture("rgb video", curRgbFrame);
	}
	
	protected void updateSourceVideo() {
		// update whichever source isn't null
		if(realSenseWrapper != null) {
			realSenseWrapper.update();
			ImageUtil.cropFillCopyImage(realSenseWrapper.getRgbImage(), curRgbFrame, true);
			ImageUtil.cropFillCopyImage(realSenseWrapper.getDepthImage(), curSourceFrame, true);
		} else if(video != null) {
//			if(video.movie.isPlaying() == false) video.movie.play();
			if(video.buffer != null) { 
				ImageUtil.cropFillCopyImage(video.buffer, curRgbFrame, true);
				ImageUtil.cropFillCopyImage(video.buffer, curSourceFrame, true);
			}
		}
	}

	protected void drawApp() {
		p.background(0);
		updateSourceVideo();
		updateOpticalFlow();
		applyFlowToRgbsource();
		drawToScreen();
		updateUIPresets();
	}
	
	protected void updateOpticalFlow() {
		opticalFlow.updateOpticalFlowProps();
		opticalFlow.update(curSourceFrame, true);
		opticalFlow.drawDebugLines(opticalFlow.resultFlowedBuffer());
//		opticalFlow.drawDebugLines((FrameLoop.frameMod(200) < 100) ? opticalFlow.resultBuffer() : opticalFlow.resultFlowedBuffer());
	}	
	
	protected void applyFlowToRgbsource() {
		// copy source rgb to buffer and mix it slightly into the displaced buffer
		BlendTowardsTexture.instance(p).setSourceTexture(curRgbFrame);
		BlendTowardsTexture.instance(p).setBlendLerp(UI.value(sourceLerp));
		BlendTowardsTexture.instance(p).applyTo(camDisplaced);
		
		// flow the displaced source buffer
		PGraphics opFlowResult = opticalFlow.resultFlowedBuffer();
		DisplacementMapFilter.instance(P.p).setMap(opFlowResult);
		DisplacementMapFilter.instance(P.p).setMode(10);
		DisplacementMapFilter.instance(P.p).setAmp(UI.value(sourceDisplaceAmp));
		for (int i = 0; i < UI.valueInt(sourceDisplaceIters); i++) {
			DisplacementMapFilter.instance.applyTo(camDisplaced);	
		}
	}
	
	protected void updateUIPresets() {
		if(KeyboardState.keyTriggered('1')) UI.loadValuesFromJSON(faceMeltConfig);
		if(KeyboardState.keyTriggered('2')) UI.loadValuesFromJSON(flowwwwwConfig);
		if(KeyboardState.keyTriggered(' ')) P.out(JsonUtil.jsonToSingleLine(UI.configToJSON()));
	}
	
	protected void drawToScreen() {
		// draw flowed source image
		ImageUtil.cropFillCopyImage(camDisplaced, p.g, true);
		
		// draw optical flow debug lines
		if(UI.valueToggle(showDebug)) {
			PG.setPImageAlpha(p.g, 0.9f);
			ImageUtil.drawImageCropFill(opticalFlow.debugBuffer(), p.g, true);	
			PG.resetPImageAlpha(p.g);
		}
	}
	
}
