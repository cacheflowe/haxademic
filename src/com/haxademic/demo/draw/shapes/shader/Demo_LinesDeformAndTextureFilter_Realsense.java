package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessToAlphaFilter;
import com.haxademic.core.draw.filters.pshader.PoissonFill;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.LinesDeformAndTextureFilter;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_LinesDeformAndTextureFilter_Realsense
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage depthImg;
	protected PImage rgbImg;
	protected PGraphics depthCopy;
	protected PGraphics rgbCopy;
	protected PoissonFill poisson;


	protected PShape shape;
	protected PImage texture;
	protected float shapeExtent = 50;

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, 160);
	}
	
	protected void firstFrame() {
		buildCamera();

		// build sheet mesh
		shape = p.createShape(P.GROUP);
		int rows = 200;
		int cols = 500;
		for (int y = 0; y < rows; y++) {
			PShape line = P.p.createShape();
			line.beginShape();
			line.stroke(255);
			line.strokeWeight(1);
			line.noFill();
			for (int x = 0; x < cols; x++) {
				line.vertex(x, y, 0);
			}
			line.endShape();
			shape.addChild(line);
		}
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height *1.25f);
		PShapeUtil.addTextureUVToShape(shape, texture);
		shapeExtent = PShapeUtil.getMaxExtent(shape);
		shape.disableStyle();

		shape.setTexture(texture);
		DebugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
	}

	protected void buildCamera() {
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		rgbImg = depthCamera.getRgbImage();
		depthImg = depthCamera.getDepthImage();
		depthCopy = PG.newPG(depthImg.width, depthImg.height);
		rgbCopy = PG.newPG(rgbImg.width, rgbImg.height);
		poisson = new PoissonFill(depthImg.width, depthImg.height);
		DebugView.setTexture("depthImg", depthImg);
		DebugView.setTexture("depthCopy", depthCopy);
		DebugView.setTexture("rgbCopy", rgbCopy);
		DebugView.setTexture("output", poisson.output());
	}
	
	protected void updateCamera() {
		p.background(0, 0, 0);
		ImageUtil.copyImage(depthImg, depthCopy);
		BrightnessToAlphaFilter.instance().setFlip(false);
		BrightnessToAlphaFilter.instance().setSmoothstepLow(0.0f);
		BrightnessToAlphaFilter.instance().setSmoothstepHigh(0.15f);
		BrightnessToAlphaFilter.instance().applyTo(depthCopy);
		
		poisson.applyTo(depthCopy);
		
		// update RGB image
		ImageUtil.copyImage(rgbImg, rgbCopy);
	}
	


	protected void drawApp() {
		background(0);
		updateCamera();
		
		// rotate
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);

		// draw shader-displaced mesh
		LinesDeformAndTextureFilter.instance().setDisplacementMap(poisson.output());
		LinesDeformAndTextureFilter.instance().setColorMap(rgbImg);
		LinesDeformAndTextureFilter.instance().setWeight(5f);
		LinesDeformAndTextureFilter.instance().setModelMaxExtent(shapeExtent * 2f);
		LinesDeformAndTextureFilter.instance().setColorThicknessMode(true);
		LinesDeformAndTextureFilter.instance().setSheetMode(true);
		LinesDeformAndTextureFilter.instance().setDisplaceAmp(pg.height * 0.5f);
		LinesDeformAndTextureFilter.instance().setFlipY(true);
		LinesDeformAndTextureFilter.instance().setBgRemove(false);
		LinesDeformAndTextureFilter.instance().setOnContext(p);

		p.stroke(255);
		p.shape(shape);
		p.resetShader();
	}
		
}