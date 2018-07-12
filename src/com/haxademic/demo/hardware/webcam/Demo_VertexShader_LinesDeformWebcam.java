package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlendTowardsTexture;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_LinesDeformWebcam 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics webcamBuffer;
	protected PGraphics webcamLerped;

	protected PShape shape;
	protected PShader displacementShader;
	protected float shapeExtent = 100;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, 160);
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3);
	}
	
	protected void setupFirstFrame() {
		// set up webcam
		p.webCamWrapper.setDelegate(this);
		webcamBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		webcamLerped = p.createGraphics(p.width, p.height, PRenderers.P2D);

		// build sheet mesh
		shape = p.createShape(P.GROUP);
		int rows = 100;
		int cols = 200;
		for (int y = 0; y < rows; y++) {
			PShape line = P.p.createShape();
			line.beginShape();
			line.stroke(255);
			line.strokeWeight(1);
			line.noFill();
			for (int x = 0; x < cols; x++) {
				line.vertex(x * 10f, y * 10f, 0);
			}
			line.endShape();
			shape.addChild(line);
		}
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.5f);
		PShapeUtil.addTextureUVToShape(shape, webcamBuffer);
		shapeExtent = PShapeUtil.getMaxExtent(shape);

		shape.setTexture(webcamBuffer);
		p.debugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
		
		// load shader
		displacementShader = loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/line-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/line-vert.glsl")
		);
	}

	public void drawApp() {
		background(0);
		
		// rotate
		DrawUtil.setCenterScreen(p);
		DrawUtil.basicCameraFromMouse(p.g, 0.1f);

		// draw mesh
		shape.disableStyle();
		p.stroke(255);
		displacementShader.set("displacementMap", webcamLerped);
		displacementShader.set("colorMap", webcamLerped);
		displacementShader.set("weight", p.mousePercentX() * 10f);
		displacementShader.set("modelMaxExtent", shapeExtent * 2f);
		if(p.mousePercentX() > 0.5f) {
			displacementShader.set("sheet", 1);
			displacementShader.set("displaceStrength", p.mousePercentY() * pg.height * 0.7f);
			displacementShader.set("colorThickness", 1);
		} else {
			displacementShader.set("sheet", 0);
			displacementShader.set("displaceStrength", p.mousePercentY() * pg.height * 0.01f);
			displacementShader.set("colorThickness", 0);
		}
		p.shader(displacementShader, P.LINES);  
		p.shape(shape);
		p.resetShader();
		
		// post
		// FXAAFilter.instance(p).applyTo(p.g);
		// BlurBasicFilter.instance(p).applyTo(p.g);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// copy webcam and create motion detection at size of cropped webcam (and downscaling)
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		ImageUtil.flipH(webcamBuffer);
		ImageUtil.flipV(webcamBuffer);
		
//		InvertFilter.instance(p).applyTo(webcamBuffer);
		
		BlendTowardsTexture.instance(p).setSourceTexture(webcamBuffer);
		BlendTowardsTexture.instance(p).setBlendLerp(0.35f);
		BlendTowardsTexture.instance(p).applyTo(webcamLerped);
		
		BrightnessFilter.instance(p).setBrightness(2f);
		BrightnessFilter.instance(p).applyTo(webcamBuffer);
//		ThresholdFilter.instance(p).applyTo(webcamBuffer);
				
		// set textures for debug view
		p.debugView.setTexture(frame);
		p.debugView.setTexture(webcamBuffer);
	}

}