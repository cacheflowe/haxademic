package com.haxademic.demo.hardware.webcam;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;

public class Demo_FrameDifferenceShapesLauncher 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected PGraphics prevFrame;
	protected PGraphics curFrame;
	protected PGraphics differenceBuffer;
	protected PGraphics shapesLayer;
	protected PShader differenceShader;
	protected ArrayList<ShapeParticle> shapes;
	protected ImageGradient imageGradient;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3 ); // 18
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	public void setupFirstFrame () {
		// capture webcam frames
		p.webCamWrapper.setDelegate(this);
		// build particles array
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient = new ImageGradient(ImageGradient.randomCoolor());
		shapes = new ArrayList<ShapeParticle>();
	}
	
	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) {
			int cameraW = 640;	// frame.width (these are jacked up on OS X)
			int cameraH = 480;	// frame.height
			flippedCamera = p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			
			// frame buffers
			prevFrame = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			curFrame = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			differenceBuffer = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			p.debugView.setTexture(differenceBuffer);
			
			// frame diff buffer/shader
			differenceShader = P.p.loadShader(FileUtil.getFile("shaders/filters/texture-difference-threshold.glsl"));

			// shapes layer
			shapesLayer = P.p.createGraphics(cameraW, cameraH, PRenderers.P3D);
			shapesLayer.smooth(8);
		}
		// copy flipped
		flippedCamera.copy(frame, 0, 0, frame.width, frame.height, flippedCamera.width, 0, -flippedCamera.width, flippedCamera.height);
		
		// copy previous frame, and current frame to buffer
		ImageUtil.copyImage(curFrame, prevFrame);
		ImageUtil.copyImage(flippedCamera, curFrame);
		p.debugView.setTexture(curFrame);

		// set difference shader textures
		differenceShader.set("tex1", curFrame);
		differenceShader.set("tex2", prevFrame);
		differenceShader.set("falloffBW", 0.25f);
		differenceShader.set("diffThresh", 0.06f);
		differenceBuffer.filter(differenceShader);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		
		if(differenceBuffer != null) {
			// draw to screen
			DrawUtil.setDrawCenter(p);
			DrawUtil.setPImageAlpha(p, 1f);
			p.blendMode(PBlendModes.BLEND);
			p.image(curFrame, p.width/2, p.height/2);
			
			// draw shapes - find launch points
			differenceBuffer.loadPixels();
			shapesLayer.beginDraw();
			shapesLayer.clear();
			shapesLayer.fill(255);
			shapesLayer.noStroke();
			
			if(p.frameCount % 4 == 0) {
				int numLaunched = 0;
				for (int i = 0; i < 2000; i++) {
					int checkX = MathUtil.randRange(0, differenceBuffer.width);
					int checkY = MathUtil.randRange(0, differenceBuffer.height);
					int pixelColor = ImageUtil.getPixelColor(differenceBuffer, checkX, checkY);
					float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
					if(redColor > 0.5f && numLaunched < 2) {
						// shapesLayer.rect(checkX, checkY, 2, 2);	// show launchpoints
						launchShape(checkX, checkY);
						numLaunched++;
					}
				}
			}
			
			// update/draw particles
//			p.blendMode(PBlendModes.ADD);
			for (int i = 0; i < shapes.size(); i++) {
				shapes.get(i).update(shapesLayer);
			}
			shapesLayer.endDraw();
			p.debugView.setValue("shapes.size()", shapes.size());
			p.image(shapesLayer, p.width/2, p.height/2);
		}
	}
	
	protected void launchShape(float x, float y) {
		// look for an available shape
		for (int i = 0; i < shapes.size(); i++) {
			if(shapes.get(i).available()) {
				shapes.get(i).launch(x, y);
				return;
			}
		}
		// didn't find one
		if(shapes.size() < 10000) {
			ShapeParticle newShape = new ShapeParticle();
			newShape.launch(x, y);
			shapes.add(newShape);
		}
	}
	
	public class ShapeParticle {
		
		protected PVector pos = new PVector(0, -100, 0);
		protected PVector speed = new PVector(0, 0, 0);
		protected PVector gravity = new PVector(0, 0, 0);
		protected float vertices = 3f;
		protected float size = 10f;
		protected float rotation = 30f;
		protected LinearFloat sizeProgress = new LinearFloat(0, 0.04f);
		protected int color;
		
		public ShapeParticle() {
			
		}
		
		public void update(PGraphics pg) {
			if(available()) return;
			// update position
			speed.add(gravity);
			pos.add(speed);
			rotation += gravity.z;
			// update size
			sizeProgress.update();
			float curSize = size * Penner.easeOutBack(sizeProgress.value(), 0, 1, 1);
			if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
			// draw shape
			float segmentRads = P.TWO_PI / vertices;
			pg.fill(color); // , 150);
			pg.stroke(255);
			pg.pushMatrix();
			pg.translate(pos.x, pos.y);
			pg.rotate(rotation);
			pg.beginShape(P.POLYGON);
			for(float i = 0; i <= vertices; i++) {
				pg.vertex(P.cos(segmentRads * i) * curSize, P.sin(segmentRads * i) * curSize);
			}
			pg.endShape();
			pg.popMatrix();
			// pg.rect(pos.x, pos.y, 2, 2);
		}
		
		public void launch(float x, float y) {
			vertices = MathUtil.randRange(3, 6);
			size = MathUtil.randRangeDecimal(10, 20);
			sizeProgress.setCurrent(0);
			sizeProgress.setTarget(1);
			
			pos.set(x, y, 0);
			speed.set(0, -5f, 0);
			rotation = P.p.random(P.TWO_PI);
			gravity.set(MathUtil.randRangeDecimal(-0.02f, 0.02f), MathUtil.randRangeDecimal(0.2f, 0.4f), MathUtil.randRangeDecimal(-0.01f, 0.01f));
			
			color = imageGradient.getColorAtProgress(P.p.random(1f));
		}
		
		public boolean available() {
			return pos.y < -100 || pos.y > p.height + 100;
		}
	}
}
