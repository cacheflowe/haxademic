package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class OpticalFlow {

	// optical flow buffers & shader
	protected PGraphics tex1;
	protected PGraphics tex0;
	protected PGraphics resultBuffer;
	protected PGraphics debugBuffer;
	protected PShader opFlowShader;
	protected int frameCount = -1;
	
	// uniforms
	protected float uDecayLerp = 0.02f;
	protected float uForce = 0.75f;
	protected float uOffset = 8f;
	protected float uLambda = 0.012f;
	protected float uThreshold = 0.1f;
	protected float uInverseX = -1f;
	protected float uInverseY = -1f;

	// more 
	protected int preBlurAmp = 20;
	protected float preBlurSigma = 20f;
	protected float resultFlowDisplaceAmp = 0.2f;
	protected int resultFlowDisplaceIters = 1;
	protected int resultBlurAmp = 20;
	protected float resultBlurSigma = 10f;
	

	public OpticalFlow(int w, int h) {
		// build buffers
		tex0 = PG.newPG(w, h);
		tex1 = PG.newPG(w, h);
		resultBuffer = PG.newPG32(w, h, false, false);		// disabling smoothing allows for per-pixel lerping w/very small values
		
		// load shader
		opFlowShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/optical-flow.glsl"));
	}
	
	// setters
	
	public void uDecayLerp(float val) { uDecayLerp = val; }
	public void uForce(float val) { uForce = val; }
	public void uOffset(float val) { uOffset = val; }
	public void uLambda(float val) { uLambda = val; }
	public void uThreshold(float val) { uThreshold = val; }
	public void uInverseX(float val) { uInverseX = val; }
	public void uInverseY(float val) { uInverseY = val; }
	
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
	
	public PGraphics debugBuffer() { 
		return debugBuffer;
	}
	
	// internal calculations
	
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
			BlurProcessingFilter.instance(P.p).applyTo(tex1);
			BlurProcessingFilter.instance(P.p).applyTo(tex1);
			BlurProcessingFilter.instance(P.p).applyTo(tex0);
			BlurProcessingFilter.instance(P.p).applyTo(tex0);
		}
		
		// update shader & do optical flow calculations
		opFlowShader.set("texFlow", resultBuffer);	// lerp from previous flow frame
		opFlowShader.set("tex0", tex0);
		opFlowShader.set("tex1", tex1);
		opFlowShader.set("uDecayLerp", uDecayLerp);
		opFlowShader.set("uForce", uForce);
		opFlowShader.set("uOffset", uOffset);
		opFlowShader.set("uLambda", uLambda);
		opFlowShader.set("uThreshold", uThreshold);
		opFlowShader.set("uInverse", uInverseX, uInverseY);
		opFlowShader.set("firstFrame", frameCount < 2);
		resultBuffer.filter(opFlowShader);
		
		if(flowTheResults) {
			// displace & blur the flow data for liquidy flow & dispersion
			DisplacementMapFilter.instance(P.p).setMap(resultBuffer);
			DisplacementMapFilter.instance(P.p).setMode(10);	// special flow mode
			DisplacementMapFilter.instance(P.p).setAmp(resultFlowDisplaceAmp);
			for (int i = 0; i < resultFlowDisplaceIters; i++) {
				DisplacementMapFilter.instance.applyTo(resultBuffer);
			}
			
			BlurProcessingFilter.instance(P.p).setBlurSize(resultBlurAmp);
			BlurProcessingFilter.instance(P.p).setSigma(resultBlurSigma);
			BlurProcessingFilter.instance(P.p).applyTo(resultBuffer);
		}
	}
	
	public void drawDebugLines() {
		// lazy build debug buffer
		if(debugBuffer == null) debugBuffer =  PG.newPG(resultBuffer.width, resultBuffer.height);
		
		// debug lines to show flow
		// r, g == x, y
		resultBuffer.loadPixels();
		debugBuffer.beginDraw();
		debugBuffer.clear();
		debugBuffer.stroke(255);
		PG.setDrawCenter(debugBuffer);
		for (int x = 0; x < resultBuffer.width; x += 15) {
			for (int y = 0; y < resultBuffer.height; y += 15) {
				int pixelColor = ImageUtil.getPixelColor(resultBuffer, x, y);
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
