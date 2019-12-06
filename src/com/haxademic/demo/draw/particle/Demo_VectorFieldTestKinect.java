package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.OpenKinectPixelImg;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class Demo_VectorFieldTestKinect 
extends Demo_VectorField {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics overlayKinectTexture;
	protected OpenKinectPixelImg kinectImg;
	
	protected void overridePropsFile() {
		super.overridePropsFile();
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.RETINA, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, OpenGLUtil.SMOOTH_LOW);
	}
	
	public void setupFirstFrame() {
		FIELD_SPACING = p.height / 20;
		ATTENTION_RADIUS = FIELD_SPACING;
		NUM_PARTICLES = 3000;
		DRAWS_PER_FRAME = 1;
		OVERDRAW_FADE = 25;

		overlayKinectTexture = p.createGraphics(p.width, p.height, P.P2D);
		overlayKinectTexture.noSmooth();
	}

	public void updateVectors() {
		
		if(p.frameCount == 1) kinectImg = new OpenKinectPixelImg(10, 1000, 2000);
		kinectImg.update();
		
		float[] offsetAndSize = ImageUtil.getOffsetAndSizeToCrop(overlayKinectTexture.width, overlayKinectTexture.height, kinectImg.texture.width, kinectImg.texture.height, true);
		overlayKinectTexture.beginDraw();
		overlayKinectTexture.clear();
		overlayKinectTexture.image(kinectImg.texture, offsetAndSize[0], offsetAndSize[1], offsetAndSize[2], offsetAndSize[3]);
		overlayKinectTexture.endDraw();
		
//		// debug draw kinect pixels
//		PG.setDrawCorner(p);
//		p.image(overlayKinectTexture, 0, 0);
//		p.image(kinectImg.texture, 0, 0);
		
		
		// update vectors
		
		PG.setDrawCenter(p);
		p.fill(255,0,0);
		float dotSize = 20f;
		float dotSizeHalf = dotSize/2f;

		overlayKinectTexture.loadPixels();
		
		// get center of mass
		float comX = 0;
		float comY = 0;
		float pixelCount = 0;
		for (PVector vector : _vectorField) {
			int pixelColor = ImageUtil.getPixelColor(overlayKinectTexture, (int)vector.x, (int)vector.y);
			if(pixelColor != ImageUtil.EMPTY_INT) {
				comX += vector.x;
				comY += vector.y;
				pixelCount++;
				
				p.pushMatrix();
				p.translate(vector.x, vector.y);
				p.fill(255,13);
				p.ellipse(0, 0, dotSizeHalf, dotSizeHalf);
				p.popMatrix();

			}
		}
		comX /= pixelCount;
		comY /= pixelCount;
		
		// debug draw com
//		p.fill(255, 0, 0);
//		p.pushMatrix();
//		p.translate(comX, comY);
//		p.rect(0, 0, 50, 50);
//		p.popMatrix();

		
		// update verctorfield based on kinect data
		for (PVector vector : _vectorField) {
			int pixelColor = ImageUtil.getPixelColor(overlayKinectTexture, (int)vector.x, (int)vector.y);
			if(pixelColor != ImageUtil.EMPTY_INT) {
				float targetRotation = MathUtil.getRadiansToTarget(comX, comY, vector.x, vector.y);
				vector.set(vector.x, vector.y, P.lerp(vector.z, targetRotation, 0.5f));
			}
			
			// debug draw attractors
			if(DEBUG_VECTORS == true) {
				p.fill(30,127);
				p.pushMatrix();
				p.translate(vector.x, vector.y);
				p.rotate( vector.z );	// use z for rotation!
				p.rect(0, 0, 1, 14);
				p.popMatrix();
			}
		}

		
		
	}

}