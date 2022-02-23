package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class OpticalFlow {

	// optical flow buffers & shader
	protected PGraphics tex1;
	protected PGraphics tex0;
	protected PGraphics resultBuffer;
	protected PGraphics resultFlowedBuffer;
	protected PGraphics debugBuffer;
	protected PShaderHotSwap opFlowShader;
	protected int frameCount = -1;
	
	// UI
	public static final String UI_uDecayLerp = "uDecayLerp";
	public static final String UI_uForce = "uForce";
	public static final String UI_uOffset = "uOffset";
	public static final String UI_uLambda = "uLambda";
	public static final String UI_uThreshold = "uThreshold";
	public static final String UI_uInverseX = "uInverseX";
	public static final String UI_uInverseY = "uInverseY";
	
	public static final String UI_preBlurAmp = "preBlurAmp";
	public static final String UI_preBlurSigma = "preBlurSigma";
	
	public static final String UI_resultFlowDecayLerp = "resultFlowDecayLerp";
	public static final String UI_resultFlowDisplaceAmp = "resultFlowDisplaceAmp";
	public static final String UI_resultFlowDisplaceIters = "resultFlowDisplaceIters";
	public static final String UI_resultBlurAmp = "resultBlurAmp";
	public static final String UI_resultBlurSigma = "resultBlurSigma";

	// uniforms
	protected float uDecayLerp = 0.02f;
	protected float uForce = 0.75f;
	protected float uOffset = 8f;
	protected float uLambda = 0.012f;
	protected float uThreshold = 0.1f;
	protected float uInverseX = -1f;
	protected float uInverseY = -1f;

	// incoming frame pre-processing
	protected int preBlurAmp = 20;
	protected float preBlurSigma = 20f;

	// flow the flow
	protected float resultFlowDecayLerp = 0.15f;
	protected float resultFlowDisplaceAmp = 0.2f;
	protected int resultFlowDisplaceIters = 1;
	protected int resultBlurAmp = 20;
	protected float resultBlurSigma = 10f;
	

	public OpticalFlow(int w, int h) {
		// build buffers
		tex0 = PG.newPG(w, h);
		tex1 = PG.newPG(w, h);
		resultBuffer = PG.newPG32(w, h, false, false);		// disabling smoothing allows for per-pixel lerping w/very small values
		resultFlowedBuffer = PG.newPG32(w, h, true, false);	// can't be 32-bit because displacement doesn't work properly - only goes diagonal
		PG.setTextureRepeat(resultBuffer, false);
		PG.setTextureRepeat(resultFlowedBuffer, false);
		
		// load shader
		opFlowShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/optical-flow.glsl"));
	}
	
	// ui
	
	public void buildUI() {
		UI.addTitle("OF: Calculation");
		UI.addSlider(UI_uDecayLerp, 0.02f, 0f, 1f, 0.001f, false);
		UI.addSlider(UI_uForce, 0.75f, 0f, 10f, 0.01f, false);
		UI.addSlider(UI_uOffset, 8f, 0f, 100f, 0.01f, false);
		UI.addSlider(UI_uLambda, 0.012f, 0f, 1f, 0.0001f, false);
		UI.addSlider(UI_uThreshold, 0.1f, 0f, 1f, 0.001f, false);
		UI.addSlider(UI_uInverseX, -1f, -1f, 1f, 1f, false);
		UI.addSlider(UI_uInverseY, -1f, -1f, 1f, 1f, false);
		
		UI.addTitle("OF: Pre blur");
		UI.addSlider(UI_preBlurAmp, 20f, 0f, 100f, 0.1f, false);
		UI.addSlider(UI_preBlurSigma, 20f, 0f, 100f, 0.1f, false);
		
		UI.addTitle("OF: Flow result displace");
		UI.addSlider(UI_resultFlowDecayLerp, 0.15f, 0f, 1f, 0.001f, false);
		UI.addSlider(UI_resultFlowDisplaceAmp, 0.2f, 0f, 1f, 0.001f, false);
		UI.addSlider(UI_resultFlowDisplaceIters, 1f, 0f, 10f, 1f, false);
		UI.addSlider(UI_resultBlurAmp, 20f, 0f, 100f, 0.1f, false);
		UI.addSlider(UI_resultBlurSigma, 20f, 0f, 100f, 0.1f, false);
	}
	
	// setters
	
	public void uDecayLerp(float val) { uDecayLerp = val; }
	public void uForce(float val) { uForce = val; }
	public void uOffset(float val) { uOffset = val; }
	public void uLambda(float val) { uLambda = val; }
	public void uThreshold(float val) { uThreshold = val; }
	public void uInverseX(float val) { uInverseX = val; }
	public void uInverseY(float val) { uInverseY = val; }
	public void resultFlowDecayLerp(float val) { resultFlowDecayLerp = val; }
	
	public void preBlurAmp(int val) { preBlurAmp = val; }
	public void preBlurSigma(float val) { preBlurSigma = val; }
	
	public void resultFlowDisplaceAmp(float val) { resultFlowDisplaceAmp = val; }
	public void resultFlowDisplaceIters(int val) { resultFlowDisplaceIters = val; }
	public void resultBlurAmp(int val) { resultBlurAmp = val; }
	public void resultBlurSigma(float val) { resultBlurSigma = val; }

	// get results
	
	public PGraphics resultBuffer() { 
		return resultBuffer;
	}
	
	public PGraphics resultFlowedBuffer() { 
		return resultFlowedBuffer;
	}
	
	public PGraphics debugBuffer() { 
		return debugBuffer;
	}
	
	// internal calculations
	
	public void updateOpticalFlowProps() {
		uDecayLerp(UI.value(UI_uDecayLerp));
		uForce(UI.value(UI_uForce));
		uOffset(UI.value(UI_uOffset));
		uLambda(UI.value(UI_uLambda));
		uThreshold(UI.value(UI_uThreshold));
		uInverseX(UI.value(UI_uInverseX));
		uInverseY(UI.value(UI_uInverseY));
		resultFlowDecayLerp(UI.value(UI_resultFlowDecayLerp));
				
		preBlurAmp(UI.valueInt(UI_preBlurAmp));
		preBlurSigma(UI.value(UI_preBlurSigma));
				
		resultFlowDisplaceAmp(UI.value(UI_resultFlowDisplaceAmp));
		resultFlowDisplaceIters(UI.valueInt(UI_resultFlowDisplaceIters));
		resultBlurAmp(UI.valueInt(UI_resultBlurAmp));
		resultBlurSigma(UI.value(UI_resultBlurSigma));
	}
	
	public void update(PImage newFrame, boolean flowTheResults) {
		frameCount++;
		
		// [broke...] flip flop buffer copy from source
//		PImage copyFrame = (frameCount % 2 == 1) ? tex0 : tex1;
//		ImageUtil.copyImage(newFrame, copyFrame);
		
		// flip flop would reverse at times... 
		// possible frame rate difference between camera & program. 
		// This has an extra texture copy but doesn't ever reverse.
		ImageUtil.copyImage(tex0, tex1);
		ImageUtil.copyImage(newFrame, tex0);

		
		// pre-process frames before optical flow:
		// blur for smoother optical flow results in some cases
		if(preBlurAmp > 0) {
			BlurProcessingFilter.instance(P.p).setBlurSize(preBlurAmp);
			BlurProcessingFilter.instance(P.p).setSigma(preBlurSigma);
//			BlurProcessingFilter.instance(P.p).applyTo(tex1);
//			BlurProcessingFilter.instance(P.p).applyTo(tex1);
//			BlurProcessingFilter.instance(P.p).applyTo(tex0);
//			BlurProcessingFilter.instance(P.p).applyTo(tex0);
		}
		
		// update shader & do optical flow calculations
		opFlowShader.shader().set("texFlow", resultBuffer);	// lerp from previous flow frame
		opFlowShader.shader().set("tex0", tex0);
		opFlowShader.shader().set("tex1", tex1);
		opFlowShader.shader().set("uDecayLerp", uDecayLerp);
		opFlowShader.shader().set("uForce", uForce);
		opFlowShader.shader().set("uOffset", uOffset);
		opFlowShader.shader().set("uLambda", uLambda);
		opFlowShader.shader().set("uThreshold", uThreshold);
		opFlowShader.shader().set("uInverse", uInverseX, uInverseY);
		opFlowShader.shader().set("firstFrame", frameCount < 2);
		opFlowShader.update();
		resultBuffer.filter(opFlowShader.shader());
		
		if(flowTheResults) {
//			resultFlowedBuffer.beginDraw();
//			resultFlowedBuffer.background(0);
//			resultFlowedBuffer.endDraw();
			BlendTowardsTexture.instance(P.p).setBlendLerp(resultFlowDecayLerp);
			BlendTowardsTexture.instance(P.p).setSourceTexture(resultBuffer);
			BlendTowardsTexture.instance(P.p).applyTo(resultFlowedBuffer);
//			ImageUtil.copyImage(resultBuffer, resultFlowedBuffer);
			
			// displace & blur the flow data for liquidy flow & dispersion
			DisplacementMapFilter.instance(P.p).setMap(resultFlowedBuffer);
			DisplacementMapFilter.instance(P.p).setMode(10);	// special flow mode
			DisplacementMapFilter.instance(P.p).setAmp(resultFlowDisplaceAmp);
			for (int i = 0; i < resultFlowDisplaceIters; i++) {
				DisplacementMapFilter.instance.applyTo(resultFlowedBuffer);
			}
			
			BlurProcessingFilter.instance(P.p).setBlurSize(resultBlurAmp);
			BlurProcessingFilter.instance(P.p).setSigma(resultBlurSigma);
			BlurProcessingFilter.instance(P.p).applyTo(resultFlowedBuffer);
		}
		
//		BlendTowardsTexture.instance(P.p).setBlendLerp(0.15f);
//		BlendTowardsTexture.instance(P.p).setSourceTexture(resultIntermediateFlowedBuffer);
//		BlendTowardsTexture.instance(P.p).applyTo(resultFlowedBuffer);
	}
	
	public void drawDebugLines(PImage opFlowResults) {
		// lazy build debug buffer
		if(debugBuffer == null) debugBuffer = PG.newPG(resultBuffer.width, resultBuffer.height);
		
		// debug lines to show flow
		// r, g == x, y
//		resultBuffer.loadPixels();
		opFlowResults.loadPixels();
		debugBuffer.beginDraw();
		debugBuffer.clear();
		debugBuffer.stroke(255);
		PG.setDrawCenter(debugBuffer);
		for (int x = 0; x < opFlowResults.width; x += 15) {
			for (int y = 0; y < opFlowResults.height; y += 15) {
				int pixelColor = ImageUtil.getPixelColor(opFlowResults, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
				float g = ColorUtil.greenFromColorInt(pixelColor) / 255f;
				float xDir = (r) - 0.5f;
				float yDir = (g) - 0.5f;
				if(P.abs(xDir) > 0.01f || P.abs(yDir) > 0.01f) { 
					float arrowScale = P.max(P.abs(xDir), P.abs(yDir));
					float arrowSize = 4 +  200f * arrowScale;
					debugBuffer.push();
					debugBuffer.translate(x, y);
					debugBuffer.rotate(P.PI - MathUtil.getRadiansToTarget(0, 0, xDir, yDir));
					debugBuffer.image(DemoAssets.arrow(), 0, 0, arrowSize, arrowSize);
					debugBuffer.pop();
				}
			}
		}
		debugBuffer.endDraw();
	}

	
}
