package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PGraphics;

public class Demo_OpticalFlowVanilla
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected RealSenseWrapper realSenseWrapper;

	protected PGraphics lastFrame;
	protected PGraphics curFrame;
	protected PGraphics curRgbFrame;
	protected PGraphics opFlowResult;
	protected PGraphics camDisplaced;
	protected PShaderHotSwap opFlowShader;
	protected PShaderHotSwap displaceShader;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
	}

	protected void firstFrame() {
		// init camera
		realSenseWrapper = new RealSenseWrapper(p, true, true);
		
		// create buffers
		curFrame = PG.newPG(p.width, p.height);
		curRgbFrame = PG.newPG(p.width, p.height);
		lastFrame = PG.newPG(p.width, p.height);
		opFlowResult = PG.newPG32(p.width, p.height, false, false);		// disabling smoothing allows for per-pixel lerping w/very small values
		camDisplaced = PG.newPG32(p.width, p.height, true, false);

		DebugView.setTexture("curFrame", curFrame);
		DebugView.setTexture("opFlowResult", opFlowResult);
		DebugView.setTexture("camDisplaced", camDisplaced);
		
		// load shader
		opFlowShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/optical-flow.glsl"));
		displaceShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/displacement-map.glsl"));
		
		// init flow result to gray (resting)
		opFlowResult.beginDraw();
		opFlowResult.background(127);
		opFlowResult.endDraw();
	}

	protected void drawApp() {
		p.background(0);

		runOpticalFlow();
		flowTheFlowData();
		applyFlowToRgbCamera();
		drawToScreen();
//		drawDebugLines();
	}
	
	protected void runOpticalFlow() {
		// update camera
		realSenseWrapper.update();
		
		// copy camera frames to buffers
		ImageUtil.cropFillCopyImage(curFrame, lastFrame, true);
		ImageUtil.cropFillCopyImage(realSenseWrapper.getDepthImage(), curFrame, true);
		
		// override!
		// draw a lissajous curve
//		curFrame.beginDraw();
//		curFrame.background(0);
//		curFrame.translate(curFrame.width/2, curFrame.height/2);
//		curFrame.fill(255);
//		curFrame.ellipse(P.cos(p.frameCount * 0.03f) * 100f, P.sin(p.frameCount * 0.05f) * 100f, 100, 100);
//		curFrame.endDraw();
		
		// pre-process frames before optical flow:
		// blur for smoother optical flow results
		BlurProcessingFilter.instance(p).setBlurSize(20);
		BlurProcessingFilter.instance(p).setSigma(20f);
		BlurProcessingFilter.instance(p).applyTo(lastFrame);
		BlurProcessingFilter.instance(p).applyTo(lastFrame);
		BlurProcessingFilter.instance(p).applyTo(curFrame);
		BlurProcessingFilter.instance(p).applyTo(curFrame);
		
		// update/draw shader
		opFlowShader.update();
		opFlowShader.shader().set("texFlow", opFlowResult);	// lerp from previous flow frame
		opFlowShader.shader().set("tex0", curFrame);
		opFlowShader.shader().set("tex1", lastFrame);
		opFlowShader.shader().set("uDecayLerp", 0.02f);
		opFlowShader.shader().set("uForce", 0.75f);
		opFlowShader.shader().set("uOffset", 8f);
		opFlowShader.shader().set("uLambda", 0.012f);
		opFlowShader.shader().set("uThreshold", 0.1f);
		opFlowResult.filter(opFlowShader.shader());
	}
	
	protected void flowTheFlowData() {
		// displace & blur the flow data for liquidy flow & dispersion
//		DisplacementMapFilter.instance(p).setMap(opFlowResult);
//		DisplacementMapFilter.instance(p).setMode(10);
//		DisplacementMapFilter.instance(p).setAmp(0.16f);
//		DisplacementMapFilter.instance(p).applyTo(opFlowResult);
		displaceShader.shader().set("map", opFlowResult);
		displaceShader.shader().set("amp", Mouse.xNorm);
		displaceShader.shader().set("mode", 10);
		displaceShader.update();
		opFlowResult.filter(displaceShader.shader());

		BlurProcessingFilter.instance(p).setBlurSize(30);
		BlurProcessingFilter.instance(p).setSigma(10f);
		BlurProcessingFilter.instance(p).applyTo(opFlowResult);
	}
	
	protected void applyFlowToRgbCamera() {
		ImageUtil.cropFillCopyImage(realSenseWrapper.getRgbImage(), curRgbFrame, true);
		BlendTowardsTexture.instance(p).setSourceTexture(curRgbFrame);
		BlendTowardsTexture.instance(p).setBlendLerp(0.1f);
		BlendTowardsTexture.instance(p).applyTo(camDisplaced);
		
//		DisplacementMapFilter.instance(p).setMode(10);
//		DisplacementMapFilter.instance(p).applyTo(camDisplaced);	
		displaceShader.shader().set("map", opFlowResult);
		displaceShader.shader().set("amp", Mouse.xNorm);
		displaceShader.shader().set("mode", 10);
		camDisplaced.filter(displaceShader.shader());
	}
	
	protected void drawToScreen() {
//		p.image(opFlowResult, 0, 0);
//		p.image(curFrame, 0, 0);
//		ImageUtil.cropFillCopyImage(opFlowResult, p.g, true);	
		ImageUtil.cropFillCopyImage(camDisplaced, p.g, true);	
	}
	
	protected void drawDebugLines() {
		// debug lines to show flow
		// r, g == x, y
		opFlowResult.loadPixels();
		p.stroke(255);
		for (int x = 0; x < opFlowResult.width; x += 5) {
			for (int y = 0; y < opFlowResult.height; y += 5) {
				int pixelColor = ImageUtil.getPixelColor(opFlowResult, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
				float g = ColorUtil.greenFromColorInt(pixelColor) / 255f;
				float xDir = (r) - 0.5f;
				float yDir = (g) - 0.5f;
				if(P.abs(xDir) > 0.01f || P.abs(yDir) > 0.01f) { 
					p.pushMatrix();
					p.translate(x, y);
					if(x % 10 == 0 & y % 10 == 0) {
						p.line(0, 0, xDir * 300f, yDir * 300f);
					}
					p.popMatrix();
				}
			}
		}
	}

}
