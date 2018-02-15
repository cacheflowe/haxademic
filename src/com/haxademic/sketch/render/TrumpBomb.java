package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class TrumpBomb 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PShapeSolid objSolid;
	protected PImage img;
	protected boolean useTexture = true;
	protected float modelHeight;
	protected float _frames = 300;
	protected ImageGradient flameColors;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) _frames );
	}

	public void setup() {
		super.setup();	
		p.noiseSeed(20);
	}
	
	protected void firstFrameSetup() {
		// load texture
//		img = p.loadImage(FileUtil.getFile("images/jupiter-360.jpg"));
		img = p.loadImage(FileUtil.getFile("images/aholes-trmp.jpg"));
		flameColors = new ImageGradient(p.loadImage(FileUtil.getFile("images/palettes/sparks-flames.jpg")));
		
		// build obj PShape and scale to window
//		obj = p.loadShape( FileUtil.getFile("models/poly-hole-square.obj"));	
//		obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
//		obj = p.loadShape( FileUtil.getFile("models/man-lowpoly.obj"));	
//		obj = p.loadShape( FileUtil.getFile("models/lego-man.obj"));	
		obj = p.loadShape( FileUtil.getFile("models/bomb.obj"));	
//		obj = obj.getTessellation();
//		obj = p.loadShape( FileUtil.getFile("models/poly-hole-tri.obj"));	
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, p.height);
		
		// add UV coordinates to OBJ based on model extents
		float modelExtent = PShapeUtil.getMaxExtent(obj);
		modelHeight = PShapeUtil.getMaxAbsY(obj);
		P.println("modelExtent", modelExtent);
		P.println("getObjHeight", modelHeight);
		if(useTexture) 
			PShapeUtil.addTextureUVToShape(obj, img, modelExtent, true);
		
		// build solid, deformable PShape object
		objSolid = new PShapeSolid(obj);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = percentComplete * P.TWO_PI;
		
		if(p.frameCount % 300 == 0)		img = p.loadImage(FileUtil.getFile("images/aholes-trmp.jpg"));
 
		p.pushMatrix();
		background(0);
		DrawUtil.setDrawCenter(p);
		
		// lights
//		p.lights();
		p.ambient(0);
		p.lightSpecular(30, 30, 30); 
		p.lightFalloff(1.0f, 0.001f, 0.0f);
		p.directionalLight(100, 100, 100, -0.0f, -0.0f, 1); 
		p.directionalLight(100, 100, 100, 0.0f, 0.0f, -1); 
		p.specular(p.color(100)); 
		p.shininess(0.0f); 

		
		// rotate
		p.translate(
				p.width/2f, 
				p.height* (0.48f + 0.025f * P.sin(P.PI - 0.6f + radsComplete)), 
				-width*1.5f - 50f + 50f * P.sin(P.PI + radsComplete));
		p.rotateZ(P.PI);
		p.rotateY(0.1f * P.sin(radsComplete)); // -P.HALF_PI + 
		p.rotateX(-0.15f + 0.18f * P.sin(P.HALF_PI + radsComplete));
//		p.rotateX(-P.PI/2f);

		
		// deform
//		objSolid.updateWithTrig(true, percentComplete * 2f, 0.04f, 17.4f);
//		objSolid.deformWithAudio();
//		objSolid.deformWithAudioByNormals();

		// draw mesh with texture or without
		// if a texture is set, drawing with p.shape() is super slow, so we can manually draw by looping over vertices
		// if no texture, p.shape() is fine
		p.noStroke();
		float wiggle = 4f + 2f * P.sin(radsComplete);
		p.translate(p.random(-wiggle,wiggle), p.random(-wiggle,wiggle), p.random(-wiggle,wiggle));
		// texture mapped with decent performance:
		float subFromRed = 85f + 85f * P.sin(radsComplete);
		p.tint(255, 255 - subFromRed, 255 - subFromRed);
		PShapeUtil.drawTriangles(p.g, objSolid.shape(), img, 1f); // img	
		
		// sparks
		float msX = P.map(p.mouseX, 0, p.width, -1000f, 1000f);
		float msY = P.map(p.mouseY, 0, p.height, -1000f, 1000f);
		debugView.setValue("msX", msX);
		debugView.setValue("msY", msY);
		p.pushMatrix();
		p.translate(-200, 795, 0);
		float sparkArea = 250;
		int numSparks = 100 + P.round(80f * P.sin(P.PI + P.HALF_PI - 0.8f - radsComplete));
		for (int i = 0; i < numSparks; i++) {
			p.fill(flameColors.getColorAtProgress(0.1f + 0.3f * P.sin(i + radsComplete)));
			p.tint(255, 255 - subFromRed, 255 - subFromRed);

			p.pushMatrix();
			p.translate(
					-sparkArea * 1.2f + sparkArea * 2.4f * p.noise(0,0,i + 0.4f * P.sin(i + radsComplete)), 
					-sparkArea + sparkArea * 2f * p.noise(0,i + 0.4f * P.sin(i + radsComplete),0), 
					-sparkArea + sparkArea * 2f * p.noise(i + 0.4f * P.sin(i + radsComplete),0,0)
					);
			p.rotateX(i + radsComplete);
			p.rotateY(i + radsComplete);

			if(P.round(P.sin(i + radsComplete) * 4) % 2 == 0)
				p.box(15);
			p.popMatrix();
		}
		p.popMatrix();
				
		p.popMatrix();
		
		SphereDistortionFilter.instance(p).setAmplitude(0.35f + 0.35f * P.sin(0.3f + radsComplete));
		SphereDistortionFilter.instance(p).applyTo(p);
		BrightnessFilter.instance(p).setBrightness(2.6f);
		BrightnessFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).applyTo(p);
	}
		
}